package dsenta.cachito.dimension.mapper;

import dsenta.cachito.mapper.dimension.IdResultFlatMapper;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

class IdResultFlatMapperTest {

    /**
     * Test case from Master thesis of Duje Senta
     */
    @Test
    void flatMapIds_basic() {
        var table = new LinkedList<List<List<Long>>>();

        table.add(new LinkedList<>(of(of(3L, 4L), of(1L, 7L, 9L), of(2L, 5L, 6L))));
        table.add(new LinkedList<>(of(of(1L, 3L), of(4L, 7L), of(2L, 5L, 6L))));
        table.add(new LinkedList<>(of(of(5L, 7L, 8L), of(1L, 3L, 4L), of(2L, 6L))));

        var sut = IdResultFlatMapper.flatMapIds(table);

        assertThat(sut).hasSize(7).containsExactly(3L, 4L, 1L, 7L, 5L, 2L, 6L);
    }

    @Test
    void flatMapIds_shouldNotHaveDuplicates() {
        var table = new LinkedList<List<List<Long>>>();

        table.add(new LinkedList<>(of(of(2L), of(2L))));
        table.add(new LinkedList<>(of(of(2L), of(2L))));

        var sut = IdResultFlatMapper.flatMapIds(table);

        assertThat(sut).hasSize(1).containsExactly(2L);
    }
}