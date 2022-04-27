package com.pandoaspen.physics.physics;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.pandoaspen.physics.utils.StandUtils;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Data
public class CubeBody implements PhysicsBody {

    private static int ENT_ID = 9324213;

    private final Location initialPosition;
    private final Material type;

    private final float mass;
    private final float restitution;

    private ProtocolManager protocolManager;

    private ArmorStand host;

    private Vector3f currentPosition;

    @Override
    public void init() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        host = (ArmorStand) initialPosition.getWorld().spawnEntity(initialPosition, EntityType.ARMOR_STAND);
        host.getEquipment().setHelmet(new ItemStack(type));
        host.setGravity(false);
        host.setInvisible(true);
        host.setArms(true);
        currentPosition = getPosition();
    }

    public boolean teleport(Vector3f location) {
        if (location.distance(currentPosition) < 0.05 || location.equals(currentPosition)) return false;
        currentPosition = location;

        host.teleport(new Location(initialPosition.getWorld(), location.x, location.y, location.z, -90, 0));
        return true;
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

        StandUtils.positionStand(this, origin, transform);
    }

    public void setHeadAngles(float x, float y, float z) {
        host.setHeadPose(new EulerAngle(z, y, x));
    }

    public void setArmAngles(float x, float y, float z) {
        host.setRightArmPose(new EulerAngle(z, y, x));
    }

    @Override
    public void remove() {
        host.remove();
    }
}
