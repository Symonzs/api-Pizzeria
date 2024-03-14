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

public class CommandesDAOJdbc {
    private DS dataSource;

    public CommandesDAOJdbc() {
        this.dataSource = new DS();
    }

    public List<CommandeGET> findAll() {
        List<CommandeGET> commandes = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();
            System.out.println(
                    "SELECT * FROM commandes ORDER BY cno");
            ResultSet rsCom = con.createStatement().executeQuery(
                    "SELECT * FROM commandes ORDER BY cno");
            while (rsCom.next()) {
                List<PizzaGET> pizzas = new ArrayList<PizzaGET>();
                PreparedStatement stmt = con.prepareStatement(
                        "SELECT pino FROM liste WHERE cno = ?");
                stmt.setInt(1, rsCom.getInt("cno"));
                ResultSet rsPiz = stmt.executeQuery();
                while (rsPiz.next()) {
                    pizzas.add(pizzaDAO.findById(rsCom.getInt("pino")));
                }
                commandes.add(new CommandeGET(rsCom.getInt("cno"), rsCom.getString("cname"), rsCom.getString("cdate"),
                        pizzas));

            }
            return commandes;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public CommandeGET findById(int cno) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM commandes WHERE cno = ?");
            stmt.setInt(1, cno);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();
                List<PizzaGET> pizzas = new ArrayList<PizzaGET>();
                PreparedStatement stmt2 = con.prepareStatement("SELECT pino FROM contient WHERE cno = ?");
                stmt2.setInt(1, rs.getInt("cno"));
                ResultSet rs2 = stmt2.executeQuery();
                while (rs2.next()) {
                    pizzas.add(pizzaDAO.findById(rs2.getInt("pino")));
                }
                return new CommandeGET(rs.getInt("cno"), rs.getString("cname"), rs.getString("cdate"), pizzas);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void delete(CommandeGET i) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}