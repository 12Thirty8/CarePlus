package Models;

import javafx.scene.text.Text;

public class ProductsModel {
    int id;
    String name;
    String category;
    Text desc;

    public ProductsModel(int id, String name, String category, Text desc) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.desc = desc;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Text getDesc() {
        return desc;
    }

    public void setDesc(Text desc) {
        this.desc = desc;
    }

}
