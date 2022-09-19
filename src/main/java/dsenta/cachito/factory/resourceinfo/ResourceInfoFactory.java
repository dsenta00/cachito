package dsenta.cachito.factory.resourceinfo;

import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.resource.info.ResourceInfo;
import dsenta.cachito.model.schema.Schema;
import dsenta.cachito.utils.RandomGenerator;
import dsenta.crypto.pes.Pes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceInfoFactory {

    public static ResourceInfo create(String name, Clazz clazz) {
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setName(name);
        resourceInfo.setClazz(clazz);
        resourceInfo.setKey(RandomGenerator.generate256HexString());
        resourceInfo.setFileName(
                Pes.encrypt(generateFileName(resourceInfo), resourceInfo.getKey())
                        .replaceAll("[^a-zA-Z\\d]", "")
                        .substring(0, 32)
        );
        return resourceInfo;
    }

    public static String generateFileName(ResourceInfo resourceInfo) {
        StringBuilder stringBuilder = new StringBuilder();

        Schema schema = resourceInfo.getClazz().getSchema();
        if (nonNull(schema)) {
            if (nonNull(schema.getOwner())) {
                stringBuilder.append(schema.getOwner().getUsername());
                stringBuilder.append("_");
            }

            stringBuilder.append(schema.getName());
            stringBuilder.append("_");
        }

        stringBuilder.append(resourceInfo.getName());

        return stringBuilder.toString();
    }

}