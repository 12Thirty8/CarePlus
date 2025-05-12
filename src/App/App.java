package App;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    public void start(Stage primaryStage) {
        System.setProperty("prism.lcdtext", "false");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/LoginPage.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            // primaryStage.setMaximized(true);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            // primaryStage.setMinWidth(942);
            // primaryStage.setMinHeight(670);
            primaryStage.setTitle("Care ++");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/ICONS/1Care++.png")));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}