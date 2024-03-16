package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.pogo.CommandeGET;
import model.pogo.CommandePOST;
import model.pogo.PizzaGET;

public class CommandesDAOJdbc {
    private DS dataSource;

    public CommandesDAOJdbc() {
        this.dataSource = new DS();
    }

    public List<CommandeGET> findAll() {
        List<CommandeGET> commandes = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            String selectCommandesQuery = "SELECT * FROM commandes ORDER BY cno";
            String selectPizzasQuery = "SELECT pino FROM liste WHERE cno = ?";
            System.out.println(selectCommandesQuery);
            ResultSet rsCommandes = con.createStatement().executeQuery(selectCommandesQuery);
            PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();
            while (rsCommandes.next()) {
                List<PizzaGET> pizzas = new ArrayList<PizzaGET>();
                PreparedStatement stmtPizzas = con.prepareStatement(selectPizzasQuery);
                stmtPizzas.setInt(1, rsCommandes.getInt("cno"));
                System.out.println(stmtPizzas);
                ResultSet rsPizzas = stmtPizzas.executeQuery();
                while (rsPizzas.next()) {
                    pizzas.add(pizzaDAO.findById(rsPizzas.getInt("pino")));
                }
                commandes.add(new CommandeGET(rsCommandes.getInt("cno"), rsCommandes.getString("cname"),
                        rsCommandes.getString("cdate"), pizzas));
            }
            return commandes;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public CommandeGET findById(int cno) {
        try (Connection con = dataSource.getConnection()) {
            String selectCommandeQuery = "SELECT * FROM commandes WHERE cno = ?";
            String selectPizzasQuery = "SELECT pino FROM contient WHERE cno = ?";
            PreparedStatement stmtCommande = con.prepareStatement(selectCommandeQuery);
            stmtCommande.setInt(1, cno);
            System.out.println(stmtCommande);
            ResultSet rsCommande = stmtCommande.executeQuery();
            if (rsCommande.next()) {
                PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();
                List<PizzaGET> pizzas = new ArrayList<PizzaGET>();
                PreparedStatement stmtPizzas = con.prepareStatement(selectPizzasQuery);
                stmtPizzas.setInt(1, rsCommande.getInt("cno"));
                System.out.println(stmtPizzas);
                ResultSet rsPizzas = stmtPizzas.executeQuery();
                while (rsPizzas.next()) {
                    pizzas.add(pizzaDAO.findById(rsPizzas.getInt("pino")));
                }
                return new CommandeGET(rsCommande.getInt("cno"), rsCommande.getString("cname"),
                        rsCommande.getString("cdate"), pizzas);
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

    public boolean save(CommandeGET cg, Set<Integer> pizzas) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    public boolean save(CommandePOST cp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    public boolean delete(CommandeGET cg, int int1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public boolean update(int int1, CommandePOST cp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}