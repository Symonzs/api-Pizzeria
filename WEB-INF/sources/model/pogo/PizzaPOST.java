package model.pogo;

import java.util.ArrayList;
import java.util.List;

public class PizzaPOST {

    private String piname;
    private List<Integer> ingredients;
    private String pipate;
    private String pibase;

    public PizzaPOST() {
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

    public static PizzaPOST fromPizzaGET(PizzaGET pg) {
        PizzaPOST pp = new PizzaPOST();
        pp.setPiname(pg.getPiname());
        List<Integer> ingredients = new ArrayList<Integer>();
        for (IngredientGET ingredient : pg.getIngredients()) {
            ingredients.add(ingredient.getIno());
        }
        pp.setIngredients(ingredients);
        pp.setPipate(pg.getPipate());
        pp.setPibase(pg.getPibase());
        return pp;
    }
}