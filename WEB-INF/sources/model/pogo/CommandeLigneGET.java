package model.pogo;

public class CommandeLigneGET {
    private Integer pqte;
    private PizzaGET pizza;

    public CommandeLigneGET(Integer pqte, PizzaGET pizza) {
        this.pqte = pqte;
        this.pizza = pizza;
    }

    public Integer getPqte() {
        return this.pqte;
    }

    public PizzaGET getPizza() {
        return this.pizza;
    }

    public void setPqte(Integer pqte) {
        this.pqte = pqte;
    }

    public void setPizza(PizzaGET pizza) {
        this.pizza = pizza;
    }
}
