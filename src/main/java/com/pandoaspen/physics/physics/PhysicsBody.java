package com.pandoaspen.physics.physics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface PhysicsBody {

    Vector3f getPosition();

    float getSize();

    float getMass();

    float getRestitution();

    void transform(Vector3f origin, Matrix4f transform);

    void init();

    void remove();

    void setHeadAngles(float x, float y, float z);

    boolean teleport(Vector3f vector3f);

}
