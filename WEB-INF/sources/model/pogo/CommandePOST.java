package model.pogo;

import java.util.ArrayList;
import java.util.List;

public class CommandePOST {

    private String cname;
    private String cdate;
    private List<Integer> pizzas;

    public CommandePOST() {
    }

    public String getCname() {
        return this.cname;
    }

    public String getCdate() {
        return this.cdate;
    }

    public List<Integer> getPizzas() {
        return this.pizzas;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public void setPizzas(List<Integer> pizzas) {
        this.pizzas = pizzas;
    }

    public void addPizzas(List<Integer> pizzas) {
        for (Integer pizza : pizzas) {
            if (!this.pizzas.contains(pizza))
                this.pizzas.add(pizza);
        }
    }

    public static CommandePOST fromCommandeGET(CommandeGET cg) {
        if (cg == null)
            return null;
        CommandePOST pp = new CommandePOST();
        pp.setCname(cg.getCname());
        pp.setCdate(cg.getCdate());
        List<Integer> pizzas = new ArrayList<Integer>();
        for (PizzaGET pizza : cg.getPizzas()) {
            pizzas.add(pizza.getPino());
        }
        pp.setPizzas(pizzas);
        return pp;
    }

    public static CommandePOST updateCommandePOST(CommandeGET cg, CommandePOST cp) {
        CommandePOST c = new CommandePOST();
        if (cg == null || cp == null)
            return null;
        if (cg.getCname().equals(cp.getCname()))
            c.setCname(null);
        else
            c.setCname(cg.getCname());
        if (cg.getCdate().equals(cp.getCdate()))
            c.setCdate(null);
        else
            c.setCdate(cg.getCdate());
        if (cg.getPizzas().equals(cp.getPizzas()))
            c.setPizzas(null);
        else
            c.setPizzas(cp.getPizzas());
        return c;
    }
}