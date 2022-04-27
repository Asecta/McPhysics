package com.pandoaspen.common.struct.tree.dynamicaabbtree;

import org.joml.AABBf;

@FunctionalInterface
public interface AABBTreeHeuristicFunction<T extends Boundable> {
    HeuristicResult getInsertionHeuristic(AABBf left, AABBf right, T object, AABBf objectAABB);

    enum HeuristicResult {
        LEFT, RIGHT
    }
}
