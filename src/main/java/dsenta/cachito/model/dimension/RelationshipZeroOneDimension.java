package dsenta.cachito.model.dimension;

import java.io.Serializable;

public class RelationshipZeroOneDimension extends RelationshipDimension implements Serializable {
    private static final long serialVersionUID = -8524507340818086584L;

    @Override
    public RelationshipZeroManyDimension toRelationshipZeroManyDimension() {
        RelationshipZeroManyDimension dimension = new RelationshipZeroManyDimension();
        dimension.valueIdsMap = this.valueIdsMap;
        return dimension;
    }

    @Override
    public RelationshipZeroOneDimension toRelationshipZeroOneDimension() {
        return this;
    }
}