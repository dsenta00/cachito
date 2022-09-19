package dsenta.cachito.resource.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class Article {
    private Long id;
    private Long lot;
    private String name;
    private Double price;
    private Bill bill;
}
