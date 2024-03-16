package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.pogo.CommandeGET;
import model.pogo.CommandeLigneGET;
import model.pogo.CommandeLignePOST;
import model.pogo.CommandePOST;

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
                List<CommandeLigneGET> pizzas = new ArrayList<CommandeLigneGET>();
                PreparedStatement stmtPizzas = con.prepareStatement(selectPizzasQuery);
                stmtPizzas.setInt(1, rsCommandes.getInt("cno"));
                System.out.println(stmtPizzas);
                ResultSet rsPizzas = stmtPizzas.executeQuery();
                while (rsPizzas.next()) {
                    pizzas.add(new CommandeLigneGET(rsPizzas.getInt("pqte"),
                            pizzaDAO.findById(rsPizzas.getInt("pino"))));
                }
                commandes.add(new CommandeGET(rsCommandes.getInt("cno"), rsCommandes.getString("cname"),
                        new Date(rsCommandes.getLong("cdate")), pizzas));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return commandes;
    }

    public CommandeGET findById(int cno) {
        try (Connection con = dataSource.getConnection()) {
            String selectCommandeQuery = "SELECT * FROM commandes WHERE cno = ?";
            String selectPizzasQuery = "SELECT pino FROM liste WHERE cno = ?";
            PreparedStatement stmtCommande = con.prepareStatement(selectCommandeQuery);
            stmtCommande.setInt(1, cno);
            System.out.println(stmtCommande);
            ResultSet rsCommande = stmtCommande.executeQuery();
            if (rsCommande.next()) {
                PizzaDAOJdbc pizzaDAO = new PizzaDAOJdbc();
                List<CommandeLigneGET> pizzas = new ArrayList<CommandeLigneGET>();
                PreparedStatement stmtPizzas = con.prepareStatement(selectPizzasQuery);
                stmtPizzas.setInt(1, rsCommande.getInt("cno"));
                System.out.println(stmtPizzas);
                ResultSet rsPizzas = stmtPizzas.executeQuery();
                while (rsPizzas.next()) {
                    pizzas.add(new CommandeLigneGET(rsPizzas.getInt("pqte"),
                            pizzaDAO.findById(rsPizzas.getInt("pino"))));
                }
                return new CommandeGET(rsCommande.getInt("cno"), rsCommande.getString("cname"),
                        new Date(rsCommande.getLong("cdate")), pizzas);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean save(CommandePOST cp) {
        try (Connection con = dataSource.getConnection()) {
            String selectCommandesQuery = "SELECT cno FROM commandes ORDER BY cno";
            String insertCommandeQuery = "INSERT INTO commandes (cno, cname, cdate) VALUES (?, ?, ?)";
            String insertListeQuery = "INSERT INTO liste (cno, pino, pqte) VALUES (?, ?, ?)";
            ResultSet rsCommandes = con.createStatement().executeQuery(selectCommandesQuery);
            int previousCno = 0;
            while (rsCommandes.next()) {
                if (rsCommandes.getInt("cno") == previousCno + 1)
                    previousCno = rsCommandes.getInt("cno");
            }
            PreparedStatement stmtCommande = con.prepareStatement(insertCommandeQuery);
            stmtCommande.setInt(1, previousCno + 1);
            stmtCommande.setString(2, cp.getCname());
            stmtCommande.setLong(3, new Date().getTime());
            System.out.println(stmtCommande);
            stmtCommande.executeUpdate();
            PreparedStatement stmtListe = con.prepareStatement(insertListeQuery);
            for (CommandeLignePOST cl : cp.getPizzas()) {
                stmtListe.setInt(1, previousCno + 1);
                stmtListe.setInt(2, cl.getPino());
                stmtListe.setInt(3, cl.getPqte());
                System.out.println(stmtListe);
                stmtListe.addBatch();
            }
            stmtListe.executeBatch();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean save(CommandeGET cg, List<CommandeLignePOST> pizzas) {
        try (Connection con = dataSource.getConnection()) {
            String insertListeQuery = "INSERT INTO liste (cno, pino, pqte) VALUES (?, ?, ?)";
            PreparedStatement stmtListe = con.prepareStatement(insertListeQuery);
            for (CommandeLignePOST cl : pizzas) {
                stmtListe.setInt(1, cg.getCno());
                stmtListe.setInt(2, cl.getPino());
                stmtListe.setInt(3, cl.getPqte());
                System.out.println(stmtListe);
                stmtListe.addBatch();
            }
            stmtListe.executeBatch();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public void delete(CommandeGET i) {
        try (Connection con = dataSource.getConnection()) {
            String deleteCommandeQuery = "DELETE FROM commandes WHERE cno = ?";
            PreparedStatement stmtCommande = con.prepareStatement(deleteCommandeQuery);
            stmtCommande.setInt(1, i.getCno());
            System.out.println(stmtCommande);
            stmtCommande.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean delete(CommandeGET cg, int pino) {
        try (Connection con = dataSource.getConnection()) {
            String updateListeQuery = "UPDATE liste SET pqte = pqte - 1 WHERE cno = ? AND pino = ?";
            String selectListeQuery = "SELECT pqte FROM liste WHERE cno = ? AND pino = ?";
            PreparedStatement stmtListe = con.prepareStatement(updateListeQuery);
            stmtListe.setInt(1, cg.getCno());
            stmtListe.setInt(2, pino);
            System.out.println(stmtListe);
            stmtListe.executeUpdate();
            PreparedStatement stmtSelectListe = con.prepareStatement(selectListeQuery);
            stmtSelectListe.setInt(1, cg.getCno());
            stmtSelectListe.setInt(2, pino);
            System.out.println(stmtSelectListe);
            ResultSet rsListe = stmtSelectListe.executeQuery();
            if (rsListe.next() && rsListe.getInt("pqte") == 0) {
                String deleteListeQuery = "DELETE FROM liste WHERE cno = ? AND pino = ?";
                PreparedStatement stmtDeleteListe = con.prepareStatement(deleteListeQuery);
                stmtDeleteListe.setInt(1, cg.getCno());
                stmtDeleteListe.setInt(2, pino);
                System.out.println(stmtDeleteListe);
                stmtDeleteListe.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}