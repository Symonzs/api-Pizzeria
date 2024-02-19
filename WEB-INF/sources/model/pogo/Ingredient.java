package model.pogo;

public class Ingredient {

    private int id;
    private String name;
    private float price;

    public Ingredient() {
    }

    public Ingredient(int id, String name, float price) {
        this.id= id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public float getPrice() {
        return this.price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ingredient [id=" + id + ", name=" + name + ", price=" + price + "]";
    }
}
