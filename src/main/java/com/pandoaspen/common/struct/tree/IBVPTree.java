package com.pandoaspen.common.struct.tree;

import com.pandoaspen.common.struct.tree.dynamicaabbtree.Boundable;
import com.pandoaspen.common.struct.tree.dynamicaabbtree.Identifiable;
import org.joml.AABBf;

import java.util.List;

public interface IBVPTree<T extends Boundable & Identifiable> {

    void detectOverlaps(AABBf aabBf, List<T> out);

    void add(T t);

    void remove(T t);

    int size();
}
