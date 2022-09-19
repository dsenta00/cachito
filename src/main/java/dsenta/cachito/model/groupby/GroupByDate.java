package dsenta.cachito.model.groupby;

public enum GroupByDate {
    DATE("date"),
    YEAR("year"),
    HOURDAY("hourday"),
    DAYWEEK("dayweek"),
    DAYMONTH("daymonth"),
    DAYYEAR("dayyear"),
    WEEKYEAR("weekyear"),
    MONTHYEAR("monthyear");

    public final String groupByDate;

    GroupByDate(String groupByDate) {
        this.groupByDate = groupByDate;
    }
}
