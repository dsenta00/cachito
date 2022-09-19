package dsenta.cachito.model.objectinstance;

import dsenta.queryablemap.wgb.WhiteGreyBlackTree;

import java.io.Serializable;

/*
 * TODO
 *
 * The ID can be converted to string, that could make this as Trie
 * In that case, all is UUID
 * But dimensions are having list of longs, which is good, and having list of string is bad
 * Consider this to see if it's worth
 */
public class ObjectInstances extends WhiteGreyBlackTree<Long, ObjectInstance> implements Serializable {
    private static final long serialVersionUID = -5126492591241465630L;
}