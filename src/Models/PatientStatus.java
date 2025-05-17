package Models;

public class PatientStatus {
    public int id;
    public String name;

    public PatientStatus(int id, String label) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name; // Ensures label shows in ChoiceBox
    }
}
