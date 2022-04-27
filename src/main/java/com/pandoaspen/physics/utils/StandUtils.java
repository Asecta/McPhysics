package com.pandoaspen.physics.utils;

import com.pandoaspen.physics.physics.CubeBody;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StandUtils {

    public static final float LARGE_HEAD_SIZE = 0.5f;
    public static final float LARGE_HEAD_SIZE_OFFSET = LARGE_HEAD_SIZE / -2f;
    public static final float LARGE_HEAD_STAND_OFFSET = 1.44f;

    public static final float SMALL_HEAD_SIZE = 0.33f;
    public static final float SMALL_HEAD_SIZE_OFFSET = SMALL_HEAD_SIZE / -2f;
    public static final float SMALL_HEAD_STAND_OFFSET = .72f;

    public static float[] LARGE_ARM_SIZE = new float[]{0, 0, 0};
    public static float[] LARGE_ARM_STAND_OFFSET = {0.5f, 0.5f, .5f};

    //    public static void positionStand(CubeBody body, Vector3f origin, Matrix4f m) {
    //        float eulerX = (float) Math.atan2(m.m12(), m.m22());
    //        float eulerY = (float) Math.atan2(-m.m02(), Math.sqrt(m.m12() * m.m12() + m.m22() * m.m22()));
    //        float eulerZ = (float) Math.atan2(m.m01(), m.m00());
    //
    //        float Sx = sin(eulerX);
    //        float Sy = sin(eulerY);
    //        float Sz = sin(eulerZ);
    //        float Cx = cos(eulerX);
    //        float Cy = cos(eulerY);
    //        float Cz = cos(eulerZ);
    //
    //        float m10 = Cy * Sz;
    //        float m11 = Cx * Cz + Sx * Sy * Sz;
    //        float m12 = -Cz * Sx + Cx * Sy * Sz;
    //
    //
    //        float rx = m10 * LARGE_ARM_SIZE[0];
    //        float ry = m11 * LARGE_ARM_SIZE[1];
    //        float rz = m12 * LARGE_ARM_SIZE[2];
    //
    //        origin.sub(LARGE_ARM_STAND_OFFSET[0], LARGE_ARM_STAND_OFFSET[1], LARGE_ARM_STAND_OFFSET[2]);
    //        origin.add(rx, ry, rz);
    //
    //        body.setArmAngles(eulerX, eulerY, eulerZ);
    //        // body.setArmAngles(eulerX, eulerY + 0.785398f, eulerZ - 0.261799f);
    //
    //        body.teleport(origin);
    //    }


    public static Vector3f getOffset(Vector3f origin, Matrix4f m) {
        Matrix3f rot = m.get3x3(new Matrix3f());
        Vector3f v = new Vector3f(rot.m00, rot.m11, rot.m22);
        v.add(origin);
        //        body.getInitialPosition().getWorld().spawnParticle(Particle.REDSTONE, v.x, v.y, v.z, 1, new
        //        Particle.DustOptions(Color.RED, 1f));

        float eulerX = (float) Math.atan2(m.m12(), m.m22());
        float eulerY = (float) Math.atan2(-m.m02(), Math.sqrt(m.m12() * m.m12() + m.m22() * m.m22()));
        float eulerZ = (float) Math.atan2(m.m01(), m.m00());

        float Sx = sin(eulerX);
        float Sy = sin(eulerY);
        float Sz = sin(eulerZ);
        float Cx = cos(eulerX);
        float Cy = cos(eulerY);
        float Cz = cos(eulerZ);

        float m10 = Cy * Sz;
        float m11 = Cx * Cz + Sx * Sy * Sz;
        float m12 = -Cz * Sx + Cx * Sy * Sz;

        float rx = m10 * LARGE_HEAD_SIZE_OFFSET;
        float ry = m11 * LARGE_HEAD_SIZE_OFFSET;
        float rz = m12 * LARGE_HEAD_SIZE_OFFSET;

        origin.sub(0, LARGE_HEAD_STAND_OFFSET, 0);
        origin.add(rx, ry, rz);

        return origin;
    }

    public static void positionStand(CubeBody body, Vector3f origin, Matrix4f m) {
        float eulerX = (float) Math.atan2(m.m12(), m.m22());
        float eulerY = (float) Math.atan2(-m.m02(), Math.sqrt(m.m12() * m.m12() + m.m22() * m.m22()));
        float eulerZ = (float) Math.atan2(m.m01(), m.m00());
        body.setHeadAngles(eulerX, eulerY, eulerZ);
        body.teleport(getOffset(origin, m));
    }

    public static float sin(float f) {
        return (float) Math.sin(f);
    }

    public static float cos(float f) {
        return (float) Math.cos(f);
    }

    //    static Vector3f transformVecByEulerAng(Vector3f euler, float x, float y, float z) {
    //        float Sx = sin(euler.x);
    //        float Sy = sin(euler.y);
    //        float Sz = sin(euler.z);
    //        float Cx = cos(euler.x);
    //        float Cy = cos(euler.y);
    //        float Cz = cos(euler.z);
    //
    //        float m00 = Cy * Cz;
    //        float m01 = Cz * Sx * Sy - Cx * Sz;
    //        float m02 = Cx * Cz * Sy + Sx * Sz;
    //        float m10 = Cy * Sz;
    //        float m11 = Cx * Cz + Sx * Sy * Sz;
    //        float m12 = -Cz * Sx + Cx * Sy * Sz;
    //        float m20 = -Sy;
    //        float m21 = Cy * Sx;
    //        float m22 = Cx * Cy;
    //
    //        float rx = m00 * x + m10 * y + m20 * z;
    //        float ry = m01 * x + m11 * y + m21 * z;
    //        float rz = m02 * x + m12 * y + m22 * z;
    //
    //        return new Vector3f(rx, ry, rz);
    //    }

}
