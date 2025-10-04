import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class App extends Application {

    private boolean readyToExit = false;

    @Override
    public void start(Stage stage) throws Exception {

        Box cube = new Box(100,100,100);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.DARKGRAY);
        material.setSpecularColor(Color.WHEAT);
        cube.setMaterial(material);

        TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
        float[] points = {
                0.0f,0.0f,0.0f,
                100.0f,0.0f,0.0f,
                0.0f,100.0f,0.0f
        };

        triangleMesh.getPoints().addAll(points);

        float[] texCoords = { 0.0f,0.0f };

        triangleMesh.getTexCoords().addAll(texCoords);

        int[] faces = {
                0,0,1,0,2,0
        };

        triangleMesh.getFaces().addAll(faces);

        MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);

        meshView.setCullFace(CullFace.NONE);
        meshView.setTranslateX(-50);  // Center it (optional)
        meshView.setTranslateY(-50);
        meshView.setTranslateZ(0);    // Keep it at origin, in front of camera

        Group root = new Group(meshView);

        // Add some lights!
        AmbientLight ambient = new AmbientLight(Color.rgb(100, 100, 100));
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(150);
        light.setTranslateY(-100);
        light.setTranslateZ(-300);

        root.getChildren().addAll(ambient, light);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-600);
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);

        Scene scene = new Scene(root, 800,600, true);
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

        //setting the rotation to rotate along the y axis.
        RotateTransition rotateY = new RotateTransition(Duration.seconds(10), cube);
        rotateY.setAxis(Rotate.Y_AXIS);
        rotateY.setByAngle(360);
        rotateY.setCycleCount(Animation.INDEFINITE);
        rotateY.play();

    }

    public static void main(String[] args) {
        launch();
    }
}
