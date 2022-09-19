package dsenta.cachito.model.dimension;

import dsenta.queryablemap.trie.Trie;

import java.io.Serializable;

public class Dimensions extends Trie<String, Dimension<?>> implements Serializable {
    private static final long serialVersionUID = -9134635122874409522L;
}