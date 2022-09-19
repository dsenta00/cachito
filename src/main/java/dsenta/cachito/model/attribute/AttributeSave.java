package dsenta.cachito.model.attribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttributeSave {
    private String type;
    private String name;
    @JsonProperty("default")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object defaultValue;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String inverseBy;
    private boolean cascadeDelete = false;
    private boolean cascadePersist = false;
    private boolean unique = false;
    private boolean filterable = false;
}