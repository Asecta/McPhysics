package com.pandoaspen.common.struct.tree.dynamicaabbtree;

import java.util.Objects;

public final class CollisionPair<T extends Boundable & Identifiable> {
    private final T objectA;
    private final T objectB;

    CollisionPair(T objectA, T objectB) {
        this.objectA = objectA;
        this.objectB = objectB;
    }

    public T getObjectA() {
        return objectA;
    }

    public T getObjectB() {
        return objectB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollisionPair<?> that = (CollisionPair<?>) o;
        return (Objects.equals(objectA.getId(), that.objectA.getId()) ||
                Objects.equals(objectA.getId(), that.objectB.getId())) &&
                (Objects.equals(objectB.getId(), that.objectB.getId()) ||
                        Objects.equals(objectB.getId(), that.objectA.getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectA.getId(), objectB.getId()) ^ Objects.hash(objectB.getId(), objectA.getId());
    }
}
