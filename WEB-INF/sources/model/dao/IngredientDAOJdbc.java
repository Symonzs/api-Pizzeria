package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.pogo.IngredientGET;
import model.pogo.IngredientPOST;

public class IngredientDAOJdbc {

    private DS dataSource;

    public IngredientDAOJdbc() {
        this.dataSource = new DS();
    }

    public List<IngredientGET> findAll() {
        List<IngredientGET> ingredients = new ArrayList<IngredientGET>();
        try (Connection con = dataSource.getConnection()) {
            String selectIngredientsQuery = "SELECT * FROM ingredients ORDER BY ino";
            System.out.println(selectIngredientsQuery);
            ResultSet rsIngredients = con.createStatement().executeQuery(selectIngredientsQuery);
            while (rsIngredients.next()) {
                ingredients.add(new IngredientGET(rsIngredients.getInt("ino"), rsIngredients.getString("iname"),
                        rsIngredients.getFloat("iprice")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ingredients;
    }

    public IngredientGET findById(int ino) {
        IngredientGET ingredient = null;
        try (Connection con = dataSource.getConnection()) {
            String selectIngredientQuery = "SELECT * FROM ingredients WHERE ino = ?";
            PreparedStatement stmt = con.prepareStatement(selectIngredientQuery);
            stmt.setInt(1, ino);
            System.out.println(stmt);
            ResultSet rsIngredient = stmt.executeQuery();
            if (rsIngredient.next()) {
                ingredient = new IngredientGET(rsIngredient.getInt("ino"), rsIngredient.getString("iname"),
                        rsIngredient.getFloat("iprice"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ingredient;
    }

    public boolean save(IngredientPOST ingredient) {
        try (Connection con = dataSource.getConnection()) {
            String selectIngredientsQuery = "SELECT ino FROM ingredients ORDER BY ino";
            String insertIngredientQuery = "INSERT INTO ingredients (ino, iname, iprice) VALUES (?, ?, ?)";
            ResultSet rsIngredients = con.createStatement().executeQuery(selectIngredientsQuery);
            int previousIno = 0;
            while (rsIngredients.next()) {
                if (rsIngredients.getInt("ino") == previousIno + 1)
                    previousIno = rsIngredients.getInt("ino");
            }
            PreparedStatement stmt = con.prepareStatement(insertIngredientQuery);
            stmt.setInt(1, previousIno + 1);
            stmt.setString(2, ingredient.getIname());
            stmt.setFloat(3, ingredient.getIprice());
            System.out.println(stmt);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean delete(IngredientGET ingredient) {
        try (Connection con = dataSource.getConnection()) {
            String deleteIngredientQuery = "DELETE FROM ingredients WHERE ino = ?";
            PreparedStatement stmt = con.prepareStatement(deleteIngredientQuery);
            stmt.setInt(1, ingredient.getIno());
            System.out.println(stmt);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}