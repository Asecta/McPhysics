package com.pandoaspen.physics.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class MeshIO {


    //    public static WavefrontObj readObj(File file) throws Exception {
    //        List<String> lines = Files.readAllLines(file.toPath());
    //
    //        List<String> vertLines = lines.stream().filter(line -> line.startsWith("v")).map(line -> line.substring
    //        (2)).collect(Collectors.toList());
    //        List<String> faceLines = lines.stream().filter(line -> line.startsWith("f")).map(line -> line.substring
    //        (2)).collect(Collectors.toList());
    //        Double[][] verts = vertLines.stream().map(line -> Arrays.stream(line.split(" ")).map
    //        (Double::parseDouble).toArray(Double[]::new)).toArray(Double[][]::new);
    //        Integer[][] faces = faceLines.stream().map(line -> Arrays.stream(line.split(" ")).map
    //        (Integer::parseInt).toArray(Integer[]::new)).toArray(Integer[][]::new);
    //        return new WavefrontObj(verts, faces, faces[0].length == 4);
    //    }
    public static WavefrontObj readObj(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            List<String> vertLines = lines.stream().filter(line -> line.startsWith("v")).map(line -> line.substring(2))
                    .collect(Collectors.toList());
            List<String> faceLines = lines.stream().filter(line -> line.startsWith("f")).map(line -> line.substring(2))
                    .collect(Collectors.toList());

            float[][] vertices = new float[vertLines.size()][3];

            for (int vertIndex = 0; vertIndex < vertices.length; vertIndex++) {
                String[] split = vertLines.get(vertIndex).split(" ");
                float[] vertex = new float[]{parseFloat(split[0]), parseFloat(split[1]), parseFloat(split[2])};
                vertices[vertIndex] = vertex;
            }

            int faceVertCount = faceLines.get(0).split(" ").length;
            int[][] faces = new int[faceLines.size()][faceVertCount];

            for (int faceIndex = 0; faceIndex < faces.length; faceIndex++) {
                String[] split = faceLines.get(faceIndex).split(" ");

                int[] face = new int[faceVertCount];
                for (int faceVertIndex = 0; faceVertIndex < faceVertCount; faceVertIndex++) {
                    face[faceVertIndex] = parseInt(split[faceVertIndex]) - 1;
                }

                faces[faceIndex] = face;
            }

            return new WavefrontObj(vertices, faces, faceVertCount == 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
