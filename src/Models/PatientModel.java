package Models;

public class PatientModel {
    int patient_id;
    String first_name;
    String last_name;

    public PatientModel(int patient_id, String first_name, String last_name) {
        this.patient_id = patient_id;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public int getPatient_id() {
        return this.patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getFirst_name() {
        return this.first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return this.last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

}
