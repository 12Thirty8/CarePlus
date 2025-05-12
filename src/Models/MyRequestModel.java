package Models;

import java.sql.Date;

public class MyRequestModel {
    int reqid;
    int recordid;
    Date reqdate;
    String status;

    public MyRequestModel(int reqid, int recordid, Date reqdate, String status) {
        this.reqid = reqid;
        this.recordid = recordid;
        this.reqdate = reqdate;
        this.status = status;
    }

    public int getReqid() {
        return reqid;
    }

    public void setReqid(int reqid) {
        this.reqid = reqid;
    }

    public int getRecordid() {
        return recordid;
    }

    public void setRecordid(int recordid) {
        this.recordid = recordid;
    }

    public Date getReqdate() {
        return reqdate;
    }

    public void setReqdate(Date reqdate) {
        this.reqdate = reqdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
