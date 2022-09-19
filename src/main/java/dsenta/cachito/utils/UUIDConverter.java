package dsenta.cachito.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UUIDConverter {

    public static long asLong(String uuid) {
        try {
            return UUID.fromString(uuid).getMostSignificantBits() & Long.MAX_VALUE;
        } catch (IllegalArgumentException e) {
            return Long.parseLong(uuid);
        }
    }
}