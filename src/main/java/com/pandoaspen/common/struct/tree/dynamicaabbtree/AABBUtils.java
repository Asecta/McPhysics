package com.pandoaspen.common.struct.tree.dynamicaabbtree;

import org.joml.AABBf;

/**
 * Created by pateman.
 */
public final class AABBUtils {

    private AABBUtils() {

    }

    public static float getWidth(AABBf aabb) {
        return aabb.maxX - aabb.minX;
    }

    public static float getHeight(AABBf aabb) {
        return aabb.maxY - aabb.minY;
    }

    public static float getDepth(AABBf aabb) {
        return aabb.maxZ - aabb.minZ;
    }

    public static float getArea(AABBf aabb) {
        final float width = getWidth(aabb);
        final float height = getHeight(aabb);
        final float depth = getDepth(aabb);
        return 2.0f * (width * height + width * depth + height * depth);
    }
}
