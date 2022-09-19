package dsenta.cachito.model.fields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldsToDisplay implements Serializable {
    private List<String> fieldNames;
    private Map<String, FieldsToDisplay> fieldsOfAttributeToDisplay;

    public static FieldsToDisplay all() {
        return new FieldsToDisplay(List.of(), Map.of());
    }

    public static FieldsToDisplay idOnly() {
        return new FieldsToDisplay(List.of("id"), Map.of());
    }
}