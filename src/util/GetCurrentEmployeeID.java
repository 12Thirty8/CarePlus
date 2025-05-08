package util;


public class GetCurrentEmployeeID {

    private static GetCurrentEmployeeID instance;

    // Current user's employee ID
    private int employee_id;

    // Private constructor to prevent direct instantiation
    private GetCurrentEmployeeID() {
    }

    // Method to get the singleton instance of GetCurrentEmployeeID
    public static GetCurrentEmployeeID getInstance() {
        if (instance == null) {
            instance = new GetCurrentEmployeeID();
        }
        return instance;
    }

    // Setter for employee_id (to be used in login or authentication flow)
    public void setEmployeeId(int employeeId) {
        this.employee_id = employeeId;
    }

    // Getter for employee_id
    public int getEmployeeId() {
        return employee_id;
    }

    public static int fetchEmployeeIdFromSession() {
        return getInstance().getEmployeeId(); // Returns the employee_id stored in the singleton
    }
}