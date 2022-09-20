package dsenta.cachito.mapper.objectinstance;

import dsenta.cachito.exception.attribute.AttributeValueTypeMismatchException;
import dsenta.cachito.model.attribute.DataType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FloatMapper {

    public static Double toDouble(Object o) {
        if (isNull(o)) {
            return null;
        }

        try {
            return o instanceof Double ? (Double) o : Double.parseDouble(o.toString());
        } catch (Exception e) {
            throw new AttributeValueTypeMismatchException(DataType.FLOAT, o);
        }
    }
}
