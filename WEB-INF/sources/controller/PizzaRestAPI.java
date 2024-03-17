package controller;

import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.IngredientDAOJdbc;
import model.dao.PizzaDAOJdbc;
import model.pogo.PizzaGET;
import model.pogo.PizzaPOST;
import jakarta.servlet.annotation.WebServlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import model.pogo.IngredientSet;

@WebServlet("/pizzas/*")
public class PizzaRestAPI extends RestAPI {

    public static PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();

    private final String BAD_GET_REQUEST = "Requêtes accepté /pizzas, /pizzas/{id} ou /pizzas/{id}/prixfinal (id est un entier)";
    private final String BAD_POST_REQUEST = "Requêtes accepté /pizzas ou /pizzas/{id} (id est un entier)";
    private final String BAD_JSON_PIZZA_POST = "Format JSON {\"piname\":\"nom\",\"ingredients\":[1, ...],\"pipate\":\"pate\",\"pibase\":\"base\"}";
    private final String BAD_JSON_INGREDIENT_POST = "Format JSON {\"ingredients\":[1, ...]}";
    private final String BAD_DELETE_REQUEST = "Requêtes accepté /pizzas/{id} ou /pizzas/{id}/{idIngredient} (id et idIngredient sont des entiers)";
    private final String BAD_PATCH_REQUEST = "Requêtes accepté /pizzas/{id} (id est un entier)";
    private final String BAD_JSON_PIZZA_PATCH = "JSON avec au moins un des champs : {\"piname\":\"nom\",\"ingredients\":[1, ...],\"pipate\":\"pate\",\"pibase\":\"base\"}";

    private final String NOT_FOUND_PIZZA = "Il n'y a pas de pizza avec l'identifiant %d";
    private final String NOT_FOUND_INGREDIENT = "La pizza % n'a pas d'ingredient avec l'identifiant %d";
    private final String NOT_FOUND_INGREDIENTS = "Au moins un des ingredients %s n'existe pas";
    private final String CONFLICT = "Il existe déjà une pizza avec ce nom";

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

        String[] infos = info.split("/");
        if (infos.length > 3) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        try {
            Integer pino = Integer.parseInt(infos[1]);
            PizzaGET p = pizzaDAO.findById(pino);
            if (p == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, pino));
                return;
            }

            if (infos.length == 3) {
                if (infos[2].equals("prixfinal")) {
                    out.print("{\n \"prixfinal\": " + p.getPrice() + "\n}");
                    return;
                }
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
                return;
            }

            out.print(objectMapper.writeValueAsString(p));
            return;
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

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
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_POST);
            return;
        }

        if (splits.length == 2) {
            try {
                IngredientSet ingredientSet = objectMapper.readValue(data.toString(), IngredientSet.class);

                if (ingredientSet == null || ingredientSet.getIngredients().isEmpty()) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_INGREDIENT_POST);
                    return;
                }

                Integer pino = Integer.parseInt(splits[1]);
                PizzaGET pg = pizzaDAO.findById(pino);
                if (pg == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, pino));
                    return;
                }
                if (!pizzaDAO.save(pg, ingredientSet.getIngredients())) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND,
                            String.format(NOT_FOUND_INGREDIENTS, ingredientSet.getIngredients().toString()));
                    return;
                }

                pg = pizzaDAO.findById(pino);
                out.print(objectMapper.writeValueAsString(pg));
                res.setStatus(HttpServletResponse.SC_CREATED);
                return;
            } catch (NumberFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_POST_REQUEST);
                return;
            } catch (UnrecognizedPropertyException | InvalidFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_INGREDIENT_POST);
                return;
            }
        } else {
            try {
                PizzaPOST p = objectMapper.readValue(data.toString(), PizzaPOST.class);
                if (p.getPiname() == null || p.getPipate() == null || p.getPibase() == null
                        || p.getIngredients() == null
                        || p.getIngredients().isEmpty()) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_POST);
                    return;
                }
                if (!pizzaDAO.save(p)) {
                    res.sendError(HttpServletResponse.SC_CONFLICT, CONFLICT);
                    return;
                }

                out.print(objectMapper.writeValueAsString(p));
                res.setStatus(HttpServletResponse.SC_CREATED);
                return;
            } catch (UnrecognizedPropertyException | InvalidFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_POST);
                return;
            }
        }
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

        try {
            Integer pino = Integer.parseInt(splits[1]);
            PizzaGET p = pizzaDAO.findById(pino);
            if (p == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, pino));
                return;
            }

            if (splits.length == 3) {
                Integer ino = Integer.parseInt(splits[2]);
                if (!pizzaDAO.delete(p, ino)) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND,
                            String.format(NOT_FOUND_INGREDIENT, pino, ino));
                    return;
                }
                IngredientDAOJdbc ingredientDAO = new IngredientDAOJdbc();
                out.print(objectMapper.writeValueAsString(ingredientDAO.findById(ino)));
                return;
            }
            pizzaDAO.delete(p);
            out.print(objectMapper.writeValueAsString(p));
            return;
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_DELETE_REQUEST);
            return;
        }
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

        try {
            Integer pino = Integer.parseInt(splits[1]);
            PizzaGET pg = pizzaDAO.findById(pino);

            if (pg == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, pino));
                return;
            }

            PizzaPOST p = objectMapper.readValue(data.toString(), PizzaPOST.class);

            if (!pizzaDAO.update(pino, p)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND,
                        String.format(NOT_FOUND_INGREDIENTS, p.getIngredients()));
                return;
            }

            out.print(objectMapper.writeValueAsString(pizzaDAO.findById(pino)));
            return;
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        } catch (UnrecognizedPropertyException | InvalidFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_PATCH);
            return;
        }
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

        try {
            Integer pino = Integer.parseInt(splits[1]);
            PizzaGET pg = pizzaDAO.findById(pino);
            if (pg == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_PIZZA, pino));
                return;
            }

            PizzaPOST p = objectMapper.readValue(data.toString(), PizzaPOST.class);

            if (p.getPiname() == null || p.getPipate() == null || p.getPibase() == null || p.getIngredients() == null
                    || p.getIngredients().isEmpty()) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_POST);
                return;
            }

            if (!pizzaDAO.put(Integer.parseInt(splits[1]), p)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND,
                        String.format(NOT_FOUND_INGREDIENTS, p.getIngredients()));
                return;
            }

            out.print(objectMapper.writeValueAsString(pizzaDAO.findById(pino)));
            return;

        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_PATCH_REQUEST);
            return;
        } catch (UnrecognizedPropertyException | InvalidFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZA_PATCH);
            return;
        }
    }
}