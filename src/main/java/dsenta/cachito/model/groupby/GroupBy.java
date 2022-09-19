package dsenta.cachito.model.groupby;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupBy implements Serializable {
    private String attribute;
    private String interval;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String from;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String to;
    private boolean asc = false;
}
