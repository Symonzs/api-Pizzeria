package controller;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.PizzaDAOJdbc;
import model.pogo.IngredientGET;
import model.pogo.IngredientPOST;
import model.pogo.PizzaGET;
import jakarta.servlet.annotation.WebServlet;

import java.util.Collection;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/pizzas/*")
public class PizzaRestAPI extends RestAPI {

    private static final Logger logger = Logger.getLogger(PizzaRestAPI.class.getName());

    public static PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        logger.info("GET /pizzas" + info);
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        if (info.equals("/") || info.equals("")) {
            Collection<PizzaGET> l = pizzaDAO.findAll();
            out.print(objectMapper.writeValueAsString(l));
            return;
        }

        String[] splits = info.split("/");
        if (splits.length > 3) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String id = splits[1];
        IngredientGET i = pizzaDAO.findById(Integer.parseInt(id));
        if (i == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (splits.length == 3) {
            if (splits[2].equals("name")) {
                out.print("{\n \"iname\":\"" + i.getIname() + "\"\n}");
                return;
            }
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        out.print(objectMapper.writeValueAsString(i));
        return;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        logger.info("POST /ingredients" + info);
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder data = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            data.append(line);
        }
        IngredientPOST i = objectMapper.readValue(data.toString(), IngredientPOST.class);
        if (!IngredientRestAPI.ingredientDAO.save(i)) {
            res.sendError(HttpServletResponse.SC_CONFLICT);
            return;
        }
        out.print(objectMapper.writeValueAsString(i));
        return;
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String info = req.getPathInfo() == null ? "" : req.getPathInfo();
        logger.info("DELETE /ingredients" + info);
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        if (info.equals("/") || info.equals("")) {
            if (!pizzaDAO.deleteAll()) {
                res.sendError(HttpServletResponse.SC_CONFLICT);
                return;
            }
            out.print(objectMapper.writeValueAsString("All ingredients deleted"));
            return;
        }

        String[] splits = info.split("/");
        if (splits.length != 2) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String id = splits[1];
        IngredientGET i = pizzaDAO.findById(Integer.parseInt(id));
        if (i == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!pizzaDAO.delete(i)) {
            res.sendError(HttpServletResponse.SC_CONFLICT);
            return;
        }
        out.print(objectMapper.writeValueAsString(i));
        return;
    }
}