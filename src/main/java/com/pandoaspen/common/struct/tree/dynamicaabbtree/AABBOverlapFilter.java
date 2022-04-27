package com.pandoaspen.common.struct.tree.dynamicaabbtree;

import java.util.function.Predicate;

@FunctionalInterface
public interface AABBOverlapFilter<T extends Boundable & Identifiable> extends Predicate<T> {

}
