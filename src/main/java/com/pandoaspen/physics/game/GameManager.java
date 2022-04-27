package com.pandoaspen.physics.game;

import com.pandoaspen.physics.physics.PhysicsWorld;
import lombok.Data;

@Data
public class GameManager {

    private WorldBHV worldBHV;
    private PhysicsWorld physicsWorld;

    public void initialize() {
        // this.agentManager = new AgentManager(this, new File(ZombieVWorldsPlugin.FILE_PATH));
        // this.aiDirector = new AIDirector(this);
        this.worldBHV = new WorldBHV(this);
        this.physicsWorld = new PhysicsWorld(this);
    }

    public void buildPhysics() {
        physicsWorld.start();
    }


    public void tick() {
        if (physicsWorld.isBuilt()) physicsWorld.tick();
    }


}