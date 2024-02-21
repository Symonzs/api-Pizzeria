package model.pogo;

import java.util.ArrayList;
import java.util.List;

public class IngredientPOST {

    public static Integer COUNTER = 1;
    public static final List<Integer> EMPTY_ROWS = new ArrayList<Integer>();

    private String iname;
    private Float iprice;

    public IngredientPOST() {
    }

    public String getIname() {
        return this.iname;
    }

    public Float getIprice() {
        return this.iprice;
    }

    public void setIname(String iname) {
        this.iname = iname.toLowerCase();
    }

    public void setIprice(Float iprice) {
        this.iprice = iprice;
    }

    @Override
    public String toString() {
        return "{" +
                " iname='" + getIname() + "'," +
                " iprice='" + getIprice() + "'" +
                "}";
    }
}
