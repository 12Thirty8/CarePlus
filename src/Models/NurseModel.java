package Models;

import java.time.LocalDateTime;

public class NurseModel {
    private int activityId;
    private int employeeId;
    private String takenFrom;
    private String activity;
    private LocalDateTime dateTime;

    public NurseModel(String takenFrom, String activity, LocalDateTime dateTime) {
        this.takenFrom = takenFrom;
        this.activity = activity;
        this.dateTime = dateTime;
    }

    // Getters and Setters
    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getTakenFrom() {
        return takenFrom;
    }

    public void setTakenFrom(String takenFrom) {
        this.takenFrom = takenFrom;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

}
