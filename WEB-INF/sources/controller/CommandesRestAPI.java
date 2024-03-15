package controller;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.CommandesDAOJdbc;
import model.pogo.CommandeGET;
import model.pogo.IngredientPOST;
import jakarta.servlet.annotation.WebServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@WebServlet("/commandes/*")
public class CommandesRestAPI extends RestAPI {

    public static CommandesDAOJdbc commandesDAO = new CommandesDAOJdbc();

    private static final String BAD_GET_REQUEST = "La requête doit être de la forme /commandes ou /commandes/{id} ou /commandes/{id}/prixfinal (id entier)";
    private static final String BAD_POST_REQUEST = "La requête doit être de la forme /commandes avec une commande en JSON de la forme {\"cname\":\"nom\",\"cdate\":\"date\",\"pizzas\":[1,2,3]}";
    private static final String BAD_JSON_POST_REQUEST = "Le JSON doit être de la forme {\"iname\":\"nom\",\"iprice\":prix}";
    private static final String BAD_DELETE_REQUEST = "La requête doit être de la forme /ingredients/{id}";
    private static final String NOT_FOUND = "L'ingrediant avec l'identifiant %s n'existe pas";
    private static final String CONFLICT = "Un ingredient avec le même nom existe déjà";

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
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND, splits[1]));
            return;
        }

        if (splits.length == 3) {
            if (splits[2].equals("prixfinal")) {
                out.print("{\n \"prixfinal\":\"" + c.getPrice() + "\"\n}");
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

        if (!info.equals("/") && !info.equals("")) {
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
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_POST_REQUEST);
            return;
        }
        IngredientPOST i = null;
        try {
            i = objectMapper.readValue(data.toString(), IngredientPOST.class);
        } catch (UnrecognizedPropertyException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_POST_REQUEST);
            return;
        }
        if (i.getIname() == null || i.getIprice() == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_POST_REQUEST);
            return;
        }
        if (!IngredientRestAPI.ingredientDAO.save(i)) {
            res.sendError(HttpServletResponse.SC_CONFLICT, CONFLICT);
            return;
        }

        out.print(objectMapper.writeValueAsString(i));
        res.setStatus(HttpServletResponse.SC_CREATED);
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
        }

        String[] splits = info.split("/");
        if (splits.length != 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        CommandeGET i = null;
        try {
            i = commandesDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }
        if (i == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND, splits[1]));
            return;
        }

        commandesDAO.delete(i);
        out.print(objectMapper.writeValueAsString(i));
        return;
    }
}