package dsenta.cachito.mapper.objectinstance;

import dsenta.cachito.exception.AttributeValueTypeMismatchException;
import dsenta.cachito.model.attribute.DataType;
import dsenta.cachito.utils.DateIso8601;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static dsenta.cachito.assertions.attribute.AttributeAssert.NOW;
import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateMapper {

    public static Date toDate(Object o) {
        if (isNull(o)) {
            return null;
        } else if (o instanceof Date) {
            return (Date) o;
        } else if (o instanceof Integer) {
            return Date.from(Instant.ofEpochMilli(((Integer) o).longValue()));
        } else if (o instanceof Long) {
            return Date.from(Instant.ofEpochMilli((Long) o));
        } else if (o instanceof String) {
            if (NOW.equalsIgnoreCase((String) o)) {
                return Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant());
            } else {
                return DateIso8601.fromString((String) o);
            }
        }

        throw new AttributeValueTypeMismatchException(DataType.DATE, o);
    }
}