package dsenta.cachito.model.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import dsenta.cachito.mapper.clazz.ClazzToClonedClazzMapper;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.fields.FieldsToDisplay;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class Attribute implements Serializable {
    private static final long serialVersionUID = -6233284918074765131L;
    private DataType dataType;
    @JsonIgnore
    private int propertyIndex;
    private String name;
    private Object defaultValue;
    @JsonIgnore
    private Clazz clazz;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String inverseBy;
    private boolean cascadeDelete = false;
    private boolean cascadePersist = false;
    private boolean unique = false;
    private boolean filterable = false;

    @JsonIgnore
    public boolean shouldHaveDimension() {
        return unique || filterable || DataType.isRelationship(dataType);
    }

    public Attribute clonePartially(FieldsToDisplay fieldsToDisplay) {
        if (isNull(fieldsToDisplay)) {
            return this;
        }

        if (isNull(fieldsToDisplay.getFieldNames()) || fieldsToDisplay.getFieldNames().isEmpty()) {
            return this;
        }

        if (!DataType.isRelationship(dataType)) {
            return this;
        }

        var attribute = new Attribute();
        attribute.setDataType(dataType);
        attribute.setPropertyIndex(propertyIndex);
        attribute.setName(name);
        attribute.setDefaultValue(defaultValue);
        attribute.setClazz(ClazzToClonedClazzMapper.clonePartially(clazz, fieldsToDisplay));
        attribute.setInverseBy(inverseBy);
        attribute.setCascadePersist(cascadePersist);
        attribute.setCascadeDelete(cascadeDelete);
        attribute.setUnique(unique);
        attribute.setFilterable(filterable);
        return attribute;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<String, Object> representableClazz() {
        if (isNull(clazz)) {
            return null;
        }

        return Map.of(
                "name", clazz.getName(),
                "resourceInfo", clazz.getResourceInfo()
        );
    }
}