package com.pandoaspen.physics.utils;

import net.minecraft.server.v1_16_R3.AxisAlignedBB;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.joml.AABBf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NMS {

    public static Set<AABBf> getBoundingBoxes(Location l, boolean corrected) {
        return getBoundingBoxes(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), corrected);
    }

    public static Set<AABBf> getBoundingBoxes(World world, int x, int y, int z, boolean corrected) {
        BlockPosition blockPosition = new BlockPosition(x, y, z);
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        List<AxisAlignedBB> aabbs =
                worldServer.getType(blockPosition).getCollisionShape(worldServer, blockPosition).d();
        return aabbs.stream().map(aabb -> {
            Vector3f v1 = new Vector3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ);
            Vector3f v2 = new Vector3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ);
            if (corrected) {
                v1 = v1.add(x, y, z);
                v2 = v2.add(x, y, z);
            }
            return new AABBf(v1, v2);
        }).collect(Collectors.toSet());
    }
}
