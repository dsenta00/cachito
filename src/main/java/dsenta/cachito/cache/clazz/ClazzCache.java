package dsenta.cachito.cache.clazz;

import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.repository.clazz.ClazzRepository;
import dsenta.cachito.repository.clazz.ClazzRepository_Simple;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClazzCache {

    private static final ClazzRepository repository = new ClazzRepository();
    private static final ClazzRepository_Simple repositorySimple = new ClazzRepository_Simple();

    public static void create(Clazz clazz) {
        if (clazz.isSimple()) {
            repositorySimple.create(clazz);
        } else {
            repository.create(clazz);
        }
    }

    public static void delete(Clazz clazz) {
        if (clazz.isSimple()) {
            repositorySimple.delete(clazz.getResourceInfo());
        } else {
            repository.delete(clazz.getResourceInfo());
        }
    }

    public static ClazzRepository stream() {
        return repository;
    }

    public static ClazzRepository_Simple simpleStream() {
        return repositorySimple;
    }

    public static void put(Clazz clazz) {
        if (clazz.isSimple()) {
            repositorySimple.put(clazz.getResourceInfo(), clazz);
        } else {
            repository.put(clazz.getResourceInfo(), clazz);
        }
    }
}