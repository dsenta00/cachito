package dsenta.cachito.model.leftjoin;

import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.filter.AndWhere;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LeftJoinWithClazz implements Serializable {
    private Clazz clazz;
    private String attribute;
    private List<AndWhere> where = new LinkedList<>();
}