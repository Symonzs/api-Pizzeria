package model.pogo;

import java.util.Set;

public class PizzaPOST {

    private String piname;
    private Set<Integer> ingredients;
    private String pipate;
    private String pibase;

    public PizzaPOST() {
    }

    public String getPiname() {
        return this.piname;
    }

    public Set<Integer> getIngredients() {
        return this.ingredients;
    }

    public String getPipate() {
        return this.pipate;
    }

    public String getPibase() {
        return this.pibase;
    }

    public void setPiname(String piname) {
        this.piname = piname.toLowerCase();
    }

    public void setIngredients(Set<Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public void setPipate(String pate) {
        this.pipate = pate;
    }

    public void setPibase(String base) {
        this.pibase = base;
    }
}