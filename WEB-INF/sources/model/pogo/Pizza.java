package model.pogo;

import java.util.List;

public class Pizza {
    private Integer pino;
    private String piname;
    private List<Ingredient> ingredients;
    private double price;
    private String pate;
    private String base;

    public Pizza() {
    }

    public Pizza(Integer pino, String piname, List<Ingredient> ingredients, double price, String pate, String base) {
        this.pino = pino;
        this.piname = piname;
        this.ingredients = ingredients;
        this.price = price;
        this.pate = pate;
        this.base = base;
    }

    public Integer getPino() {
        return this.pino;
    }

    public String getPiname() {
        return this.piname;
    }

    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public double getPrice() {
        return this.price;
    }

    public String getPate() {
        return this.pate;
    }

    public String getBase() {
        return this.base;
    }

    public void setPino(Integer pino) {
        this.pino = pino;
    }

    public void setPiname(String piname) {
        this.piname = piname;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPate(String pate) {
        this.pate = pate;
    }

    public void setBase(String base) {
        this.base = base;
    }

    @Override
    public String toString() {
        return "Pizza [id=" + pino + ", name=" + piname + ", ingredients=" + ingredients + ", price=" + price + ", pate=" + pate + ", base=" + base + "]";
    }
}