package model.pogo;

import java.util.ArrayList;
import java.util.List;

public class PizzaPOST {

    public static Integer COUNTER = 1;
    public static final List<Integer> EMPTY_ROWS = new ArrayList<Integer>();

    private String piname;
    private List<IngredientGET> ingredients;
    private String pipate;
    private String pibase;

    public PizzaPOST(Integer pino, String piname, List<IngredientGET> ingredients, double price, String pate,
            String base) {
        this.piname = piname;
        this.ingredients = ingredients;
        this.pipate = pate;
        this.pibase = base;
    }

    public String getPiname() {
        return this.piname;
    }

    public List<IngredientGET> getIngredients() {
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

    public void setIngredients(List<IngredientGET> ingredients) {
        this.ingredients = ingredients;
    }

    public void setPipate(String pate) {
        this.pipate = pate;
    }

    public void setPibase(String base) {
        this.pibase = base;
    }
}