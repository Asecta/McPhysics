# McPhysics

A basic experimental physics engine implementation inside minecraft, using the bullet physics library. 

This allows for real time physics in minecraft with reasonable performance by uses pre-generated and optimized meshes representing the world. Working with a pre-generated mesh allows for much faster collision detection, at the cost of being static. This could be overcome with some clever partial mesh regeneration, but would only be applicable for specific usages (such as heavily driven gamemodes)

Example of it in use: 
(First a basic example, and then generating 220 cubes with a restitution (bounciness) greater than 100% )


https://user-images.githubusercontent.com/8020221/165645782-9db7e5c1-e529-4077-a884-9d6331a301ef.mp4


Wall falling over
https://user-images.githubusercontent.com/8020221/165646148-159e4092-7bf1-41ae-a676-aff90332b0a4.mp4


Dropping cubes onto a structure 
https://user-images.githubusercontent.com/8020221/165646302-5020f7b2-4010-4bd0-92ed-312c9a062c95.mp4

