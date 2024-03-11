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
                PreparedStatement stmt2 = con.prepareStatement("SELECT ino FROM contient WHERE pino = ?");
                stmt2.setInt(1, rs.getInt("pino"));
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
            return stmt.executeUpdate() == 1;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
    }

    public boolean delete(PizzaGET pizza, int ino) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /*
     * public PizzaGET findById(int ino) {
     * PizzaGET ingredient = null;
     * try (Connection con = ds.getConnection()) {
     * PreparedStatement stmt =
     * con.prepareStatement("SELECT * FROM ingredients WHERE ino = ?");
     * stmt.setInt(1, ino);
     * System.out.println(stmt);
     * ResultSet rs = stmt.executeQuery();
     * if (rs.next()) {
     * ingredient = new PizzaGET(rs.getInt("ino"), rs.getString("iname"),
     * rs.getFloat("iprice"));
     * }
     * } catch (SQLException e) {
     * System.err.println(e.getMessage());
     * }
     * return ingredient;
     * }
     * 
     * public boolean save(IngredientPOST ingredient) {
     * try (Connection con = ds.getConnection()) {
     * ResultSet rs =
     * con.createStatement().executeQuery("SELECT ino FROM ingredients ORDER BY ino"
     * );
     * int previousIno = 0;
     * while (rs.next()) {
     * if (rs.getInt(1) != previousIno + 1) {
     * IngredientPOST.EMPTY_ROWS.add(previousIno + 1);
     * }
     * previousIno = rs.getInt(1);
     * IngredientPOST.COUNTER = previousIno + 1;
     * }
     * PreparedStatement stmt = con
     * .prepareStatement("INSERT INTO ingredients (ino, iname, iprice) VALUES (?, ?, ?)"
     * );
     * if (IngredientPOST.EMPTY_ROWS.isEmpty()) {
     * stmt.setInt(1, IngredientPOST.COUNTER);
     * } else {
     * stmt.setInt(1, IngredientPOST.EMPTY_ROWS.remove(0));
     * }
     * stmt.setString(2, ingredient.getIname());
     * stmt.setFloat(3, ingredient.getIprice());
     * System.out.println(stmt);
     * return stmt.executeUpdate() == 1;
     * } catch (SQLException e) {
     * System.err.println(e.getMessage());
     * }
     * return false;
     * }
     * 
     * public boolean delete(IngredientGET ingredient) {
     * try (Connection con = ds.getConnection()) {
     * PreparedStatement stmt =
     * con.prepareStatement("DELETE FROM ingredients WHERE ino = ?");
     * stmt.setInt(1, ingredient.getIno());
     * System.out.println(stmt);
     * return stmt.executeUpdate() == 1;
     * } catch (SQLException e) {
     * System.err.println(e.getMessage());
     * }
     * IngredientPOST.EMPTY_ROWS.add(ingredient.getIno());
     * return false;
     * }
     * 
     * public boolean deleteAll() {
     * try (Connection con = ds.getConnection()) {
     * System.out.println("TRUNCATE TABLE ingredients CASCADE");
     * return
     * con.createStatement().executeUpdate("TRUNCATE TABLE ingredients CASCADE") ==
     * 0;
     * } catch (SQLException e) {
     * System.err.println(e.getMessage());
     * }
     * IngredientPOST.COUNTER = 1;
     * return false;
     * }
     */
}