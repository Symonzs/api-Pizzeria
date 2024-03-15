package controller;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.CommandesDAOJdbc;
import model.pogo.CommandeGET;
import model.pogo.CommandePOST;
import model.pogo.IngredientPOST;
import jakarta.servlet.annotation.WebServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@WebServlet("/commandes/*")
public class CommandesRestAPI extends RestAPI {

    public static CommandesDAOJdbc commandesDAO = new CommandesDAOJdbc();

    private static final String BAD_GET_REQUEST = "La requête doit être de la forme /commandes ou /commandes/{id} ou /commandes/{id}/prixfinal (id entier)";
    private static final String BAD_POST_REQUEST = "La requête doit être de la forme /commandes ou /commandes/{id}";
    private static final String BAD_JSON_POST_COMMANDE_REQUEST = "Le JSON doit être de la forme {\"cname\":\"nom\",\"cdate\":\"date\",\"pizzas\":[1,2,3]}";
    private static final String BAD_JSON_POST_PIZZA_REQUEST = "Le JSON doit être de la forme {\"pizzas\":[1,2,3]}";
    private static final String BAD_DELETE_REQUEST = "La requête doit être de la forme /commandes/{id} ou /commandes/{id}/{id_pizza}";

    private static final String NOT_FOUND_COMMANDE = "La commande %s n'existe pas";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        if (info.equals("/") || info.equals("")) {
            out.print(objectMapper.writeValueAsString(commandesDAO.findAll()));
            return;
        }

        String[] splits = info.split("/");
        if (splits.length > 3) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        CommandeGET c = null;
        try {
            c = commandesDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }
        if (c == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_COMMANDE, splits[1]));
            return;
        }

        if (splits.length == 3) {
            if (splits[2].equals("prixfinal")) {
                out.print("{\n \"prixfinal\": " + c.getPrice() + "\n}");
                return;
            }
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        out.print(objectMapper.writeValueAsString(c));
        return;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        String splits[] = info.split("/");
        if (splits.length > 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_POST_REQUEST);
            return;
        }

        StringBuilder data = new StringBuilder();
        BufferedReader reader = req.getReader();

        String line;
        while ((line = reader.readLine()) != null) {
            data.append(line);
        }

        if (data.isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_POST_COMMANDE_REQUEST);
            return;
        }

        CommandePOST p = null;

        if (splits.length == 2) {
            System.out.println(splits);

            IngredientSet il = null;
            try {
                il = objectMapper.readValue(data.toString(), IngredientSet.class);
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_INGREDIENT_POST_REQUEST);
                return;
            }
            if (il.getIngredients() == null || il.getIngredients().isEmpty()) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_INGREDIENT_POST_REQUEST);
                return;
            }
            PizzaGET pg = null;
            try {
                pg = pizzaDAO.findById(Integer.parseInt(splits[1]));
            } catch (NumberFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_POST_REQUEST);
                return;
            }
            if (pg == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, splits[1]));
                return;
            }
            if (!pizzaDAO.save(pg, il.getIngredients())) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND,
                        String.format(NOT_FOUND_INGREDIENTS, il.getIngredients().toString()));
                return;
            }
            p = PizzaPOST.fromPizzaGET(pizzaDAO.findById(Integer.parseInt(splits[1])));

        } else {
            try {
                p = objectMapper.readValue(data.toString(), PizzaPOST.class);
            } catch (UnrecognizedPropertyException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_POST_REQUEST);
                return;
            }
            if (p.getPiname() == null || p.getPipate() == null || p.getPibase() == null || p.getIngredients() == null
                    || p.getIngredients().isEmpty()) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_POST_REQUEST);
                return;
            }

            if (!pizzaDAO.save(p)) {
                res.sendError(HttpServletResponse.SC_CONFLICT, CONFLICT);
                return;
            }
        }

        out.print(objectMapper.writeValueAsString(p));
        return;
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        if (info.equals("/") || info.equals("")) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_DELETE_REQUEST);
            return;
        }

        String[] splits = info.split("/");
        if (splits.length > 3) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_DELETE_REQUEST);
            return;
        }

        PizzaGET p = null;
        try {
            p = pizzaDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_DELETE_REQUEST);
            return;
        }
        if (p == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, splits[1]));
            return;
        }

        if (splits.length == 3) {
            try {
                if (!pizzaDAO.delete(p, Integer.parseInt(splits[2]))) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND,
                            String.format(NOT_FOUND_INGREDIENT, splits[1], splits[2]));
                    return;
                }
            } catch (NumberFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_DELETE_REQUEST);
                return;
            }
            IngredientDAOJdbc ingredientDAO = new IngredientDAOJdbc();
            IngredientGET i = ingredientDAO.findById(Integer.parseInt(splits[2]));
            out.print(objectMapper.writeValueAsString(i));
            return;
        }

        pizzaDAO.delete(p);
        out.print(objectMapper.writeValueAsString(p));
        return;
    }

    public void doPatch(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        if (info.equals("/") || info.equals("")) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        }

        String[] splits = info.split("/");
        if (splits.length > 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        }

        StringBuilder data = new StringBuilder();
        BufferedReader reader = req.getReader();

        String line;
        while ((line = reader.readLine()) != null) {
            data.append(line);
        }

        if (data.isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_PATCH);
            return;
        }

        PizzaGET pg = null;
        try {
            pg = pizzaDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        }

        if (pg == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, splits[1]));
            return;
        }

        PizzaPOST p = null;
        try {
            p = PizzaPOST.updatePizzaPOST(pg, objectMapper.readValue(data.toString(), PizzaPOST.class));
        } catch (UnrecognizedPropertyException | InvalidFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_PATCH);
            return;
        }

        System.out.println(p.getPiname() + " " + p.getPipate() + " " + p.getPibase() + " " + p.getIngredients());

        if (!pizzaDAO.update(Integer.parseInt(splits[1]), p)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                    String.format(NOT_FOUND_INGREDIENTS, p.getIngredients()));
            return;
        }

        out.print(objectMapper.writeValueAsString(p));
        return;
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        if (info.equals("/") || info.equals("")) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        }

        String[] splits = info.split("/");
        if (splits.length > 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        }

        StringBuilder data = new StringBuilder();
        BufferedReader reader = req.getReader();

        String line;
        while ((line = reader.readLine()) != null) {
            data.append(line);
        }

        if (data.isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_PATCH);
            return;
        }

        PizzaGET pg = null;
        try {
            pg = pizzaDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        }

        if (pg == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, splits[1]));
            return;
        }

        PizzaPOST p = null;
        try {
            p = objectMapper.readValue(data.toString(), PizzaPOST.class);
        } catch (UnrecognizedPropertyException | InvalidFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_PATCH);
            return;
        }

        if (p.getPiname() == null || p.getPipate() == null || p.getPibase() == null || p.getIngredients() == null
                || p.getIngredients().isEmpty()) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_POST_REQUEST);
            return;
        }

        if (!pizzaDAO.put(Integer.parseInt(splits[1]), p)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                    String.format(NOT_FOUND_INGREDIENTS, p.getIngredients()));
            return;
        }

        out.print(objectMapper.writeValueAsString(pg));
        return;
    }
}
}