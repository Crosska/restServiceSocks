package com.crosska.api.socksApi.model;

public class Sock {

    private Integer id;
    private String color;
    private int cotton;
    private int amount;

    public Sock() {
        amount = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCotton() {
        return cotton;
    }

    public void setCotton(int cotton) {
        this.cotton = cotton;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
