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

    private static final String BAD_GET_REQUEST = "Requêtes accepté /ingredients ou /ingredients/{id} ou /ingredients/{id}/name (id est un entier)";
    private static final String BAD_POST_REQUEST = "Requêtes accepté /ingredients";
    private static final String BAD_JSON_POST_REQUEST = "Format JSON : {\"iname\":\"nom\",\"iprice\":prix}";
    private static final String BAD_DELETE_REQUEST = "Requêtes accepté /ingredients/{id} (id est un entier)";

    private static final String NOT_FOUND = "Il n'y a pas d'ingredient avec l'identifiant %d";
    private static final String CONFLICT = "Il existe déjà un ingredient avec ce nom";

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

        String[] infos = info.split("/");
        if (infos.length > 3) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }

        try {
            Integer ino = Integer.parseInt(infos[1]);
            IngredientGET i = ingredientDAO.findById(ino);
            if (i == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND, ino));
                return;
            }

            if (infos.length == 3) {
                if (infos[2].equals("name")) {
                    out.print("{\n \"iname\":\"" + i.getIname() + "\"\n}");
                    return;
                }
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
                return;
            }

            out.print(objectMapper.writeValueAsString(i));
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

        try {
            IngredientPOST i = objectMapper.readValue(data.toString(), IngredientPOST.class);

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
        } catch (UnrecognizedPropertyException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_POST_REQUEST);
            return;
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
        }

        String[] infos = info.split("/");
        if (infos.length != 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Integer ino = Integer.parseInt(infos[1]);
            IngredientGET i = ingredientDAO.findById(ino);
            if (i == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND, ino));
                return;
            }

            ingredientDAO.delete(i);
            out.print(objectMapper.writeValueAsString(i));
            return;
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_GET_REQUEST);
            return;
        }
    }
}