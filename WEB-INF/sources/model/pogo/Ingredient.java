package model.pogo;

public class Ingredient {

    private int ino;
    private String name;
    private float price;

    public Ingredient() {
    }

    public Ingredient(int ino, String name, float price) {
        this.ino= ino;
        this.name = name;
        this.price = price;
    }

    public int getIno() {
        return this.ino;
    }

    public String getName() {
        return this.name;
    }

    public float getPrice() {
        return this.price;
    }

    public void setIno(int ino) {
        this.ino = ino;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ingredient [id=" + ino + ", name=" + name + ", price=" + price + "]";
    }
}
