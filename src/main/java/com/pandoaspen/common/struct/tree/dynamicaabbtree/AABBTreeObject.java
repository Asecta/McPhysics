package com.pandoaspen.common.struct.tree.dynamicaabbtree;

import java.util.Objects;

final class AABBTreeObject<E extends Identifiable> {
    private final E data;

    private AABBTreeObject(E data) {
        this.data = data;
    }

    static <E extends Identifiable> AABBTreeObject<E> create(E data) {
        return new AABBTreeObject<>(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AABBTreeObject<?> that = (AABBTreeObject<?>) o;
        return Objects.equals(data.getId(), that.data.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(data.getId());
    }

    E getData() {
        return data;
    }
}
