package controller;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.PizzaDAOJdbc;
import model.pogo.IngredientSet;
import model.pogo.PizzaGET;
import model.pogo.PizzaPOST;
import jakarta.servlet.annotation.WebServlet;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/pizzas/*")
public class PizzaRestAPI extends RestAPI {

    public static PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();

    private final String BAD_GET_REQUEST = "La requête doit être de la forme /pizzas, /pizzas/{id} ou /pizzas/{id}/prixfinal (id étant un entier)";
    private final String BAD_POST_REQUEST = "La requête doit être de la forme :\n- /pizzas avec un JSON de la forme"
            + "{\"piname\":\"nom\",\"ingredients\":[1, ...],\"pipate\":\"pate\",\"pibase\":\"base\"}"
            + "\n- /pizzas/{id} (id étant un entier) avec un JSON de la forme {\"ingredients\":[1, ...]}";
    private final String BAD_DELETE_REQUEST = "La requête doit être de la forme /pizzas/{id} ou /pizzas/{id}/{idIngredient} (id et idIngredient étant des entiers)";
    private final String BAD_PATCH_REQUEST = "La requête doit être de la forme /pizzas/{id} (id étant un entier) avec un JSON contenant au moins un des champs {\"piname\":\"nom\",\"ingredients\":[1, ...],\"pipate\":\"pate\",\"pibase\":\"base\"}";
    private final String NOT_FOUND_PIZZA = "La pizza avec l'identifiant %s n'existe pas";
    private final String NOT_FOUND_INGREDIENT = "La pizza avec l'identifiant %s ne contient pas l'ingrédient avec l'identifiant %s";
    private final String CONFLICT = "Une pizza avec le même nom existe déjà";

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

        PizzaPOST p = null;

        if (splits.length == 2) {
            IngredientSet il = objectMapper.readValue(data.toString(), IngredientSet.class);
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
            pizzaDAO.save(pg, il.getIngredients());
            p = objectMapper.convertValue(pizzaDAO.findById(pg.getPino()), PizzaPOST.class);
        } else {
            p = objectMapper.readValue(data.toString(), PizzaPOST.class);
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
            if (!pizzaDAO.delete(p, Integer.parseInt(splits[2]))) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND,
                        String.format(NOT_FOUND_INGREDIENT, splits[1], splits[2]));
                return;
            }
            out.print(objectMapper.writeValueAsString(p));
            return;
        }

        pizzaDAO.delete(p);
        out.print(objectMapper.writeValueAsString(p));
        return;
    }
}