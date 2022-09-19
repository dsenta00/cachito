package dsenta.cachito.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.model.objectinstance.FieldList;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomObjectMapper {

    private static final Set<String> PRIMITIVE_DATA_TYPES = Set.of(
            Boolean.class.getSimpleName(),
            Integer.class.getSimpleName(),
            Long.class.getSimpleName(),
            Float.class.getSimpleName(),
            Double.class.getSimpleName(),
            String.class.getSimpleName(),
            Date.class.getSimpleName()
    );

    private static final Set<String> ARRAY_TYPES = Set.of(
            Set.class.getSimpleName(),
            List.class.getSimpleName(),
            Collection.class.getSimpleName(),
            "ListN",
            "List12",
            LinkedList.class.getSimpleName(),
            ArrayList.class.getSimpleName()
    );

    @SuppressWarnings("unchecked")
    public static Map<String, Object> convert(Object object) {
        return (object instanceof Map) ?
                (Map<String, Object>) object :
                convert(object, new HashMap<>());
    }

    private static Map<String, Object> convert(Object object, Map<Object, Map<String, Object>> visited) {
        if (isNull(object)) {
            return new LinkedHashMap<>();
        }

        if (visited.containsKey(object)) {
            return visited.get(object);
        }

        Map<String, Object> objectMap = new LinkedHashMap<>();
        visited.put(object, objectMap);

        FieldList
                .from(object.getClass())
                .forEach(field -> {
                    try {
                        Object value = field.get(object);

                        if (isNull(value)) {
                            if (!objectMap.containsKey(field.getName())) {
                                objectMap.put(field.getName(), null);
                            }
                        } else if (PRIMITIVE_DATA_TYPES.contains(value.getClass().getSimpleName())) {
                            objectMap.put(field.getName(), value);
                        } else if (value instanceof Enum) {
                            objectMap.put(field.getName(), value.toString());
                        } else if (value instanceof Instant) {
                            objectMap.put(field.getName(), ((Instant) value).toEpochMilli());
                        } else if (value instanceof Date) {
                            objectMap.put(field.getName(), ((Date) value).toInstant().toEpochMilli());
                        } else if (Object[].class.getSimpleName().equals(value.getClass().getSimpleName())) {
                            objectMap.put(field.getName(), Arrays
                                    .stream((Object[]) value)
                                    .filter(Objects::nonNull)
                                    .map(o -> visited.containsKey(o) ? visited.get(o) : convert(o, visited))
                                    .collect(Collectors.toList()));
                        } else if (ARRAY_TYPES.contains(value.getClass().getSimpleName())) {
                            objectMap.put(field.getName(), ((Collection<?>) value)
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .map(o -> visited.containsKey(o) ? visited.get(o) : convert(o, visited))
                                    .collect(Collectors.toList()));
                        } else if (visited.containsKey(value)) {
                            objectMap.put(field.getName(), visited.get(value));
                        } else {
                            objectMap.put(field.getName(), convert(value, visited));
                        }
                    } catch (IllegalAccessException ignored) {
                    }
                });

        return objectMap;
    }
}