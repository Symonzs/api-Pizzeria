package controller;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.IngredientDAOJdbc;
import model.pogo.IngredientGET;
import model.pogo.IngredientPOST;
import jakarta.servlet.annotation.WebServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@WebServlet("/ingredients/*")
public class IngredientRestAPI extends RestAPI {

    public static IngredientDAOJdbc ingredientDAO = new IngredientDAOJdbc();

    private static final String BAD_GET_REQUEST = "La requête doit être de la forme /ingredients ou /ingredients/{id} ou /ingredients/{id}/name (id entier)";
    private static final String BAD_POST_REQUEST = "La requête doit être de la forme /ingredients avec un ingredient en JSON de la forme {\"iname\":\"nom\",\"iprice\":prix}";
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
            out.print(objectMapper.writeValueAsString(ingredientDAO.findAll()));
            return;
        }

        String[] splits = info.split("/");
        if (splits.length > 3) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        IngredientGET i = null;
        try {
            i = ingredientDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        if (i == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND, splits[1]));
            return;
        }

        if (splits.length == 3) {
            if (splits[2].equals("name")) {
                out.print("{\n \"iname\":\"" + i.getIname() + "\"\n}");
                return;
            }
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        out.print(objectMapper.writeValueAsString(i));
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

        IngredientPOST i = null;
        try {
            i = objectMapper.readValue(data.toString(), IngredientPOST.class);
        } catch (UnrecognizedPropertyException e) {
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

        IngredientGET i = null;
        try {
            i = ingredientDAO.findById(Integer.parseInt(splits[1]));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        if (i == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND, splits[1]));
            return;
        }
        ingredientDAO.delete(i);
        out.print(objectMapper.writeValueAsString(i));
        return;
    }
}