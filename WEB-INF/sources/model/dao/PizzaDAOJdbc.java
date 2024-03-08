package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.pogo.PizzaGET;
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

    public IngredientGET findById(int int1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    public boolean deleteAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
    }

    public boolean delete(IngredientGET i) {
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