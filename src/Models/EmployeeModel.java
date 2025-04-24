package Models;

import java.sql.Date;

public class EmployeeModel {
    int id;
    String fname;
    String lname;
    Date dob;
    String number;
    String email;
    int dep;
    String password;
    int shift;
    int dayoff;

    public EmployeeModel(int id, String fname, String lname, Date dob, String number, String email, int dep,
            String password, int shift, int dayoff) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
        this.number = number;
        this.email = email;
        this.dep = dep;
        this.password = password;
        this.shift = shift;
        this.dayoff = dayoff;
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

    public int getDep() {
        return dep;
    }

    public String getPassword() {
        return password;
    }

    public int getShift() {
        return shift;
    }

    public int getDayoff() {
        return dayoff;
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

    public void setDep(int dep) {
        this.dep = dep;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public void setDayoff(int dayoff) {
        this.dayoff = dayoff;
    }
}