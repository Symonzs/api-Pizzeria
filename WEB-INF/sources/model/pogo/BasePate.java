package model.pogo;

public class BasePate {
    private int bano;
    private String name;

    public BasePate() {
    }

    public BasePate(int id, String name) {
        this.bano = id;
        this.name = name;
    }

    public int getBano() {
        return this.bano;
    }

    public String getName() {
        return this.name;
    }

    public void setBano(int id) {
        this.bano = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
