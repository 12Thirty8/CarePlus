package Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Model class representing a record in the 'records' table.
 */
public class RecordsModel {
    private final IntegerProperty recordId = new SimpleIntegerProperty();
    private final IntegerProperty patientId = new SimpleIntegerProperty();
    private final StringProperty fName = new SimpleStringProperty();
    private final StringProperty lName = new SimpleStringProperty();
    private final StringProperty doctorName = new SimpleStringProperty();
    private final IntegerProperty status = new SimpleIntegerProperty();
    private final StringProperty chiefComplaint = new SimpleStringProperty();
    private final StringProperty diagnosis = new SimpleStringProperty();
    private final StringProperty disposition = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> recordDate = new SimpleObjectProperty<>();

    public RecordsModel() {
    }

    public RecordsModel(String patientName, int patientId,
            String doctorName, String diagnosis,
            String disposition, int status) {
        // Split patientName into fName and lName
        String[] names = patientName.trim().split(" ", 2);
        this.fName.set(names.length > 0 ? names[0] : "");
        this.lName.set(names.length > 1 ? names[1] : "");
        this.patientId.set(patientId);
        this.doctorName.set(doctorName);
        this.diagnosis.set(diagnosis);
        this.disposition.set(disposition);
        this.status.set(status);
    }

    // Properties
    public IntegerProperty recordIdProperty() {
        return recordId;
    }

    public IntegerProperty patientIdProperty() {
        return patientId;
    }

    public StringProperty fNameProperty() {
        return fName;
    }

    public StringProperty lNameProperty() {
        return lName;
    }

    public StringProperty doctorNameProperty() {
        return doctorName;
    }

    public IntegerProperty statusProperty() {
        return status;
    }

    public StringProperty chiefComplaintProperty() {
        return chiefComplaint;
    }

    public StringProperty diagnosisProperty() {
        return diagnosis;
    }

    public StringProperty dispositionProperty() {
        return disposition;
    }

    public ObjectProperty<LocalDate> recordDateProperty() {
        return recordDate;
    }

    // Getters & Setters
    public int getRecordId() {
        return recordId.get();
    }

    public void setRecordId(int value) {
        recordId.set(value);
    }

    public int getPatientId() {
        return patientId.get();
    }

    public void setPatientId(int value) {
        patientId.set(value);
    }

    public String getFName() {
        return fName.get();
    }

    public void setFName(String value) {
        fName.set(value);
    }

    public String getLName() {
        return lName.get();
    }

    public void setLName(String value) {
        lName.set(value);
    }

    public String getDoctorName() {
        return doctorName.get();
    }

    public void setDoctorName(String value) {
        doctorName.set(value);
    }

    public int getStatus() {
        return status.get();
    }

    public void setStatus(int value) {
        status.set(value);
    }

    public String getChiefComplaint() {
        return chiefComplaint.get();
    }

    public void setChiefComplaint(String value) {
        chiefComplaint.set(value);
    }

    public String getDiagnosis() {
        return diagnosis.get();
    }

    public void setDiagnosis(String value) {
        diagnosis.set(value);
    }

    public String getDisposition() {
        return disposition.get();
    }

    public void setDisposition(String value) {
        disposition.set(value);
    }

    public LocalDate getRecordDate() {
        return recordDate.get();
    }

    public void setRecordDate(LocalDate value) {
        recordDate.set(value);
    }

    /**
     * Full name of the patient (fName + " " + lName).
     */
    public String getPatientName() {
        return getFName() + " " + getLName();
    }

    public ReadOnlyStringWrapper patientNameProperty() {
        return new ReadOnlyStringWrapper(getPatientName());
    }
}
