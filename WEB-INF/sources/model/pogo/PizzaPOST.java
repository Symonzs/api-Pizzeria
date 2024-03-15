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
        if (pg == null)
            return null;
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

    public static PizzaPOST updatePizzaPOST(PizzaGET pg, PizzaPOST pp) {
        PizzaPOST p = new PizzaPOST();
        if (pg == null || pp == null)
            return null;
        if (pg.getPiname().equals(pp.getPiname())) {
            p.setPiname(null);
        } else {
            p.setPiname(pp.getPiname());
        }
        if (pg.getPipate().equals(pp.getPipate())) {
            p.setPipate(null);
        } else {
            p.setPipate(pp.getPipate());
        }
        if (pg.getPibase().equals(pp.getPibase())) {
            p.setPibase(null);
        } else {
            p.setPibase(pp.getPibase());
        }
        if (pg.getIngredients().equals(pp.getIngredients())) {
            p.setIngredients(null);
        } else {
            p.setIngredients(pp.getIngredients());
        }
        return p;
    }
}