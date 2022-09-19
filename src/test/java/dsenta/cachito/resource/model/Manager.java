package dsenta.cachito.resource.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Manager extends Employee {
    private Long id;
    private String managerId;
    private List<Employee> employees = new LinkedList<>();
}
