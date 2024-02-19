package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.pogo.Ingredient;

public class IngredientDAOJdbc{

    private DS ds;

    public IngredientDAOJdbc() {
        ds = new DS();
    }

    
    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        try (Connection con = ds.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ingredients");
            while (rs.next()) {
                ingredients.add(new Ingredient(rs.getInt("ino"), rs.getString("name"), rs.getFloat("price")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ingredients;
    }

    
    public Ingredient findById(int ino) {
        Ingredient ingredient = null;
        try (Connection con = ds.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM ingredients WHERE ino = ?");
            stmt.setInt(1, ino);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ingredient = new Ingredient(rs.getInt("ino"), rs.getString("name"), rs.getFloat("price"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ingredient;
    }

    
    public boolean save(Ingredient ingredient) {
        try (Connection con = ds.getConnection()) {
            PreparedStatement stmt = con
                    .prepareStatement("INSERT INTO ingredients (ino, name, price) VALUES (?, ?, ?)");
            stmt.setInt(1, ingredient.getIno());
            stmt.setString(2, ingredient.getName());
            stmt.setFloat(3, ingredient.getPrice());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    
    public String findNameById(int ino) {
        String name = null;
        try (Connection con = ds.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("SELECT name FROM ingredients WHERE ino = ?");
            stmt.setInt(1, ino);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return name;
    }
}
