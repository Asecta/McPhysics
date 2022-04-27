package com.pandoaspen.physics.physics;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class WalkableCubeBody extends CubeBody {

    private ArmorStand shulkerStand;
    private Shulker shulker;

    public WalkableCubeBody(Location initialPosition, Material type, float mass, float restitution) {
        super(initialPosition, type, mass, restitution);
    }

    @Override
    public void init() {
        super.init();

        shulkerStand =
                (ArmorStand) getInitialPosition().getWorld().spawnEntity(getInitialPosition(), EntityType.ARMOR_STAND);
        shulkerStand.setGravity(false);
        shulkerStand.setInvisible(true);

        shulker = (Shulker) getInitialPosition().getWorld().spawnEntity(getInitialPosition(), EntityType.SHULKER);
        shulker.setAI(false);
        shulker.setInvulnerable(true);
        shulker.setGravity(false);
        shulker.setInvisible(true);

        shulkerStand.addPassenger(shulker);

        shulker.setCollidable(false);
        shulkerStand.setCollidable(false);
    }

    @Override
    public Location getInitialPosition() {
        return new Location(super.getInitialPosition().getWorld(), super.getInitialPosition().getX(),
                super.getInitialPosition().getY(), super.getInitialPosition().getZ(), 0, 0);
    }

    @Override
    public boolean teleport(Vector3f location) {
        if (!super.teleport(location)) {
            return false;
        }
        Location loc = new Location(getInitialPosition().getWorld(), location.x, location.y, location.z, -90, 0);
        loc.subtract(0, .8f, 0);

        try {
            methods[1].invoke(methods[0].invoke(shulkerStand), loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        } catch (Exception ex) {
        }

        PacketContainer teleportPacket = getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportPacket.getIntegers().write(0, shulkerStand.getEntityId());
        teleportPacket.getDoubles().write(0, (double) loc.getX());
        teleportPacket.getDoubles().write(1, (double) loc.getY() + .1);
        teleportPacket.getDoubles().write(2, (double) loc.getZ());

        teleportPacket.getBooleans().write(0, true);

        teleportPacket.getBytes().write(0, (byte) 192);
        getProtocolManager().broadcastServerPacket(teleportPacket);

        return true;

    }

    @Override
    public void remove() {
        super.remove();
        shulker.remove();
        shulkerStand.remove();
    }

    @Override
    public void transform(Vector3f origin, Matrix4f transform) {
        super.transform(origin, transform);
    }

    private final Method[] methods = ((Supplier<Method[]>) () -> {
        try {
            Method getHandle =
                    Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftEntity")
                            .getDeclaredMethod("getHandle");
            return new Method[]{getHandle,
                    getHandle.getReturnType().getDeclaredMethod("setPositionRotation", double.class, double.class,
                            double.class, float.class, float.class)};
        } catch (Exception ex) {
            return null;
        }
    }).get();
}
