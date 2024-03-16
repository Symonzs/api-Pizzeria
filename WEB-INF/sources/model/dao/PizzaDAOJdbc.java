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
            String selectPizzasQuery = "SELECT * FROM pizzas ORDER BY pino";
            String selectIngredientsQuery = "SELECT ino FROM contient WHERE pino = ? ORDER BY ino";
            System.out.println(selectPizzasQuery);
            ResultSet rsPizzas = con.createStatement().executeQuery(selectPizzasQuery);
            IngredientDAOJdbc ingredientDAO = new IngredientDAOJdbc();
            while (rsPizzas.next()) {
                List<IngredientGET> ingredients = new ArrayList<IngredientGET>();
                PreparedStatement stmtIngredients = con.prepareStatement(selectIngredientsQuery);
                stmtIngredients.setInt(1, rsPizzas.getInt("pino"));
                System.out.println(stmtIngredients);
                ResultSet rsIngredients = stmtIngredients.executeQuery();
                while (rsIngredients.next()) {
                    ingredients.add(ingredientDAO.findById(rsIngredients.getInt("ino")));
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
        try (Connection con = dataSource.getConnection()) {
            String selectPizzaQuery = "SELECT * FROM pizzas WHERE pino = ?";
            String selectIngredientsQuery = "SELECT ino FROM contient WHERE pino = ? ORDER BY ino";
            PreparedStatement stmtPizza = con.prepareStatement(selectPizzaQuery);
            stmtPizza.setInt(1, pino);
            System.out.println(stmtPizza);
            ResultSet rsPizza = stmtPizza.executeQuery();
            if (rsPizza.next()) {
                IngredientDAOJdbc ingredientDAO = new IngredientDAOJdbc();
                List<IngredientGET> ingredients = new ArrayList<IngredientGET>();
                PreparedStatement stmtIngredients = con.prepareStatement(selectIngredientsQuery);
                stmtIngredients.setInt(1, pino);
                System.out.println(stmtIngredients);
                ResultSet rsIngredients = stmtIngredients.executeQuery();
                while (rsIngredients.next()) {
                    ingredients.add(ingredientDAO.findById(rsIngredients.getInt("ino")));
                }
                return new PizzaGET(rsPizza.getInt("pino"), rsPizza.getString("piname"), ingredients,
                        rsPizza.getString("pipate"), rsPizza.getString("pibase"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean save(PizzaPOST pizza) {
        try (Connection con = dataSource.getConnection()) {
            String selectPizzasQuery = "SELECT pino FROM pizzas ORDER BY pino";
            String insertPizzaQuery = "INSERT INTO pizzas (pino, piname, pipate, pibase) VALUES (?, ?, ?, ?)";
            String insertContientQuery = "INSERT INTO contient (pino, ino) VALUES (?, ?)";
            ResultSet rsPizzas = con.createStatement().executeQuery(selectPizzasQuery);
            int previousPino = 0;
            while (rsPizzas.next()) {
                if (rsPizzas.getInt("pino") == previousPino + 1)
                    previousPino = rsPizzas.getInt("pino");
            }
            PreparedStatement stmtPizza = con.prepareStatement(insertPizzaQuery);
            stmtPizza.setInt(1, previousPino + 1);
            stmtPizza.setString(2, pizza.getPiname());
            stmtPizza.setString(3, pizza.getPipate());
            stmtPizza.setString(4, pizza.getPibase());
            System.out.println(stmtPizza);
            stmtPizza.executeUpdate();
            PreparedStatement stmtContient = con.prepareStatement(insertContientQuery);
            for (Integer ino : pizza.getIngredients()) {
                stmtContient.clearParameters();
                stmtContient.setInt(1, previousPino + 1);
                stmtContient.setInt(2, ino);
                System.out.println(stmtContient);
                stmtContient.addBatch();
            }
            stmtContient.executeBatch();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean save(PizzaGET pizza, Set<Integer> ingredients) {
        try (Connection con = dataSource.getConnection()) {
            String insertContientQuery = "INSERT INTO contient (pino, ino) VALUES (?, ?)";
            PreparedStatement stmtContient = con.prepareStatement(insertContientQuery);
            for (IngredientGET ingredient : pizza.getIngredients()) {
                while (ingredients.contains(ingredient.getIno())) {
                    ingredients.remove(ingredient.getIno());
                }
            }
            for (Integer ino : ingredients) {
                stmtContient.clearParameters();
                stmtContient.setInt(1, pizza.getPino());
                stmtContient.setInt(2, ino);
                System.out.println(stmtContient);
                stmtContient.addBatch();
            }
            stmtContient.executeBatch();
            return true;
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
                PreparedStatement stmt = con
                        .prepareStatement("SELECT Count(*) FROM contient WHERE pino = ? AND ino = ?");
                PreparedStatement stmt2 = con.prepareStatement("INSERT INTO contient (pino, ino) VALUES (?, ?)");
                for (int ino : pizza.getIngredients()) {
                    stmt.clearParameters();
                    stmt.setInt(1, pino);
                    stmt.setInt(2, ino);
                    System.out.println(stmt);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        stmt2.clearParameters();
                        stmt2.setInt(1, pino);
                        stmt2.setInt(2, ino);
                        System.out.println(stmt2);
                        stmt2.addBatch();
                    }
                }
                stmt2.executeBatch();
            }
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean put(int pino, PizzaPOST pizza) {
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
                con.setAutoCommit(false);
                PreparedStatement stmt = con.prepareStatement("DELETE FROM contient WHERE pino = ?");
                stmt.setInt(1, pino);
                System.out.println(stmt);
                stmt.executeUpdate();
                PreparedStatement stmt2 = con.prepareStatement("INSERT INTO contient (pino, ino) VALUES (?, ?)");
                for (int ino : pizza.getIngredients()) {
                    stmt2.clearParameters();
                    stmt2.setInt(1, pino);
                    stmt2.setInt(2, ino);
                    System.out.println(stmt2);
                    stmt2.addBatch();
                }
                stmt2.executeBatch();
                con.commit();
                con.setAutoCommit(true);
            }
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}