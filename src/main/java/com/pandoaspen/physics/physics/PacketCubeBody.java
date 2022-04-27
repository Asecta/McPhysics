package com.pandoaspen.physics.physics;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.UUID;

@Data
public class PacketCubeBody implements PhysicsBody {

    private static int ENT_ID = 9324213;

    private final Location initialPosition;
    private final Material type;

    private final float mass;
    private final float restitution;

    private ProtocolManager protocolManager;

    private int id;
    private UUID uuid;

    @Override
    public void init() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.id = ENT_ID++;
        this.uuid = UUID.randomUUID();

        PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        spawnPacket.getIntegers().write(0, id);
        spawnPacket.getUUIDs().write(0, uuid);
        spawnPacket.getIntegers().write(1, 1);
        spawnPacket.getDoubles().write(0, initialPosition.getX());
        spawnPacket.getDoubles().write(1, initialPosition.getY());
        spawnPacket.getDoubles().write(2, initialPosition.getZ());

        PacketContainer metadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, id);

        WrappedDataWatcher dataWatcher =
                new WrappedDataWatcher(metadataPacket.getWatchableCollectionModifier().read(0));
        WrappedDataWatcher.WrappedDataWatcherObject invisIndex =
                new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
        dataWatcher.setObject(invisIndex, (byte) 0x20);
        metadataPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        PacketContainer equipmentPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipmentPacket.getIntegers().write(0, id);

        Pair<EnumWrappers.ItemSlot, ItemStack> itemStackPair =
                new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(type));
        equipmentPacket.getSlotStackPairLists().write(0, Arrays.asList(itemStackPair));

        protocolManager.broadcastServerPacket(spawnPacket);
        protocolManager.broadcastServerPacket(metadataPacket);
        protocolManager.broadcastServerPacket(equipmentPacket);
    }

    public boolean teleport(Vector3f location) {
        PacketContainer teleportPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportPacket.getIntegers().write(0, id);
        teleportPacket.getDoubles().write(0, (double) location.x);
        teleportPacket.getDoubles().write(1, (double) location.y);
        teleportPacket.getDoubles().write(2, (double) location.z);

        teleportPacket.getBytes().write(0, (byte) 192);
        protocolManager.broadcastServerPacket(teleportPacket);
        return true;
    }

    public void setHeadPose(float pitch, float yaw, float roll) {
        PacketContainer metadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, id);

        WrappedDataWatcher dataWatcher =
                new WrappedDataWatcher(metadataPacket.getWatchableCollectionModifier().read(0));
        WrappedDataWatcher.WrappedDataWatcherObject index =
                new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.getVectorSerializer());
        dataWatcher.setObject(index, new Vector3F(pitch, yaw, 0));

        metadataPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        protocolManager.broadcastServerPacket(metadataPacket);
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f((float) initialPosition.getX(), (float) initialPosition.getY(),
                (float) initialPosition.getZ());
    }

    @Override
    public float getSize() {
        return .5f;
    }

    @Override
    public void transform(Vector3f origin, Matrix4f transform) {
        //        Vector3f origin = new Vector3f();
        //        origin.add(transform.origin);
        //
        //        Vector3f direction = new Vector3f(1, 0, 0);
        //        transform.basis.transform(direction);
        //
        //        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x));
        //        float pitch = (float) Math.toDegrees(Math.atan2(Math.sqrt(direction.z * direction.z + direction.x *
        //        direction.x), direction.y));
        //
        //        //        System.out.println(String.format("X: %.5f Y: %.5f Z: %.5f", direction.x, direction.y,
        //        direction.z));
        //        //      System.out.println(String.format("Pitch: %.5f Yaw: %.5f", pitch, yaw));
        //
        //        setHeadPose(pitch, yaw, 0);
        //
        //
        //        origin.sub(new Vector3f(0f, 1.44133f, 0f));
        //        float width = .25f;
        //        direction.mul(width);
        //        direction.mul(-1);
        //        origin.add(direction);
        //        teleport(origin);
    }

    @Override
    public void remove() {
        PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, new int[]{id});
        protocolManager.broadcastServerPacket(destroyPacket);
    }

    @Override
    public void setHeadAngles(float x, float y, float z) {

    }
}
/*
    @Override
    public void transform(Transform transform) {
        Vector3f origin = new Vector3f();
        origin.add(transform.origin);

        Vector3f direction = new Vector3f(1, 0, 0);
        transform.basis.transform(direction);

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x));
        float pitch = (float) Math.toDegrees(Math.atan2(Math.sqrt(direction.z * direction.z + direction.x * direction
        .x), direction.y));

        //        System.out.println(String.format("X: %.5f Y: %.5f Z: %.5f", direction.x, direction.y, direction.z));
        //      System.out.println(String.format("Pitch: %.5f Yaw: %.5f", pitch, yaw));

        setHeadPose(pitch, yaw, 0);

        origin.sub(new Vector3f(0f, 1.44133f, 0f));

        float width = .25f;
        direction.scale(width);
        direction.scale(-1);

        origin.add(direction);
        teleport(origin);
    }
    private static int ENT_ID = 9324213;

    private final Location initialPosition;
    private final Material type;

    private final float mass;
    private final float restitution;

    private ProtocolManager protocolManager;

    private int id;
    private UUID uuid;

    @Override
    public void init() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.id = ENT_ID++;
        this.uuid = UUID.randomUUID();

        PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        spawnPacket.getIntegers().write(0, id);
        spawnPacket.getUUIDs().write(0, uuid);
        spawnPacket.getIntegers().write(1, 1);
        spawnPacket.getDoubles().write(0, initialPosition.getX());
        spawnPacket.getDoubles().write(1, initialPosition.getY());
        spawnPacket.getDoubles().write(2, initialPosition.getZ());

        PacketContainer metadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, id);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadataPacket.getWatchableCollectionModifier().read
        (0));
        WrappedDataWatcher.WrappedDataWatcherObject invisIndex = new WrappedDataWatcher.WrappedDataWatcherObject(0,
        WrappedDataWatcher.Registry.get(Byte.class));
        dataWatcher.setObject(invisIndex, (byte) 0x20);
        metadataPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        PacketContainer equipmentPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipmentPacket.getIntegers().write(0, id);

        Pair<EnumWrappers.ItemSlot, ItemStack> itemStackPair = new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack
        (type));
        equipmentPacket.getSlotStackPairLists().write(0, Arrays.asList(itemStackPair));

        protocolManager.broadcastServerPacket(spawnPacket);
        protocolManager.broadcastServerPacket(metadataPacket);
        protocolManager.broadcastServerPacket(equipmentPacket);
    }

    public void teleport(Vector3f location) {
        PacketContainer teleportPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportPacket.getIntegers().write(0, id);
        teleportPacket.getDoubles().write(0, (double) location.x);
        teleportPacket.getDoubles().write(1, (double) location.y);
        teleportPacket.getDoubles().write(2, (double) location.z);

        teleportPacket.getBytes().write(0, (byte) 192);
        protocolManager.broadcastServerPacket(teleportPacket);
    }

    public void setHeadPose(float pitch, float yaw, float roll) {
        PacketContainer metadataPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, id);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadataPacket.getWatchableCollectionModifier().read
        (0));
        WrappedDataWatcher.WrappedDataWatcherObject index = new WrappedDataWatcher.WrappedDataWatcherObject(15,
        WrappedDataWatcher.Registry.getVectorSerializer());
        dataWatcher.setObject(index, new Vector3F(pitch, yaw, 0));

        metadataPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

        protocolManager.broadcastServerPacket(metadataPacket);
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f((float) initialPosition.getX(), (float) initialPosition.getY(), (float) initialPosition
        .getZ());
    }

    @Override
    public Vector3f getSize() {
        return new Vector3f(.5f, .5f, .5f);
    }


    @Override
    public void transform(Transform transform) {
        Vector3f origin = new Vector3f();
        origin.add(transform.origin);

        Vector3f direction = new Vector3f(1, 0, 0);
        transform.basis.transform(direction);

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x));
        float pitch = (float) Math.toDegrees(Math.atan2(Math.sqrt(direction.z * direction.z + direction.x * direction
        .x), direction.y));

        //        System.out.println(String.format("X: %.5f Y: %.5f Z: %.5f", direction.x, direction.y, direction.z));
        //      System.out.println(String.format("Pitch: %.5f Yaw: %.5f", pitch, yaw));

        setHeadPose(pitch, yaw, 0);


        origin.sub(new Vector3f(0f, 1.44133f, 0f));
        float width = .25f;
        direction.scale(width);
        direction.scale(-1);
        origin.add(direction);
        teleport(origin);
    }

    @Override
    public void remove() {
        PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, new int[]{id});
        protocolManager.broadcastServerPacket(destroyPacket);
    }

 */