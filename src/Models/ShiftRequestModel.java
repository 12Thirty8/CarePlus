package Models;

import java.sql.Date;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class ShiftRequestModel {
    private SimpleIntegerProperty srid;
    private SimpleStringProperty shift;
    private SimpleStringProperty newshift;
    private SimpleStringProperty description;
    private SimpleObjectProperty<Date> requestDate;
    private SimpleBooleanProperty status;

    // Default constructor
    public ShiftRequestModel() {
        this.srid = new SimpleIntegerProperty();
        this.shift = new SimpleStringProperty();
        this.newshift = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.requestDate = new SimpleObjectProperty<>();
        this.status = new SimpleBooleanProperty();
    }

    // Parameterized constructor
    public ShiftRequestModel(int srid, String shift, String newshift, String description,
            Date requestDate, Boolean status) {
        this.srid = new SimpleIntegerProperty(srid);
        this.shift = new SimpleStringProperty(shift);
        this.newshift = new SimpleStringProperty(newshift);
        this.description = new SimpleStringProperty(description);
        this.requestDate = new SimpleObjectProperty<>(requestDate);
        this.status = new SimpleBooleanProperty(status);
    }

    // Getters and Setters for properties
    public int getSrid() {
        return srid.get();
    }

    public void setSrid(int srid) {
        this.srid.set(srid);
    }

    public SimpleIntegerProperty sridProperty() {
        return srid;
    }

    public String getShift() {
        return shift.get();
    }

    public void setShift(String shift) {
        this.shift.set(shift);
    }

    public SimpleStringProperty shiftProperty() {
        return shift;
    }

    public String getNewshift() {
        return newshift.get();
    }

    public void setNewshift(String newshift) {
        this.newshift.set(newshift);
    }

    public SimpleStringProperty newshiftProperty() {
        return newshift;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public Date getRequestDate() {
        return requestDate.get();
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate.set(requestDate);
    }

    public SimpleObjectProperty<Date> requestDateProperty() {
        return requestDate;
    }

    public Boolean getStatus() {
        return status.get();
    }

    public void setStatus(Boolean status) {
        this.status.set(status);
    }

    public SimpleBooleanProperty statusProperty() {
        return status;
    }
}