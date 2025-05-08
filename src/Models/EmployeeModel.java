package Models;

import java.sql.Date;

public class EmployeeModel {
    int id;
    String fname;
    String lname;
    Date dob;
    String number;
    String email;
    String depName;
    String password;
    String shiftName;
    String dayoffName;

    public EmployeeModel(int id, String fname, String lname, Date dob, String number, String email, String depName,
            String password, String shiftName, String dayoffName) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
        this.number = number;
        this.email = email;
        this.depName = depName;
        this.password = password;
        this.shiftName = shiftName;
        this.dayoffName = dayoffName;
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public Date getDob() {
        return dob;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getDepName() {
        return depName;
    }

    public String getPassword() {
        return password;
    }

    public String getShiftName() {
        return shiftName;
    }

    public String getDayoffName() {
        return dayoffName;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public void setDayoffName(String dayoffName) {
        this.dayoffName = dayoffName;
    }
}