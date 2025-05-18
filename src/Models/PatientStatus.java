package Models;

public class PatientStatus {
    public int id;
    public String name;

    public PatientStatus(int id, String label) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf(id); // Ensures ID shows in ChoiceBox
    }
}
