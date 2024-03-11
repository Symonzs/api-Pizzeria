package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.pogo.PizzaGET;
import model.pogo.PizzaPOST;
import model.pogo.IngredientGET;

public class PizzaDAOJdbc {

    private DS dataSource;

    public PizzaDAOJdbc() {
        dataSource = new DS();
    }

    public List<PizzaGET> findAll() {
        List<PizzaGET> pizzas = new ArrayList<PizzaGET>();
        try (Connection con = dataSource.getConnection()) {
            IngredientDAOJdbc ingredientDAO = new IngredientDAOJdbc();
            System.out.println(
                    "SELECT * FROM pizzas ORDER BY pino");
            ResultSet rsPizzas = con.createStatement().executeQuery(
                    "SELECT * FROM pizzas ORDER BY pino");
            while (rsPizzas.next()) {
                List<IngredientGET> ingredients = new ArrayList<IngredientGET>();
                PreparedStatement stmt = con.prepareStatement(
                        "SELECT ino FROM contient WHERE pino = ?");
                stmt.setInt(1, rsPizzas.getInt("pino"));
                ResultSet rsIngredients = stmt.executeQuery();
                while (rsIngredients.next()) {
                    ingredients
                            .add(ingredientDAO.findById(rsIngredients.getInt("ino")));
                }
                pizzas.add(new PizzaGET(rsPizzas.getInt("pino"), rsPizzas.getString("piname"), ingredients,
                        rsPizzas.getString("pipate"), rsPizzas.getString("pibase")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return pizzas;
    }

    public PizzaGET findById(int pino) {
        PizzaGET pizza = null;
        try (Connection con = dataSource.getConnection()) {
            IngredientDAOJdbc ingredientDAO = new IngredientDAOJdbc();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM pizzas WHERE pino = ?");
            stmt.setInt(1, pino);
            System.out.println(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                List<IngredientGET> ingredients = new ArrayList<IngredientGET>();
                PreparedStatement stmt2 = con.prepareStatement("SELECT ino FROM contient WHERE pino = ? ORDER BY ino");
                stmt2.setInt(1, rs.getInt("pino"));
                System.out.println(stmt2);
                ResultSet rsIngredients = stmt2.executeQuery();
                while (rsIngredients.next()) {
                    ingredients.add(ingredientDAO.findById(rsIngredients.getInt("ino")));
                }
                pizza = new PizzaGET(rs.getInt("pino"), rs.getString("piname"), ingredients, rs.getString("pipate"),
                        rs.getString("pibase"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return pizza;
    }

    public boolean save(PizzaPOST pizza) {
        try (Connection con = dataSource.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT pino FROM pizzas ORDER BY pino");
            int previousPino = 0;
            while (rs.next()) {
                if (rs.getInt("pino") == previousPino + 1)
                    previousPino = rs.getInt("pino");
            }
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO pizzas (pino, piname, pipate, pibase) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, previousPino + 1);
            stmt.setString(2, pizza.getPiname());
            stmt.setString(3, pizza.getPipate());
            stmt.setString(4, pizza.getPibase());
            System.out.println(stmt);
            stmt.executeUpdate();
            PreparedStatement stmt2 = con.prepareStatement("INSERT INTO contient (pino, ino) VALUES (?, ?)");
            for (int ino : pizza.getIngredients()) {
                stmt2.clearParameters();
                stmt2.setInt(1, previousPino + 1);
                stmt2.setInt(2, ino);
                System.out.println(stmt2);
                stmt2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean save(PizzaGET pizza, Set<Integer> ingredients) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO contient (pino, ino) VALUES (?, ?)");
            for (IngredientGET ingredient : pizza.getIngredients()) {
                while (ingredients.contains(ingredient.getIno())) {
                    ingredients.remove(ingredient.getIno());
                }
            }
            for (int ino : ingredients) {
                stmt.clearParameters();
                stmt.setInt(1, pizza.getPino());
                stmt.setInt(2, ino);
                System.out.println(stmt);
                stmt.addBatch();
            }
            return stmt.executeBatch().length == ingredients.size();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean delete(PizzaGET pizza) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement stmt2 = con.prepareStatement("DELETE FROM pizzas WHERE pino = ?");
            stmt2.setInt(1, pizza.getPino());
            System.out.println(stmt2);
            stmt2.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean delete(PizzaGET pizza, int ino) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM contient WHERE pino = ? AND ino = ?");
            stmt.setInt(1, pizza.getPino());
            stmt.setInt(2, ino);
            System.out.println(stmt);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean update(int pino, PizzaPOST pizza) {
        try (Connection con = dataSource.getConnection()) {
            if (pizza.getPiname() != null) {
                PreparedStatement stmt = con.prepareStatement("UPDATE pizzas SET piname = ? WHERE pino = ?");
                stmt.setString(1, pizza.getPiname());
                stmt.setInt(2, pino);
                System.out.println(stmt);
                stmt.executeUpdate();
            }
            if (pizza.getPipate() != null) {
                PreparedStatement stmt = con.prepareStatement("UPDATE pizzas SET pipate = ? WHERE pino = ?");
                stmt.setString(1, pizza.getPipate());
                stmt.setInt(2, pino);
                System.out.println(stmt);
                stmt.executeUpdate();
            }
            if (pizza.getPibase() != null) {
                PreparedStatement stmt = con.prepareStatement("UPDATE pizzas SET pibase = ? WHERE pino = ?");
                stmt.setString(1, pizza.getPibase());
                stmt.setInt(2, pino);
                System.out.println(stmt);
                stmt.executeUpdate();
            }
            if (pizza.getIngredients() != null) {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM contient WHERE pino = ?");
                stmt.setInt(1, pino);
                System.out.println(stmt);
                stmt.executeUpdate();
                stmt = con.prepareStatement("INSERT INTO contient (pino, ino) VALUES (?, ?)");
                for (int ino : pizza.getIngredients()) {
                    stmt.clearParameters();
                    stmt.setInt(1, pino);
                    stmt.setInt(2, ino);
                    System.out.println(stmt);
                    stmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}