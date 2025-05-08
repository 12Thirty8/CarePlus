package Models;

import java.sql.Date;

public class NurseModel {
    private int activityId;
    private int employeeId;
    private String takenFrom;
    private String activity;
    private Date dateTime;

    public NurseModel(String takenFrom, String activity, Date dateTime) {
        this.takenFrom = takenFrom;
        this.activity = activity;
        this.dateTime = dateTime;
    }

    public NurseModel(int employeeId, String takenFrom, String activity, Date dateTime) {
        this.employeeId = employeeId;
        this.takenFrom = takenFrom;
        this.activity = activity;
    }

    // Constructor with activityId (for retrieving from database)
    public NurseModel(int activityId, int employeeId, String takenFrom, String activity, Date dateTime) {
        this.activityId = activityId;
        this.employeeId = employeeId;
        this.takenFrom = takenFrom;
        this.activity = activity;
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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

}
