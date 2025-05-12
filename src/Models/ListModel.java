package Models;

public class ListModel {
    int id;
    String name;
    String dosage;
    int quantity;

    public ListModel(int id, String name, String dosage, int quantity) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.quantity = quantity;
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

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
