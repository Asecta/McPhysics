package com.pandoaspen.common.struct.tree.dynamicaabbtree;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface CollisionFilter<T extends Boundable & Identifiable> extends BiPredicate<T, T> {
}
