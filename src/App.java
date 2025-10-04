import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class App extends Application {

    private boolean readyToExit = false;

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();

        Scene scene = new Scene(root, 800,600);

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
