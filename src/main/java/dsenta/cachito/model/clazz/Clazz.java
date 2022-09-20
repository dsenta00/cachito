package dsenta.cachito.model.clazz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import dsenta.cachito.exception.attribute.AttributeDoesNotExistException;
import dsenta.cachito.mapper.clazz.ClazzToClonedClazzMapper;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.fields.FieldsToDisplay;
import dsenta.cachito.model.resource.info.ResourceInfo;
import dsenta.cachito.model.schema.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@NoArgsConstructor
public final class Clazz implements Serializable, Comparable<Clazz> {
    private static final long serialVersionUID = -4450891152025863231L;
    private boolean cache = false;
    private boolean simple = false;
    @JsonIgnore
    private ResourceInfo resourceInfo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Clazz parentClazz;
    private Map<String, Attribute> attributes = new HashMap<>();
    @JsonIgnore
    private Schema schema;

    public Clazz clonePartially(FieldsToDisplay fieldsToDisplay) {
        return ClazzToClonedClazzMapper.clonePartially(this, fieldsToDisplay);
    }

    public String getSchemaName() {
        return resourceInfo.getClazz().getSchema().getName();
    }

    public String getName() {
        return resourceInfo.getName();
    }

    public Attribute getAttributeRecursive(String name) {
        Attribute attribute = attributes.get(name);

        if (nonNull(attribute)) {
            return attribute;
        }

        if (isNull(parentClazz)) {
            throw new AttributeDoesNotExistException(name);
        }

        return parentClazz.getAttributeRecursive(name);
    }

    public Optional<Attribute> getAttribute(String name) {
        return Optional.ofNullable(attributes.get(name));
    }

    @JsonIgnore
    public Collection<Attribute> getAttributeCollection() {
        return attributes.values();
    }

    @Override
    public int hashCode() {
        return this.resourceInfo.hashCode();
    }

    @Override
    public int compareTo(Clazz clazz) {
        return resourceInfo.compareTo(clazz.resourceInfo);
    }

    @Override
    public String toString() {
        return resourceInfo.getName();
    }

    public void setAttributeList(List<Attribute> attributes) {
        this.attributes = attributes
                .stream()
                .collect(Collectors.toMap(Attribute::getName, attribute -> attribute));
    }
}