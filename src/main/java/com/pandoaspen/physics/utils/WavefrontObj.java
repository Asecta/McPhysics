package com.pandoaspen.physics.utils;

import lombok.Data;

@Data
public class WavefrontObj {
    private final float[][] vertices;
    private final int[][] faces;
    private final boolean isQuadMesh;

    public WavefrontObj triangulate() {
        if (!isQuadMesh) return this;

        int[][] triFaces = new int[faces.length * 2][3];

        for (int i = 0; i < faces.length * 2; i += 2) {
            int[] face = faces[i / 2];
            triFaces[i] = new int[]{face[0], face[3], face[2]};
            triFaces[i + 1] = new int[]{face[2], face[1], face[0]};
        }

        return new WavefrontObj(vertices, triFaces, false);
    }

    public int countFaces() {
        return faces.length;
    }

    public int countVertices() {
        return vertices.length;
    }

    public float[] getFlatVertices() {
        float[] result = new float[vertices.length * 3];

        int i = 0;
        for (float[] arr : vertices) {
            for (float f : arr) {
                result[i++] = f;
            }
        }

        return result;
    }

    public int[] getFlatFaces() {
        int[] result = new int[faces.length * 3];

        int i = 0;
        for (int[] arr : faces) {
            for (int f : arr) {
                result[i++] = f;
            }
        }

        return result;
    }

}