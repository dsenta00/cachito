package dsenta.cachito.model.clazzalter;

import dsenta.cachito.model.attribute.Attribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClazzAlter {
    private List<Attribute> fieldsToAdd = new ArrayList<>();
    private Map<String, Attribute> fieldsToUpdate = new HashMap<>();
    private List<String> fieldsToDelete = new ArrayList<>();
}