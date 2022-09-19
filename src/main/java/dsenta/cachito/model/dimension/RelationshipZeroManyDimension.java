package dsenta.cachito.model.dimension;

import java.io.Serializable;

public class RelationshipZeroManyDimension extends RelationshipDimension implements Serializable {
    private static final long serialVersionUID = 51636800356214079L;

    @Override
    public RelationshipZeroManyDimension toRelationshipZeroManyDimension() {
        return this;
    }
}