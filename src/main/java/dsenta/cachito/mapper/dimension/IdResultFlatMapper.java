package dsenta.cachito.mapper.dimension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdResultFlatMapper {

    /**
     * TODO document this by some example from C++ project and make it more abstract.
     * This algorithm is valuable and it needs to be revisited.
     */
    public static List<Long> flatMapIds(List<List<List<Long>>> queryResult) {
        List<Long> result = new LinkedList<>();

        if (queryResult.isEmpty()) {
            return result;
        }

        List<List<Long>> firstRaw = queryResult
                .get(0)
                .stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());

        while (!firstRaw.isEmpty()) {
            List<Long> front = firstRaw.get(0);
            List<Long> iterateResult = getIterate(front, queryResult, queryResult.size() - 1)
                    .stream()
                    .filter(id -> !result.contains(id))
                    .collect(Collectors.toList());

            if (iterateResult.isEmpty()) {
                firstRaw.remove(0);
            } else {
                result.addAll(iterateResult);
                front.removeAll(iterateResult);

                if (front.isEmpty()) {
                    firstRaw.remove(0);
                }
            }
        }

        return result;
    }

    private static List<Long> getIterate(List<Long> match, List<List<List<Long>>> drv, int fromIndex) {
        if (fromIndex <= 0) {
            return match;
        }

        List<List<Long>> result = drv.get(fromIndex);
        List<Long> iResult = findNext(match, result);

        if (iResult.isEmpty()) {
            removeRest(match, drv, fromIndex - 1);
            return iResult;
        } else {
            List<Long> furtherResult = getIterate(iResult, drv, fromIndex - 1);
            result.remove(iResult);

            return furtherResult;
        }
    }

    private static List<Long> findNext(List<Long> match, List<List<Long>> result) {
        List<Long> findResult = new LinkedList<>();

        for (List<Long> iList : result) {
            var intersectResult = intersect(match, iList)
                    .stream()
                    .filter(id -> !findResult.contains(id))
                    .collect(Collectors.toList());

            findResult.addAll(intersectResult);
        }

        return findResult;
    }

    public static List<Long> intersect(List<Long> l1, List<Long> l2) {
        List<Long> result = new LinkedList<>();

        for (Long id : l1) {
            if (l2.contains(id)) {
                result.add(id);
            }
        }

        return result;
    }

    private static void removeRest(List<Long> match, List<List<List<Long>>> drv, int fromIndex) {
        while (fromIndex > 0) {
            drv.get(fromIndex--).remove(match);
        }
    }
}