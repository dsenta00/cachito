package dsenta.cachito.mapper.objectinstance;

import dsenta.cachito.exception.attribute.AttributeValueTypeMismatchException;
import dsenta.cachito.model.attribute.DataType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntMapper {

    public static Long toLongInt(Object o) {
        if (isNull(o)) {
            return null;
        }

        try {
            return o instanceof Long ? (Long) o : Long.parseLong(o.toString());
        } catch (Exception e) {
            throw new AttributeValueTypeMismatchException(DataType.INTEGER, o);
        }
    }
}