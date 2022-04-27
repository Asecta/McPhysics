package com.pandoaspen.common.struct.tree;

import com.pandoaspen.common.struct.tree.dynamicaabbtree.Boundable;
import com.pandoaspen.common.struct.tree.dynamicaabbtree.Identifiable;
import org.joml.AABBf;

import java.util.ArrayList;
import java.util.List;

public class FakeBVP<T extends Boundable & Identifiable> implements IBVPTree<T> {

    private List<T> list = new ArrayList<>();

    @Override
    public void detectOverlaps(AABBf aabBf, List<T> out) {

        List<T> results = new ArrayList<>();

        for (T node : list) {
            AABBf target = node.getAABB(new AABBf());

            if (aabBf.testAABB(target)) {
                results.add(node);
            }

        }
    }

    @Override
    public void add(T t) {
        list.add(t);
    }

    @Override
    public void remove(T t) {
        list.remove(t);
    }

    @Override
    public int size() {
        return list.size();
    }
}
