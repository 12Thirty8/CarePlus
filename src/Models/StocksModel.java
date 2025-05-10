package Models;

import java.sql.Date;

public class StocksModel {
    int batchId;
    String medName;
    int stock;
    Date expDate;
    String inBy;

    public StocksModel(int batchId, String medName, int stock, Date expDate, String inBy) {
        this.batchId = batchId;
        this.medName = medName;
        this.stock = stock;
        this.expDate = expDate;
        this.inBy = inBy;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
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

    public String getInBy() {
        return inBy;
    }

    public void setInBy(String inBy) {
        this.inBy = inBy;
    }

}
