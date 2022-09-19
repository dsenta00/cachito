package dsenta.cachito.resource.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Employee extends Person {
    private Long id;
    private String employeeId;
}
