package dsenta.cachito.utils;

import dsenta.cachito.exception.InvalidPaginationParameterException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationOfList {

    public static <T> List<T> getPage(List<T> list, int pageNo, int pageSize) {
        if (pageSize <= 0) {
            throw new InvalidPaginationParameterException("pageSize", pageSize);
        }

        if (pageNo <= 0) {
            throw new InvalidPaginationParameterException("pageNo", pageNo);
        }

        int fromIndex = (pageNo - 1) * pageSize;
        if (isNull(list) || list.size() < fromIndex) {
            return Collections.emptyList();
        }

        return list.subList(fromIndex, Math.min(fromIndex + pageSize, list.size()));
    }
}
