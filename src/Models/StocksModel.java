package Models;

import java.sql.Date;

public class StocksModel {
    int medId;
    String medName;
    int stock;
    Date expDate;
    String category;
    String inBy;

    public StocksModel(int medId, String medName, int stock, Date expDate, String category, String inBy) {
        this.medId = medId;
        this.medName = medName;
        this.stock = stock;
        this.expDate = expDate;
        this.category = category;
        this.inBy = inBy;
    }

    public int getMedId() {
        return medId;
    }

    public void setMedId(int medId) {
        this.medId = medId;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInBy() {
        return inBy;
    }

    public void setInBy(String inBy) {
        this.inBy = inBy;
    }

}
