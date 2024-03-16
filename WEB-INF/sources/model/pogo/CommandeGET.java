package model.pogo;

import java.util.Date;
import java.util.List;

public class CommandeGET {

    private Integer cno;
    private String cname;
    private Date cdate;
    private List<CommandeLigneGET> pizzas;
    private float price;

    public CommandeGET(Integer cno, String cname, Date cdate, List<CommandeLigneGET> pizzas) {
        this.cno = cno;
        this.cname = cname;
        this.cdate = cdate;
        this.pizzas = pizzas;
        this.price = 0;
        for (CommandeLigneGET pizza : pizzas) {
            this.price += pizza.getPizza().getPrice() * pizza.getPqte();
        }
    }

    public Integer getCno() {
        return this.cno;
    }

    public String getCname() {
        return this.cname;
    }

    public Date getCdate() {
        return this.cdate;
    }

    public List<CommandeLigneGET> getPizzas() {
        return this.pizzas;
    }

    public float getPrice() {
        return this.price;
    }

    public void setCno(Integer cno) {
        this.cno = cno;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public void setPizzas(List<CommandeLigneGET> pizzas) {
        this.pizzas = pizzas;
    }

}
