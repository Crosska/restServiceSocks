package com.crosska.api.socksApi.model;

import javax.persistence.*;

@Entity
@Table(name = "socks")
public class Sock {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "color")
    private String color;

    @Column(name = "cotton")
    private int cotton;

    @Column(name = "amount")
    private int amount;

    public Sock(String color, int cotton, int amount) {
        this.color = color;
        this.cotton = cotton;
        this.amount = amount;
    }

    public Sock() {}

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
