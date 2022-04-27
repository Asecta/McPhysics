package com.pandoaspen.physics;

import co.aikar.commands.PaperCommandManager;
import com.pandoaspen.physics.command.PhysicsCommand;
import com.pandoaspen.physics.game.GameManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PhysicsPlugin extends JavaPlugin implements Listener {

    public static PhysicsPlugin INSTANCE;

    private GameManager gameManager;


    public PhysicsPlugin() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        PaperCommandManager commandManager = new PaperCommandManager(this);

        PhysicsCommand physicsCommand = new PhysicsCommand(this);

        this.gameManager = new GameManager();
        this.gameManager.initialize();

        commandManager.registerCommand(physicsCommand);

        getServer().getPluginManager().registerEvents(new PhysicsListener(gameManager), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, gameManager::tick, 1, 1);

        gameManager.getPhysicsWorld().start();
        print("Physics world built.");
    }

    @Override
    public void onDisable() {
        if (gameManager.getPhysicsWorld().isBuilt()) {
            gameManager.getPhysicsWorld().clearBodies();
            gameManager.getPhysicsWorld().getDynamicsWorld().destroy();
        }
    }

    public void print(String string, Object... args) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format(string, args)));
    }
}