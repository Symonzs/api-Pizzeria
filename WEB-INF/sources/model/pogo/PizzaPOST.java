package model.pogo;

import java.util.ArrayList;
import java.util.List;

public class PizzaPOST {

    public static Integer COUNTER = 1;
    public static final List<Integer> EMPTY_ROWS = new ArrayList<Integer>();

    private String piname;
    private List<Integer> ingredients;
    private String pipate;
    private String pibase;

    public PizzaPOST(Integer pino, String piname, List<Integer> ingredients, double price, String pate,
            String base) {
        this.piname = piname;
        this.ingredients = ingredients;
        this.pipate = pate;
        this.pibase = base;
    }

    public String getPiname() {
        return this.piname;
    }

    public List<Integer> getIngredients() {
        return this.ingredients;
    }

    public String getPipate() {
        return this.pipate;
    }

    public String getPibase() {
        return this.pibase;
    }

    public void setPiname(String piname) {
        this.piname = piname;
    }

    public void setIngredients(List<Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public void setPipate(String pate) {
        this.pipate = pate;
    }

    public void setPibase(String base) {
        this.pibase = base;
    }

    public void addIngredients(List<Integer> ingredients) {
        for (Integer ingredient : ingredients) {
            if (!this.ingredients.contains(ingredient))
                this.ingredients.add(ingredient);
        }
    }
}