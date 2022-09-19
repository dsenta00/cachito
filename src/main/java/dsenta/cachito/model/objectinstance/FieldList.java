package dsenta.cachito.model.objectinstance;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.isStatic;

public class FieldList extends ArrayList<Field> {

    public static List<Field> from(Class<?> c) {
        List<Field> fields = Arrays.stream(c.getDeclaredFields())
                .filter(field -> !isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toList());

        if (Objects.nonNull(c.getSuperclass())) {
            fields.addAll(from(c.getSuperclass()));
        }

        return fields;
    }
}