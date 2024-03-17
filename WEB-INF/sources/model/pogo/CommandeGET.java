package model.pogo;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class CommandeGET {

    private Integer cno;
    private String cname;
    private String cdate;
    private List<CommandeLigneGET> pizzas;
    private float price;

    public CommandeGET(Integer cno, String cname, long cdate, List<CommandeLigneGET> pizzas) {
        this.cno = cno;
        this.cname = cname;
        this.cdate = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(cdate));
        this.pizzas = pizzas;
        this.price = 0;
        for (CommandeLigneGET cl : pizzas) {
            this.price += cl.getPizza().getPrice() * cl.getPqte();
        }
    }

    public Integer getCno() {
        return this.cno;
    }

    public String getCname() {
        return this.cname;
    }

    public String getCdate() {
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
        this.cdate = DateFormat.getDateInstance(DateFormat.SHORT).format(cdate);
    }

    public void setPizzas(List<CommandeLigneGET> pizzas) {
        this.pizzas = pizzas;
    }

}
