package dsenta.cachito;

import dsenta.cachito.builder.PersistableCachitoBuilder;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.persistence.Persistence;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Cachito {

    public static PersistableCachitoBuilder cache() {
        return new PersistableCachitoBuilder();
    }

    public static PersistableCachitoBuilder persistable(Persistence persistence) {
        return PersistableCachitoBuilder.builder()
                .persistence(persistence)
                .build();
    }

    public static PersistableCachitoBuilder clazz(Clazz clazz) {
        return PersistableCachitoBuilder.builder()
                .clazz(clazz)
                .build();
    }
}