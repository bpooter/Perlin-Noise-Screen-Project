import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private boolean readyToExit = false;
    private static final int SIZE = 35;
    private static final float SCALE = 40;

    @Override
    public void start(Stage stage) throws Exception {

        double offsetX = (SIZE * SCALE) / 2.0;
        double offsetZ = (SIZE * SCALE) / 2.0;

        TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);

        /* todo make the noise more deliberate, fix the noise to generate mountains valleys and plains rather than uniform */
        FastNoiseLite noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noise.SetFrequency(0.005f);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(5);
        noise.SetFractalLacunarity(2.0f);
        noise.SetFractalGain(0.5f);

        float noiseScale = 0.5f;
        float heightScale = 250f;

        float scrollSpeed = 0.025f;
        final float[] scrollOffset = {0f};

        int width = 300;
        int depth = 300;

        int roadWidth = 3;
        int roadStart = (SIZE / 2) - (roadWidth / 2);
        int roadEnd = roadStart + roadWidth;

        TerrainGenerator terrain = new TerrainGenerator();
        double[][] heights = new double[width][depth];
        //terrain.updateMeshHeights(heights, width, depth);
        float spacing = 100f;

        // add vertices to triangle mesh
        for (int z = 0; z < depth - 1; z++){
            for (int x = 0; x < width - 1; x++){
                float worldX = (x - width / 2f) * spacing;
                float worldY = (float) heights[x][z]; // height from terrain generator
                float worldZ = (z - depth / 2f) * spacing;
                triangleMesh.getPoints().addAll(worldX, worldY, worldZ);
            }
        }

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                terrain.updateScroll();
                terrain.updateMeshHeights(heights, width, depth);

                // add vertices to triangle mesh with updated values
                for (int z = 0; z < depth - 1; z++){
                    for (int x = 0; x < width - 1; x++){
                        float worldX = (x - width / 2f) * spacing;
                        float worldY = (float) heights[x][z]; // height from terrain generator
                        float worldZ = (z - depth / 2f) * spacing;
                        triangleMesh.getPoints().addAll(worldX, worldY, worldZ);
                    }
                }
            }
        };

        /*AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                //scrollOffset[0] += scrollSpeed;

                triangleMesh.getPoints().clear();

                //adding points
                for (int y = 0; y <= SIZE; y++) {
                    for (int x = 0; x <= SIZE; x++) {
                        float xPoint = (float)(x * SCALE - offsetX);
                        float zPoint = (float)(y * SCALE - offsetZ);

                        float noiseValue = noise.GetNoise(x * noiseScale, (y + scrollOffset[0]) * noiseScale);

                        float yPoint;
                        if (x>= roadStart && x < roadEnd){
                            yPoint = 15f;
                        } else {
                            yPoint = Math.signum(noiseValue) * (float)Math.pow(Math.abs(noiseValue), 1.5) * heightScale;
                        }

                        triangleMesh.getPoints().addAll(xPoint, yPoint, zPoint);
                    }
                }
            }
        };*/
        timer.start();


        triangleMesh.getTexCoords().addAll(0,0);

        for (int y=0; y<SIZE; y++){
            for (int x=0; x<SIZE; x++){
                int p0 = y * (SIZE + 1) + x;
                int p1 = p0 + 1;
                int p2 = p0 + (SIZE + 1);
                int p3 = p2 + 1;

                triangleMesh.getFaces().addAll(p0, 0, p2, 0, p1, 0);
                triangleMesh.getFaces().addAll(p1, 0, p2, 0, p3, 0);
            }
        }

        PhongMaterial filledMaterial = new PhongMaterial();
        PhongMaterial lineMaterial = new PhongMaterial();

        filledMaterial.setDiffuseColor(Color.BLACK);
        filledMaterial.setSpecularColor(Color.TRANSPARENT);

        lineMaterial.setDiffuseColor(Color.WHITE);

        MeshView meshView = new MeshView(triangleMesh);
        MeshView filledMesh = new MeshView(triangleMesh);
        filledMesh.setDrawMode(DrawMode.FILL);
        filledMesh.setMaterial(filledMaterial);
        meshView.setMaterial(lineMaterial);


        meshView.setDrawMode(DrawMode.LINE);

        meshView.setCullFace(CullFace.NONE);
        filledMesh.setCullFace(CullFace.NONE);

        Group root = new Group(filledMesh,meshView);

        // Add some lights!
        AmbientLight ambient = new AmbientLight(Color.rgb(100, 100, 100));
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(0);
        light.setTranslateY(0);
        light.setTranslateZ(0);

        root.getChildren().addAll(ambient, light);

        Box originBox = new Box(100,100,100);
        originBox.setMaterial(new PhongMaterial(Color.RED));
        originBox.setTranslateZ(-400);
        root.getChildren().add(originBox);


        Scene scene = new Scene(root, 800,600, true);

        PerspectiveCamera camera = new PerspectiveCamera(true);

        double meshWidth = SIZE * SCALE;
        double aspect = scene.getWidth() / scene.getHeight();
        double verticalFOV = Math.toRadians(camera.getFieldOfView());
        double horizontalFOV = 2 * Math.atan(Math.tan(verticalFOV / 2) * aspect);
        double requiredDistance = (meshWidth / 2.0) / Math.tan(horizontalFOV / 1.65);

        // Set camera at a distance so it sees the whole mesh horizontally
        camera.setTranslateZ(offsetZ - requiredDistance);
        camera.setRotationAxis(Rotate.X_AXIS);
        //camera.setRotate(-10);
        camera.setRotationAxis(Rotate.Y_AXIS);
        camera.setRotate(-30);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        scene.setCamera(camera);

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> readyToExit = true);
        delay.play();

        scene.setOnKeyPressed(e -> {
            if (readyToExit) System.exit(0);

        });


        scene.setOnMouseMoved(e -> {
            if (readyToExit) System.exit(0);
        });

        scene.setOnMouseClicked(e -> {
            if (readyToExit) System.exit(0);
        });

        stage.setScene(scene);
        stage.setFullScreen(true);

        stage.show();

    }



    public static void main(String[] args) {
        launch();
    }
}
