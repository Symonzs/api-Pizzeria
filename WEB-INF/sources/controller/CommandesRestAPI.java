package controller;

import java.io.*;
import java.util.Set;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.CommandesDAOJdbc;
import model.dao.IngredientDAOJdbc;
import model.pogo.CommandeGET;
import model.pogo.CommandePOST;
import model.pogo.IngredientGET;
import jakarta.servlet.annotation.WebServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@WebServlet("/commandes/*")
public class CommandesRestAPI extends RestAPI {

    public static CommandesDAOJdbc commandesDAO = new CommandesDAOJdbc();

    private static final String BAD_GET_REQUEST = "La requête doit être de la forme /commandes ou /commandes/{id} ou /commandes/{id}/prixfinal (id entier)";
    private static final String BAD_POST_REQUEST = "La requête doit être de la forme /commandes ou /commandes/{id}";
    private static final String BAD_JSON_COMMANDE_POST = "Le JSON doit être de la forme {\"cname\":\"nom\",\"cdate\":\"date\",\"pizzas\":[1, ...]}";
    private static final String BAD_JSON_PIZZAS_POST = "Le JSON doit être de la forme {\"pizzas\":[1, ...]}";
    private static final String BAD_PATCH_REQUEST = "La requête doit être de la forme /commandes/{id}";
    private static final String BAD_DELETE_REQUEST = "La requête doit être de la forme /commandes/{id} ou /commandes/{id}/{id_pizza}";

    private static final String NOT_FOUND_COMMANDE = "La commande avec l'identifiant %s n'existe pas";
    private static final String NOT_FOUND_PIZZA = "La pizza avec l'identifiant %s pour la commande %s n'existe pas";
    private static final String NOT_FOUND_PIZZAS = "Au moins une des pizzas %s n'existe pas";

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
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_COMMANDE_POST);
            return;
        }

        CommandePOST cp = null;

        if (splits.length == 2) {
            System.out.println(splits);

            Set<Integer> pizzas = null;
            try {
                pizzas = objectMapper.readValue(data.toString(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, Integer.class));
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZAS_POST);
                return;
            }
            if (pizzas == null || pizzas.isEmpty()) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZAS_POST);
                return;
            }
            CommandeGET cg = null;
            try {
                cg = commandesDAO.findById(Integer.parseInt(splits[1]));
            } catch (NumberFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_POST_REQUEST);
                return;
            }
            if (cg == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_COMMANDE, splits[1]));
                return;
            }
            if (!commandesDAO.save(cg, pizzas)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND,
                        String.format(NOT_FOUND_PIZZAS, pizzas.toString()));
                return;
            }
            cp = CommandePOST.fromCommandeGET(commandesDAO.findById(Integer.parseInt(splits[1])));

        } else {
            try {
                cp = objectMapper.readValue(data.toString(), CommandePOST.class);
            } catch (UnrecognizedPropertyException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_COMMANDE_POST);
                return;
            }
            if (cp.getCname() == null || cp.getCdate() == null || cp.getPizzas() == null || cp.getPizzas().isEmpty()) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_COMMANDE_POST);
                return;
            }

            commandesDAO.save(cp);
        }

        out.print(objectMapper.writeValueAsString(cp));
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

        CommandeGET cg = null;
        try {
            cg = commandesDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_DELETE_REQUEST);
            return;
        }
        if (cg == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_COMMANDE, splits[1]));
            return;
        }

        if (splits.length == 3) {
            try {
                if (!commandesDAO.delete(cg, Integer.parseInt(splits[2]))) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND,
                            String.format(NOT_FOUND_PIZZA, splits[1], splits[2]));
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

        commandesDAO.delete(cg);
        out.print(objectMapper.writeValueAsString(cg));
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
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_COMMANDE_POST);
            return;
        }

        CommandeGET cg = null;
        try {
            cg = commandesDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        }

        if (cg == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, splits[1]));
            return;
        }

        CommandePOST cp = null;
        try {
            cp = CommandePOST.updateCommandePOST(cg, objectMapper.readValue(data.toString(), CommandePOST.class));
        } catch (UnrecognizedPropertyException | InvalidFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_COMMANDE_POST);
            return;
        }

        if (!commandesDAO.update(Integer.parseInt(splits[1]), cp)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                    String.format(NOT_FOUND_PIZZAS, cp.getPizzas()));
            return;
        }

        out.print(objectMapper.writeValueAsString(cp));
        return;
    }
}