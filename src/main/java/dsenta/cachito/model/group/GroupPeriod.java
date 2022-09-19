package dsenta.cachito.model.group;

import dsenta.cachito.model.entity.Entity;

import java.io.Serializable;
import java.util.List;

public class GroupPeriod implements Serializable {
    private final Object from;
    private final Object to;
    private final String periodName;
    private final List<Entity> records;

    public GroupPeriod(Object from, Object to, String periodName, List<Entity> records) {
        this.from = from;
        this.to = to;
        this.periodName = periodName;
        this.records = records;
    }

    public Object getFrom() {
        return from;
    }

    public Object getTo() {
        return to;
    }

    public String getPeriodName() {
        return periodName;
    }

    public List<Entity> getRecords() {
        return records;
    }
}