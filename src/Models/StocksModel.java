package Models;

import java.sql.Date;

public class StocksModel {

    private int id;
    private String name;
    private int quantity;
    private Date expDate;
    private String sinby;
    private Date sinDate;
    private String status;

    public StocksModel(int id, String name, int quantity, Date expDate, String sinby, Date sinDate, String status) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expDate = expDate;
        this.sinby = sinby;
        this.sinDate = sinDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getSinby() {
        return sinby;
    }

    public void setSinby(String sinby) {
        this.sinby = sinby;
    }

    public Date getSinDate() {
        return sinDate;
    }

    public void setSinDate(Date sinDate) {
        this.sinDate = sinDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}