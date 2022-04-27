package com.pandoaspen.common.struct.tree.dynamicaabbtree;


import org.joml.AABBf;

/**
 * Created by pateman.
 */
public interface Boundable {

    AABBf getAABB(AABBf dest);
}
