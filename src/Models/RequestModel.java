package Models;

import java.sql.Date;

public class RequestModel {
    int reqid;
    int recId;
    String nurseName;
    Date requestDate;
    boolean status;

    public RequestModel(int reqid, int recId, String nurseName,
            Date requestDate, boolean status) {
        this.reqid = reqid;
        this.recId = recId;
        this.nurseName = nurseName;
        this.requestDate = requestDate;
        this.status = status;
    }

    public int getReqid() {
        return reqid;
    }

    public void setReqid(int reqid) {
        this.reqid = reqid;
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
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

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}