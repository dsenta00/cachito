package dsenta.cachito.model.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AndWhere implements Serializable {
    private String attribute;
    private Operator operator;
    private Object value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object value2;
    private boolean asc = false;

    public AndWhere(String attribute, Operator operator, Object value) {
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
    }

    public AndWhere(String attribute, Operator operator, Object from, Object to) {
        this.attribute = attribute;
        this.operator = operator;
        this.value = from;
        this.value2 = to;
    }

    public AndWhere(String attribute, Operator operator, Object value, boolean asc) {
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
        this.asc = asc;
    }
}