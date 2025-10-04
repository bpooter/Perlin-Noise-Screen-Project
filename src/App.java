import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class App extends Application {

    private boolean readyToExit = false;

    @Override
    public void start(Stage stage) throws Exception {

        Box cube = new Box(200,200,200);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.DEEPSKYBLUE);
        material.setSpecularColor(Color.LIGHTBLUE);
        cube.setMaterial(material);

        Group root = new Group(cube);

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

        RotateTransition rotateX = new RotateTransition(Duration.seconds(10), cube);
        rotateX.setAxis(Rotate.X_AXIS);
        rotateX.setByAngle(360);
        rotateX.setCycleCount(Animation.INDEFINITE);
        rotateX.play();

    }

    public static void main(String[] args) {
        launch();
    }
}
