package App;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    public void start(Stage primaryStage) {
        System.setProperty("prism.lcdtext", "false");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Dashboard.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            // primaryStage.setMaximized(true);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            // primaryStage.setMinWidth(942);
            // primaryStage.setMinHeight(670);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}