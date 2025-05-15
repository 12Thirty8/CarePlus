package Models;

import java.time.LocalDateTime;

public class ChangeLog {
    private int id;
    private String tableName;
    private String action;
    private String oldData;
    private String newData;
    private String changedBy;
    private LocalDateTime changedAt;

    public ChangeLog(int id, String tableName, String action, String oldData, String newData, String changedBy, LocalDateTime changedAt) {
        this.id = id;
        this.tableName = tableName;
        this.action = action;
        this.oldData = oldData;
        this.newData = newData;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    // Getters
    public int getId() { return id; }
    public String getTableName() { return tableName; }
    public String getAction() { return action; }
    public String getOldData() { return oldData; }
    public String getNewData() { return newData; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
