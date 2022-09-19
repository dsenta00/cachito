package dsenta.cachito.repository.clazz;

import dsenta.cachito.model.attribute.Attribute;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.resource.info.ResourceInfo;
import dsenta.queryablemap.trie.Trie;

import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class ClazzRepository_Simple {

    private final Trie<String, Clazz> clazzMap = new Trie<>();

    public Optional<Clazz> get(String name) {
        return Optional.ofNullable(clazzMap.get(name));
    }

    public void create(Clazz inputClazz) {
        if (isNull(inputClazz)) {
            return;
        }

        if (get(inputClazz.getResourceInfo().getName()).isEmpty()) {
            int n = inputClazz.getAttributeCollection().stream().map(Attribute::getPropertyIndex).collect(Collectors.toSet()).size();
            assert n == inputClazz.getAttributeCollection().size() : String.format("Invalid property index set for %s", inputClazz.getResourceInfo().getName());
        }

        clazzMap.put(inputClazz.getResourceInfo().getName(), inputClazz);
    }

    public void delete(Clazz clazz) {
        clazzMap.remove(clazz.getResourceInfo().getName());
    }

    public void delete(ResourceInfo resourceInfo) {
        clazzMap.remove(resourceInfo.getName());
    }

    public void put(ResourceInfo resourceInfo, Clazz clazz) {
        clazzMap.put(resourceInfo.getName(), clazz);
    }
}