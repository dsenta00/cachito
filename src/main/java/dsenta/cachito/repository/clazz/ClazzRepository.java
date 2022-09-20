package dsenta.cachito.repository.clazz;

import dsenta.cachito.exception.resource.CannotDropParentResourceException;
import dsenta.cachito.exception.resource.CannotDropResource_RelationshipFromOtherResourceException;
import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.resource.info.ResourceInfo;
import dsenta.queryablemap.trie.Trie;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ClazzRepository {

    private final Map<ResourceInfo, Clazz> clazzMap = new Trie<>();

    public Stream<Clazz> stream() {
        return clazzMap.values().stream();
    }

    public List<Clazz> getChildClazzes(Clazz inputClazz) {
        return clazzMap.values().stream()
                .filter(clazz -> nonNull(clazz.getParentClazz()) && clazz.getParentClazz().compareTo(inputClazz) == 0)
                .collect(Collectors.toList());
    }

    public boolean hasChildClazzes(Clazz inputClazz) {
        return clazzMap.values().stream()
                .anyMatch(clazz -> nonNull(clazz.getParentClazz()) && clazz.getParentClazz().compareTo(inputClazz) == 0);
    }

    public List<Clazz> getRelatedClazzes(Clazz inputClazz) {
        return clazzMap.values().stream()
                .filter(clazz -> clazz.getAttributeCollection().stream().anyMatch(clazzField -> DataType.isRelationship(clazzField.getDataType()) && clazzField.getClazz().compareTo(inputClazz) == 0))
                .collect(Collectors.toList());
    }

    public Optional<Clazz> get(ResourceInfo resourceInfo) {
        return Optional.ofNullable(clazzMap.get(resourceInfo));
    }

    public void create(Clazz inputClazz) {
        if (isNull(inputClazz)) {
            return;
        }

        if (get(inputClazz.getResourceInfo()).isEmpty()) {
            int n = inputClazz.getAttributeCollection().stream().map(Attribute::getPropertyIndex).collect(Collectors.toSet()).size();
            assert n == inputClazz.getAttributeCollection().size() : String.format("Invalid property index set for %s", inputClazz.getResourceInfo().getName());
        }

        clazzMap.put(inputClazz.getResourceInfo(), inputClazz);

        inputClazz.getAttributeCollection().stream()
                .map(Attribute::getClazz)
                .forEach(this::create);
    }

    public void delete(Clazz clazz) {
        boolean isParent = clazzMap.values().stream()
                .anyMatch(c -> nonNull(c.getParentClazz()) && c.getParentClazz().equals(clazz));

        if (isParent) {
            throw new CannotDropParentResourceException(clazz.getName());
        }

        List<Clazz> clazzesRelatedToThisClazz = clazzMap.values().stream()
                .filter(c -> c.getAttributeCollection().stream().anyMatch(attribute -> DataType.isRelationship(attribute.getDataType()) && attribute.getClazz().compareTo(clazz) == 0))
                .filter(c -> c.compareTo(clazz) != 0)
                .collect(Collectors.toList());

        if (!clazzesRelatedToThisClazz.isEmpty()) {
            throw new CannotDropResource_RelationshipFromOtherResourceException(clazz, clazzesRelatedToThisClazz);
        }

        clazzMap.remove(clazz.getResourceInfo());
    }

    public void delete(ResourceInfo resourceInfo) {
        clazzMap.remove(resourceInfo);
    }

    public void put(ResourceInfo resourceInfo, Clazz clazz) {
        clazzMap.put(resourceInfo, clazz);
    }
}