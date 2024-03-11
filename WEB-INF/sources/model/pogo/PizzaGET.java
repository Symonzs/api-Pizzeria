package model.pogo;

import java.util.List;

public class PizzaGET {

    private Integer pino;
    private String piname;
    private List<IngredientGET> ingredients;
    private float price;
    private String pipate;
    private String pibase;

    public PizzaGET(Integer pino, String piname, List<IngredientGET> ingredients, String pate,
            String base) {
        this.pino = pino;
        this.piname = piname;
        this.ingredients = ingredients;
        this.price = 0.0f;
        for (IngredientGET ingredient : ingredients) {
            System.out.println(ingredient.getIprice());
            this.price += ingredient.getIprice();
        }
        this.pipate = pate;
        this.pibase = base;
    }

    public Integer getPino() {
        return this.pino;
    }

    public String getPiname() {
        return this.piname;
    }

    public List<IngredientGET> getIngredients() {
        return this.ingredients;
    }

    public double getPrice() {
        return this.price;
    }

    public String getPipate() {
        return this.pipate;
    }

    public String getPibase() {
        return this.pibase;
    }

    public void setPino(Integer pino) {
        this.pino = pino;
    }

    public void setPiname(String piname) {
        this.piname = piname;
    }

    public void setIngredients(List<IngredientGET> ingredients) {
        this.ingredients = ingredients;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setPipate(String pate) {
        this.pipate = pate;
    }

    public void setPibase(String base) {
        this.pibase = base;
    }
}