package model.pogo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IngredientPOST {

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
}
