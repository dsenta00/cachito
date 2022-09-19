package dsenta.cachito.model.objectinstance;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.attribute.Attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@NoArgsConstructor
@AllArgsConstructor
public class ObjectInstance implements Serializable {
    private static final long serialVersionUID = 7524570374694567850L;
    private Object[] properties;

    public List<Object> getProperties() {
        return new ArrayList<>(Arrays.asList(properties));
    }

    public void setProperties(Object[] properties) {
        this.properties = properties;
    }

    public Object get(Attribute attribute) {
        return this.properties[attribute.getPropertyIndex()];
    }

    public ObjectInstance deepClone() {
        return new ObjectInstance(Arrays.copyOf(properties, properties.length));
    }

    public void set(Attribute attribute, Object value) {
        if (isNull(attribute)) {
            return;
        }

        if (isNull(value)) {
            assert Objects.nonNull(attribute.getDefaultValue()) : String.format("Field %s should not be null!", attribute.getName());
            value = attribute.getDefaultValue();
        }

        this.setUnsafe(attribute, value);
    }

    public void setUnsafe(Attribute attribute, Object value) {
        switch (attribute.getDataType()) {
            case BOOLEAN:
            case INTEGER:
            case FLOAT:
            case STRING:
            case DATE:
                this.properties[attribute.getPropertyIndex()] = value;
                break;
        }
    }
}