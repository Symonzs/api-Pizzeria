package controller;

import java.io.*;
import java.util.Set;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.IngredientDAOJdbc;
import model.dao.PizzaDAOJdbc;
import model.pogo.PizzaGET;
import model.pogo.PizzaPOST;
import jakarta.servlet.annotation.WebServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import model.pogo.IngredientGET;

@WebServlet("/pizzas/*")
public class PizzaRestAPI extends RestAPI {

    public static PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();

    private final String BAD_GET_REQUEST = "La requête doit être de la forme /pizzas, /pizzas/{id} ou /pizzas/{id}/prixfinal (id étant un entier)";
    private final String BAD_POST_REQUEST = "La requête doit être de la forme /pizzas ou /pizzas/{id} (id étant un entier)";
    private final String BAD_JSON_PIZZA_POST_REQUEST = "Le JSON doit être de la forme {\"piname\":\"nom\",\"ingredients\":[1, ...],\"pipate\":\"pate\",\"pibase\":\"base\"}";
    private final String BAD_JSON_INGREDIENT_POST_REQUEST = "Le JSON doit être de la forme {\"ingredients\":[1, ...]}";
    private final String BAD_DELETE_REQUEST = "La requête doit être de la forme /pizzas/{id} ou /pizzas/{id}/{idIngredient} (id et idIngredient étant des entiers)";
    private final String BAD_PATCH_REQUEST = "La requête doit être de la forme /pizzas/{id} (id étant un entier)";
    private final String BAD_JSON_PIZZA_PATCH = "Le JSON doit contenir au moins un des champs {\"piname\":\"nom\",\"ingredients\":[1, ...],\"pipate\":\"pate\",\"pibase\":\"base\"}";

    private final String NOT_FOUND_PIZZA = "La pizza avec l'identifiant %s n'existe pas";
    private final String NOT_FOUND_INGREDIENT = "L'ingredient avec l'identifiant %s pour la pizza %s n'existe pas";
    private final String NOT_FOUND_INGREDIENTS = "Au moins un des ingredients %s n'existe pas";
    private final String CONFLICT = "Une pizza avec le même nom existe déjà";

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            doPatch(req, res);
        } else {
            super.service(req, res);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        if (info.equals("/") || info.equals("")) {
            out.print(objectMapper.writeValueAsString(pizzaDAO.findAll()));
            return;
        }

        String[] splits = info.split("/");
        if (splits.length > 3) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        PizzaGET p = null;
        try {
            p = pizzaDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }
        if (p == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, splits[1]));
            return;
        }

        if (splits.length == 3) {
            if (splits[2].equals("prixfinal")) {
                out.print("{\n \"prixfinal\": " + p.getPrice() + "\n}");
                return;
            }
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        out.print(objectMapper.writeValueAsString(p));
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
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_POST_REQUEST);
            return;
        }

        PizzaPOST p = null;

        if (splits.length == 2) {

            Set<Integer> ingredients = null;
            try {
                ingredients = objectMapper.readValue(data.toString(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, Integer.class));
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_INGREDIENT_POST_REQUEST);
                return;
            }
            if (ingredients == null || ingredients.isEmpty()) {
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
            if (!pizzaDAO.save(pg, ingredients)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND,
                        String.format(NOT_FOUND_INGREDIENTS, ingredients.toString()));
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