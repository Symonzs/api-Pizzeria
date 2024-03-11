package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.pogo.CommandeGET;
import model.pogo.IngredientGET;
import model.pogo.PizzaGET;

public class CommandeDAOJdbc {
    private DS dataSource;

    public CommandeDAOJdbc() {
        this.dataSource = new DS();
    }

        public List<CommandeGET> findAll() {
        List<CommandeGET> commandes = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();
            System.out.println(
                    "SELECT * FROM pizzas ORDER BY pino");
            ResultSet rsCom = con.createStatement().executeQuery(
                    "SELECT * FROM commandes ORDER BY cno");
            while (rsCom.next()) {
                List<PizzaGET> pizzas = new ArrayList<PizzaGET>();
                PreparedStatement stmt = con.prepareStatement(
                        "SELECT pino FROM contient WHERE cno = ?");
                stmt.setInt(1, rsCom.getInt("pino"));
                ResultSet rsPiz = stmt.executeQuery();
                while (rsPiz.next()) {
                    pizzas
                            .add(pizzaDAO.findById(rsCom.getInt("pino")));

                }
                commandes.add(new CommandeGET(rsCom.getInt("cno"), rsCom.getString("cname"), rsCom.getString("cdate"), pizzas));
                
            }
            return commandes;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    
}