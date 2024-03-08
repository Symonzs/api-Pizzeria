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
            String query = "SELECT * FROM ingredients ORDER BY ino";
            System.out.println(query);
            ResultSet rs = con.createStatement().executeQuery(query);
            while (rs.next()) {
                ingredients.add(new IngredientGET(rs.getInt("ino"), rs.getString("iname"), rs.getFloat("iprice")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ingredients;
    }

    public IngredientGET findById(int ino) {
        IngredientGET ingredient = null;
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM ingredients WHERE ino = ?");
            stmt.setInt(1, ino);
            System.out.println(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ingredient = new IngredientGET(rs.getInt("ino"), rs.getString("iname"), rs.getFloat("iprice"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ingredient;
    }

    public boolean save(IngredientPOST ingredient) {
        try (Connection con = dataSource.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT ino FROM ingredients ORDER BY ino");
            int previousIno = 0;
            while (rs.next()) {
                if (rs.getInt("ino") == previousIno + 1)
                    previousIno = rs.getInt("ino");
            }
            PreparedStatement stmt = con
                    .prepareStatement("INSERT INTO ingredients (ino, iname, iprice) VALUES (?, ?, ?)");
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
            PreparedStatement stmt = con.prepareStatement("DELETE FROM ingredients WHERE ino = ?");
            stmt.setInt(1, ingredient.getIno());
            System.out.println(stmt);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}