package com.pandoaspen.physics.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.pandoaspen.physics.PhysicsPlugin;
import com.pandoaspen.physics.game.GameManager;
import com.pandoaspen.physics.physics.CubeBody;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandAlias("phys")
@RequiredArgsConstructor
public class PhysicsCommand extends BaseCommand {

    public static float force = 10;

    private final PhysicsPlugin plugin;


    @Subcommand("force")
    public void cmdForce(Player sender, float d) {
        force = d;
    }

    @Subcommand("clear")
    public void cmdClear(Player sender) {
        getGameManager().getPhysicsWorld().clearBodies();
    }

    @Subcommand("wall")
    public void cmdWall(Player player, float radius, float mass, float restitution, int count, String mat) {

        double spacing = radius * 2;
        Material material = Material.getMaterial(mat);

        for (double x = 0; x < spacing * count; x += spacing) {
            for (double y = 0; y < spacing * count; y += spacing) {
                Location location = player.getLocation();
                location.add(x, y + radius + .1, 0);
                location.setDirection(new Vector(1, 0, 0));
                CubeBody PacketCubeBody = new CubeBody(location, material, mass, restitution);
                getGameManager().getPhysicsWorld().addPhysicsObject(PacketCubeBody);
            }
        }
    }


    public GameManager getGameManager() {
        return plugin.getGameManager();
    }

}