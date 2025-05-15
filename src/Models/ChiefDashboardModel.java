package Models;

import java.sql.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChiefDashboardModel {
    private final IntegerProperty requestId = new SimpleIntegerProperty();
    private final IntegerProperty recordId = new SimpleIntegerProperty();
    private final IntegerProperty listId = new SimpleIntegerProperty();
    private final StringProperty encodedBy = new SimpleStringProperty();
    private final ObjectProperty<Date> requestDate = new SimpleObjectProperty<>();
    private final IntegerProperty status = new SimpleIntegerProperty();

    public ChiefDashboardModel(int reqId, int recId, int list, String enc, Date date, int stat) {
        requestId.set(reqId);
        recordId.set(recId);
        listId.set(list);
        encodedBy.set(enc);
        requestDate.set(date);
        status.set(stat);
    }

    // getters for the *value*
    public int getRequestId() {
        return requestId.get();
    }

    public int getRecordId() {
        return recordId.get();
    }

    public int getListId() {
        return listId.get();
    }

    public String getEncodedBy() {
        return encodedBy.get();
    }

    public Date getRequestDate() {
        return requestDate.get();
    }

    public int getStatus() {
        return status.get();
    }

    // *JavaFX* property getters
    public IntegerProperty requestIdProperty() {
        return requestId;
    }

    public IntegerProperty recordIdProperty() {
        return recordId;
    }

    public IntegerProperty listIdProperty() {
        return listId;
    }

    public StringProperty encodedByProperty() {
        return encodedBy;
    }

    public ObjectProperty<Date> requestDateProperty() {
        return requestDate;
    }

    public IntegerProperty statusProperty() {
        return status;
    }
}
