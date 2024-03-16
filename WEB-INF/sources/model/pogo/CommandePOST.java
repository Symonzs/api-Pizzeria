package model.pogo;

import java.util.List;

public class CommandePOST {

    private String cname;
    private List<CommandeLignePOST> pizzas;

    public CommandePOST() {
    }

    public String getCname() {
        return this.cname;
    }

    public List<CommandeLignePOST> getPizzas() {
        return this.pizzas;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setPizzas(List<CommandeLignePOST> pizzas) {
        this.pizzas = pizzas;
    }
}