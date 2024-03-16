package controller;

import java.io.*;
import java.util.List;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import model.dao.CommandesDAOJdbc;
import model.dao.PizzaDAOJdbc;
import model.pogo.CommandeGET;
import model.pogo.CommandeLignePOST;
import model.pogo.CommandePOST;
import jakarta.servlet.annotation.WebServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@WebServlet("/commandes/*")
public class CommandesRestAPI extends RestAPI {

    public static CommandesDAOJdbc commandesDAO = new CommandesDAOJdbc();

    private static final String BAD_GET_REQUEST = "Requêtes accepté /commandes ou /commandes/{id} ou /commandes/{id}/prixfinal (id est un entier)";
    private static final String BAD_POST_REQUEST = "Requêtes accepté /commandes ou /commandes/{id}";
    private static final String BAD_JSON_COMMANDE_POST = "Format JSON : {\"cname\":\"nom\",\"cdate\":\"date\",\"pizzas\":[{\"pqte\":1,\"pino\":1}, ...]} (pqte est un entier > 0, pino est un entier)";
    private static final String BAD_JSON_PIZZAS_POST = "Format JSON : [{\"pqte\":1,\"pino\":1}, ...] (pqte est un entier > 0, pino est un entier)";
    private static final String BAD_DELETE_REQUEST = "Requêtes accepté /commandes/{id} ou /commandes/{id}/{id_pizza}";

    private static final String NOT_FOUND_COMMANDE = "Il n'existe pas de commande avec l'identifiant %s";
    private static final String NOT_FOUND_PIZZA = "La pizza %s n'existe pas dans la commande %s";
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

        try {
            Integer cno = Integer.parseInt(splits[1]);
            CommandeGET c = commandesDAO.findById(cno);
            if (c == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_COMMANDE, cno));
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

        if (splits.length == 2) {
            try {
                List<CommandeLignePOST> pizzas = objectMapper.readValue(data.toString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, CommandeLignePOST.class));
                if (pizzas == null || pizzas.isEmpty()) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZAS_POST);
                    return;
                }
                for (CommandeLignePOST cl : pizzas) {
                    if (cl.getPino() == null || cl.getPqte() == null || cl.getPqte() <= 0) {
                        res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZAS_POST);
                        return;
                    }
                }
                Integer cno = Integer.parseInt(splits[1]);
                CommandeGET cg = commandesDAO.findById(cno);
                if (cg == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_COMMANDE, cno));
                    return;
                }
                if (!commandesDAO.save(cg, pizzas)) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND,
                            String.format(NOT_FOUND_PIZZAS, pizzas.toString()));
                    return;
                }
                out.print(objectMapper.writeValueAsString(commandesDAO.findById(cno)));
                return;
            } catch (NumberFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_POST_REQUEST);
                return;
            } catch (UnrecognizedPropertyException | InvalidFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_PIZZAS_POST);
                return;
            }
        } else {
            try {
                CommandePOST cp = objectMapper.readValue(data.toString(), CommandePOST.class);
                if (cp.getCname() == null || cp.getPizzas() == null
                        || cp.getPizzas().isEmpty()) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_COMMANDE_POST);
                    return;
                }
                if (!commandesDAO.save(cp)) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND,
                            String.format(NOT_FOUND_PIZZAS, cp.getPizzas()));
                    return;
                }

                out.print(objectMapper.writeValueAsString(cp));
                return;
            } catch (UnrecognizedPropertyException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_JSON_COMMANDE_POST);
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
            Integer cno = Integer.parseInt(splits[1]);
            CommandeGET cg = commandesDAO.findById(cno);
            if (cg == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(NOT_FOUND_COMMANDE, cno));
                return;
            }
            if (splits.length == 3) {
                Integer pino = Integer.parseInt(splits[2]);
                if (!commandesDAO.delete(cg, pino)) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND,
                            String.format(NOT_FOUND_PIZZA, pino, cno));
                    return;
                }
                PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();
                out.print(objectMapper.writeValueAsString(pizzaDAO.findById(pino)));
                return;
            }
            commandesDAO.delete(cg);
            out.print(objectMapper.writeValueAsString(cg));
            return;
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, BAD_DELETE_REQUEST);
            return;
        }
    }
}