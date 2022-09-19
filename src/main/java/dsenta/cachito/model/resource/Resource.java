package dsenta.cachito.model.resource;

import lombok.Data;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.objectinstance.ObjectInstances;
import dsenta.cachito.model.dimension.Dimension;
import dsenta.cachito.model.dimension.Dimensions;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

@Data
public class Resource implements Serializable {
    private static final long serialVersionUID = 4324601694217270662L;
    private Clazz clazz;
    private ObjectInstances objectInstances = new ObjectInstances();
    private Dimensions dimensions = new Dimensions();

    public Resource(Clazz clazz) {
        setClazz(clazz);
    }

    @SuppressWarnings("unchecked")
    public <K extends Comparable<K>>
    Optional<Dimension<K>> getDimension(String field) {
        return Optional.ofNullable((Dimension<K>) dimensions.get(field));
    }

    public Stream<Attribute> streamDimensionalAttributes() {
        return clazz.getAttributeCollection().stream().filter(Attribute::shouldHaveDimension);
    }
}
