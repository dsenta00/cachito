package dsenta.cachito.model.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pagination implements Serializable {
    private int perPage;
    private int pageNo;
}