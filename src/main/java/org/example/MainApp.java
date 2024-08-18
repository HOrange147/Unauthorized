package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class MainApp extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/Mainview.fxml"));
        primaryStage.setTitle("Unauthorized综合利用工具");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args )
    {
        launch(args);
    }
}
