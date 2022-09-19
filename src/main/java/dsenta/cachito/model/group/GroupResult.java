package dsenta.cachito.model.group;

import java.util.ArrayList;
import java.util.List;

public class GroupResult {
    private Object from;
    private final Object to;
    private final String periodName;
    private List<Long> ids = new ArrayList<>();

    public GroupResult(Object from, Object to, String periodName) {
        this.from = from;
        this.to = to;
        this.periodName = periodName;
    }

    public GroupResult(Object from, Object to, String periodName, List<Long> ids) {
        this.from = from;
        this.to = to;
        this.periodName = periodName;
        this.ids = ids;
    }

    public Object getFrom() {
        return from;
    }

    public Object getTo() {
        return to;
    }

    public void setFrom(Object from) {
        this.from = from;
    }

    public String getPeriodName() {
        return periodName;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}