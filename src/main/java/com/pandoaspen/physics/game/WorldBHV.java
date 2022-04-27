package com.pandoaspen.physics.game;

import com.pandoaspen.common.struct.tree.dynamicaabbtree.AABBOverlapFilter;
import com.pandoaspen.common.struct.tree.dynamicaabbtree.AABBTree;
import com.pandoaspen.common.struct.tree.dynamicaabbtree.Boundable;
import com.pandoaspen.common.struct.tree.dynamicaabbtree.Identifiable;
import com.pandoaspen.physics.utils.NMS;
import lombok.Data;
import lombok.Value;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.AABBf;
import org.joml.Rayf;
import org.joml.Vector3f;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

@Data
public class WorldBHV {

    private final GameManager gameManager;

    private AABBTree<SimpleVolume> tree;

    public boolean isInitialized() {
        return tree != null;
    }

    public boolean isInView(Player player, AABBf aabBf) {
        if (aabBf == null) return false;

        float pX = (float) player.getEyeLocation().getX();
        float pY = (float) player.getEyeLocation().getY();
        float pZ = (float) player.getEyeLocation().getZ();

        Set<SimpleVolume> result = new HashSet<>();
        double distanceSquared =
                Math.pow(pX - aabBf.minX, 2) + Math.pow(pY - aabBf.minY, 2) + Math.pow(pZ - aabBf.minZ, 2);
        AABBOverlapFilter<SimpleVolume> overlapFilter =
                simpleVolume -> simpleVolume.getCenter().distanceSquared(pX, pY, pZ) < distanceSquared;

        for (Vector3f v : getVertices(aabBf)) {
            if (testInView(pX, pY, pZ, v.x, v.y, v.z, result, overlapFilter)) return true;
        }

        return false;
    }

    public boolean testInView(float pX, float pY, float pZ, float dX, float dY, float dZ, Set<SimpleVolume> set,
                              AABBOverlapFilter<SimpleVolume> filter) {
        Rayf rayf = new Rayf(pX, pY, pZ, dX - pX, dY - pY, dZ - pZ);
        tree.detectRayIntersection(rayf, filter, set);
        return set.size() == 0;
    }

    public Set<Vector3f> getVertices(AABBf a) {
        Set<Vector3f> set = new HashSet<>();
        set.add(new Vector3f(a.minX, a.minY, a.minZ));
        set.add(new Vector3f(a.maxX, a.minY, a.minZ));
        set.add(new Vector3f(a.minX, a.maxY, a.minZ));
        set.add(new Vector3f(a.minX, a.minY, a.maxZ));
        set.add(new Vector3f(a.maxX, a.maxY, a.minZ));
        set.add(new Vector3f(a.minX, a.maxY, a.maxZ));
        set.add(new Vector3f(a.maxX, a.minY, a.maxZ));
        set.add(new Vector3f(a.maxX, a.maxY, a.maxZ));
        return set;
    }

    public void generateTree(Location l1, Location l2, BiConsumer<Integer, Integer> callback, int callbackStep) {
        if (!l1.getWorld().equals(l2.getWorld())) return;

        tree = new AABBTree<>();

        World world = l1.getWorld();
        Vector start = Vector.getMinimum(l1.toVector(), l2.toVector());
        Vector end = Vector.getMaximum(l1.toVector(), l2.toVector());

        int i = 0;
        int v = (end.getBlockX() - start.getBlockX()) * (end.getBlockZ() - start.getBlockZ());

        for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
            for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
                int yMax = world.getHighestBlockYAt(x, z);
                for (int y = start.getBlockY(); y < Math.min(end.getBlockY(), yMax); y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block == null || block.getType() == Material.AIR) continue;
                    insertBlock(world, x, y, z);
                }

                if (i++ % callbackStep == 0 && callback != null) {
                    callback.accept(i, v);
                }
            }
        }
    }

    private final int[][] directions = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {-1, 0, 0}, {0, -1, 0}, {0, 0, -1}};

    private void insertBlock(World world, int x, int y, int z) {
        Set<AABBf> aabBfs = NMS.getBoundingBoxes(world, x, y, z, true);

        boolean valid = false;

        for (int[] d : directions) {
            if (!world.getBlockAt(x + d[0], y + d[1], z + d[2]).getType().isOccluding()) {
                valid = true;
                break;
            }
        }

        if (!valid) return;

        for (AABBf aabBf : aabBfs) {
            tree.add(new SimpleVolume(aabBf));
        }
    }

    public void save(File file) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileOutputStream fileOut = new FileOutputStream(file);
        DataOutputStream out = new DataOutputStream(fileOut);

        out.writeInt(tree.size());
        out.writeLong(SimpleVolume.GID);

        for (SimpleVolume volume : tree.getAll()) {
            out.writeLong(volume.id);
            out.writeFloat(volume.aabbf.minX);
            out.writeFloat(volume.aabbf.minY);
            out.writeFloat(volume.aabbf.minZ);
            out.writeFloat(volume.aabbf.maxX);
            out.writeFloat(volume.aabbf.maxY);
            out.writeFloat(volume.aabbf.maxZ);
        }

        out.close();
        fileOut.close();
    }

    public void load(File file) throws IOException {
        if (!file.exists()) return;

        this.tree = new AABBTree<>();

        FileInputStream fileIn = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fileIn);

        int size = in.readInt();
        long gid = in.readLong();

        for (int i = 0; i < size; i++) {
            long id = in.readLong();

            float minX = in.readFloat();
            float minY = in.readFloat();
            float minZ = in.readFloat();
            float maxX = in.readFloat();
            float maxY = in.readFloat();
            float maxZ = in.readFloat();

            tree.add(new SimpleVolume(id, minX, minY, minZ, maxX, maxY, maxZ));
        }

        in.close();
        fileIn.close();

        SimpleVolume.GID = gid;
    }

    @Value
    public static class SimpleVolume implements Boundable, Identifiable {

        public static long GID = 0;

        private final long id;
        private final AABBf aabbf;

        public SimpleVolume(AABBf aabBf) {
            this.id = GID++;
            this.aabbf = aabBf;
        }

        public SimpleVolume(long id, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this.id = id;
            this.aabbf = new AABBf(minX, minY, minZ, maxX, maxY, maxZ);
        }

        @Override
        public AABBf getAABB(AABBf dest) {
            return aabbf;
        }

        public Vector3f getCenter() {
            float dX = aabbf.minX + (aabbf.maxX - aabbf.minX) / 2;
            float dY = aabbf.minY + (aabbf.maxY - aabbf.minY) / 2;
            float dZ = aabbf.minZ + (aabbf.maxZ - aabbf.minZ) / 2;
            return new Vector3f(dX, dY, dZ);
        }
    }
}

