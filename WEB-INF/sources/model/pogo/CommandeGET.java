package model.pogo;

import java.util.List;

public class CommandeGET {

    private Integer cno;
    private String cname;
    private String cdate;
    private List<PizzaGET> pizzas;
    
    public CommandeGET(Integer cno, String cname, String cdate, List<PizzaGET> pizzas) {
        this.cno = cno;
        this.cname = cname;
        this.cdate = cdate;
        this.pizzas = pizzas;
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

    public List<PizzaGET> getPizzas() {
        return this.pizzas;
    }

    public void setCno(Integer cno) {
        this.cno = cno;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public void setPizzas(List<PizzaGET> pizzas) {
        this.pizzas = pizzas;
    }

}
