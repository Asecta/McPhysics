package com.pandoaspen.physics;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.pandoaspen.physics.command.PhysicsCommand;
import com.pandoaspen.physics.game.GameManager;
import com.pandoaspen.physics.physics.CubeBody;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import javax.vecmath.Vector3f;

@RequiredArgsConstructor
public class PhysicsListener implements Listener {

    public final GameManager gameManager;

    public boolean applyForces(Player player) {

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.DIAMOND_SHOVEL) return false;
        Location l = player.getEyeLocation();

        Vector3f from = new Vector3f((float) l.getX(), (float) l.getY(), (float) l.getZ());

        Vector l2 = player.getLocation().getDirection().multiply(20).add(l.toVector());

        Vector3f to = new Vector3f((float) l2.getX(), (float) l2.getY(), (float) l2.getZ());

        Vector dir = player.getLocation().getDirection().normalize();
        Vector3f force = new Vector3f((float) dir.getX(), (float) dir.getY(), (float) dir.getZ());
        force.scale(PhysicsCommand.force);

        gameManager.getPhysicsWorld().getDynamicsWorld().rayTest(from, to, new CollisionWorld.RayResultCallback() {
            @Override
            public float addSingleResult(CollisionWorld.LocalRayResult rayResult, boolean normalInWorldSpace) {
                if (rayResult.collisionObject instanceof RigidBody) {
                    rayResult.collisionObject.activate();
                    ((RigidBody) rayResult.collisionObject).applyCentralForce(force);
                }
                return 0;
            }
        });

        return true;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (applyForces(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (applyForces(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ARMOR_STAND) return;
        if (applyForces(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract2(PlayerInteractEvent event) {
        if (event.getMaterial() == null || event.getMaterial() != Material.GOLDEN_SHOVEL) return;
        //        if (event.getAction() == Action.PHYSICAL) return;

        Location l = event.getPlayer().getEyeLocation();

        CubeBody PacketCubeBody = new CubeBody(l, Material.DIAMOND_BLOCK, .2f, .2f);
        RigidBody rigidBody = gameManager.getPhysicsWorld().addPhysicsObject(PacketCubeBody);

        Vector d = event.getPlayer().getLocation().getDirection();

        Vector3f velocity = new Vector3f((float) d.getX(), (float) d.getY(), (float) d.getZ());
        velocity.scale(PhysicsCommand.force);

        rigidBody.applyCentralForce(velocity);

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

}
