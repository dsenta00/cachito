package dsenta.cachito.model.group;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private final List<GroupPeriod> groupPeriods;

    public Group(List<GroupPeriod> groupPeriods) {
        this.groupPeriods = groupPeriods;
    }

    public List<GroupPeriod> getGroupPeriods() {
        return groupPeriods;
    }
}