package dsenta.cachito.resource.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode
@Data
public class Bill {
    private Long id;
    private Person buyer;
    private @EqualsAndHashCode.Exclude List<Article> articles = new LinkedList<>();
    private Date date;

    public String toString() {
        return "Bill(id=" + this.getId() +
                ", buyer=" + this.getBuyer() +
                ", date=" + this.getDate() + ")";
    }
}
