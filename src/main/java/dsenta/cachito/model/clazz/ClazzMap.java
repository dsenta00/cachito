package dsenta.cachito.model.clazz;

import dsenta.cachito.model.resource.info.ResourceInfo;
import dsenta.queryablemap.trie.Trie;

import java.io.Serializable;

public class ClazzMap extends Trie<ResourceInfo, Clazz> implements Serializable {
    private static final long serialVersionUID = 4500528539047716022L;
}