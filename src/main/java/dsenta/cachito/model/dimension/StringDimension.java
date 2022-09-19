package dsenta.cachito.model.dimension;

import dsenta.queryablemap.trie.Trie;

public class StringDimension extends Dimension<String> {
    private static final long serialVersionUID = 5352872108783113926L;

    public StringDimension() {
        super(new Trie<>(), String.class);
    }

    @Override
    public StringDimension toStringDimension() {
        return this;
    }

    @Override
    public boolean isRelationship() {
        return false;
    }
}