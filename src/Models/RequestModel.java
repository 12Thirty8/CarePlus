package Models;

import java.sql.Date;

public class RequestModel {
    int id;
    String requestListId;
    String patientName;
    String doctorName;
    String nurseName;
    Date requestDate;
    boolean status;

    public RequestModel(int id, String requestListId, String patientName, String doctorName, String nurseName,
            Date requestDate, boolean status) {
        this.id = id;
        this.requestListId = requestListId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.nurseName = nurseName;
        this.requestDate = requestDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestListId() {
        return requestListId;
    }

    public void setRequestListId(String requestListId) {
        this.requestListId = requestListId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getNurseName() {
        return nurseName;
    }

    public void setNurseName(String nurseName) {
        this.nurseName = nurseName;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}