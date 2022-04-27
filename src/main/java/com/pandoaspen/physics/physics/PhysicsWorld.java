package com.pandoaspen.physics.physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.pandoaspen.physics.game.GameManager;
import com.pandoaspen.physics.utils.MeshIO;
import com.pandoaspen.physics.utils.WavefrontObj;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.PI;
import static java.lang.Math.floor;

@Data
public class PhysicsWorld {
    private GameManager gameManager;
    private boolean built = false;

    private DynamicsWorld dynamicsWorld;
    private Map<RigidBody, PhysicsBody> physicsBodies;

    public PhysicsWorld(GameManager gameManager) {
        this.gameManager = gameManager;
        this.physicsBodies = new HashMap<>();
    }

    public RigidBody addPhysicsObject(PhysicsBody object) {
        object.init();

        DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(1, 0, 0, 0),
                new Vector3f(object.getPosition().x, object.getPosition().y, object.getPosition().z), 1.0f)));
        CollisionShape fallShape = new BoxShape(new Vector3f(object.getSize(), object.getSize(), object.getSize()));

        Vector3f fallInertia = new Vector3f(0, 0, 0);
        fallShape.calculateLocalInertia(object.getMass(), fallInertia);

        RigidBodyConstructionInfo fallRigidBodyCI =
                new RigidBodyConstructionInfo(object.getMass(), motionState, fallShape, fallInertia);
        RigidBody fallRigidBody = new RigidBody(fallRigidBodyCI);

        fallRigidBody.setRestitution(object.getRestitution());

        fallRigidBody.setFriction(.65f);

        dynamicsWorld.addRigidBody(fallRigidBody);
        physicsBodies.put(fallRigidBody, object);

        return fallRigidBody;
    }


    public void tick() {
        dynamicsWorld.stepSimulation(1 / 20f, 10);

        for (Map.Entry<RigidBody, PhysicsBody> entry : physicsBodies.entrySet()) {
            Transform transform = new Transform();
            entry.getKey().getWorldTransform(transform);
            entry.getValue()
                    .transform(asJomlVector(transform.origin), asJomlMatrix(transform.getMatrix(new Matrix4f())));
        }
    }

    public void drawCube(Transform transform, Vector3f halfExtents) {
        for (int i = 0; i < 8; i++) {
            float dx = ((i & 1) * 2 - 1) * (halfExtents.x);
            float dy = (((i >> 1) & 1) * 2 - 1) * (halfExtents.y);
            float dz = (((i >> 2) & 1) * 2 - 1) * (halfExtents.z);

            Vector3f vector3f = new Vector3f(dx, dy, dz);
            transform.transform(vector3f);

            particle(vector3f.x, vector3f.y, vector3f.z);
        }
    }

    public void particle(float x, float y, float z) {
        Bukkit.getWorlds().get(0)
                .spawnParticle(Particle.REDSTONE, x, y, z, 1, new Particle.DustOptions(Color.RED, .5f));
    }

    public void start() {
        dynamicsWorld = buildDynamicsWorld();
        dynamicsWorld.setGravity(new Vector3f(0, -10, 0));

        File MESH_FILE = new File(Bukkit.getPluginManager().getPlugin("McPhysics").getDataFolder(), "test.obj");

        WavefrontObj obj = MeshIO.readObj(MESH_FILE).triangulate();
        ConcaveShape groundShape = readTrimeshShape(obj);

        CollisionObject groundObject = new CollisionObject();
        groundObject.setCollisionShape(groundShape);
        groundObject.setWorldTransform(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, 0), 1f)));
        groundObject.setRestitution(1f);

        groundObject.setFriction(.64f);

        dynamicsWorld.addCollisionObject(groundObject);

        built = true;
    }

    final float phi = ((float) Math.sqrt(5) + 1) / 2f - 1f;
    final float TWO_PI = (float) (PI * 2);

    float[][] generateFibSphere(Transform transform, float radius, int n) {

        float[][] points = new float[n][3];

        for (int i = 0; i < points.length; i++) {
            float longitude = phi * TWO_PI * i;
            longitude /= (2 * PI);
            longitude -= floor(longitude);
            longitude *= TWO_PI;

            if (longitude > PI) {
                longitude -= (2 * PI);
            }

            final float latitude = (float) Math.asin(-1 + 2 * i / (float) n);
            final float cosOfLatitude = (float) Math.cos(latitude);
            float x = (float) (radius * cosOfLatitude * Math.cos(longitude));
            float y = radius * cosOfLatitude * (float) Math.sin(longitude);
            float z = radius * (float) Math.sin(latitude);

            Vector3f vector3f = new Vector3f(x, y, z);
            transform.transform(vector3f);

            points[i] = new float[]{vector3f.x, vector3f.y, vector3f.z};
        }

        return points;
    }

    private BvhTriangleMeshShape readTrimeshShape(WavefrontObj obj) {
        int[] indices = obj.getFlatFaces();
        float[] coords = obj.getFlatVertices();

        IndexedMesh indexedMesh = new IndexedMesh();
        indexedMesh.numTriangles = indices.length / 3;
        indexedMesh.triangleIndexBase =
                ByteBuffer.allocateDirect(indices.length * Float.BYTES).order(ByteOrder.nativeOrder());
        indexedMesh.triangleIndexBase.asIntBuffer().put(indices);
        indexedMesh.triangleIndexStride = 3 * Float.BYTES;
        indexedMesh.numVertices = coords.length / 3;
        indexedMesh.vertexBase = ByteBuffer.allocateDirect(coords.length * Float.BYTES).order(ByteOrder.nativeOrder());
        indexedMesh.vertexBase.asFloatBuffer().put(coords);
        indexedMesh.vertexStride = 3 * Float.BYTES;

        TriangleIndexVertexArray meshInterface = new TriangleIndexVertexArray();
        meshInterface.addIndexedMesh(indexedMesh);

        BvhTriangleMeshShape triangleMeshShape = new BvhTriangleMeshShape(meshInterface, false);
        return triangleMeshShape;
    }

    private DynamicsWorld buildDynamicsWorld() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        DiscreteDynamicsWorld dynamicsWorld =
                new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);


        return dynamicsWorld;
    }

    public void removeBody(RigidBody rigidBody) {
        physicsBodies.get(rigidBody).remove();
        dynamicsWorld.removeRigidBody(rigidBody);
    }

    public void clearBodies() {
        for (RigidBody rigidBody : physicsBodies.keySet()) {
            removeBody(rigidBody);
        }
        physicsBodies.clear();
    }

    public static org.joml.Matrix4f asJomlMatrix(Matrix4f m) {
        return new org.joml.Matrix4f(m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12, m.m13, m.m20, m.m21, m.m22, m.m23,
                m.m30, m.m31, m.m32, m.m33);
    }

    public static org.joml.Vector3f asJomlVector(Vector3f v) {
        return new org.joml.Vector3f(v.x, v.y, v.z);
    }

}