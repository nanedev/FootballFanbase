package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 29/10/2017.
 */

public class OddsModel {

    String value1;
    String valueX;
    String value2;
    String companyName;

    public OddsModel() {
    }

    public OddsModel(String value1, String valueX, String value2, String companyName) {
        this.value1 = value1;
        this.valueX = valueX;
        this.value2 = value2;
        this.companyName = companyName;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValueX() {
        return valueX;
    }

    public void setValueX(String valueX) {
        this.valueX = valueX;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
