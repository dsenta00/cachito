package dsenta.cachito.resource.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Person implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String oib;
    private Date createdAt;
    private Date updatedAt;
}
