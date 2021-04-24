package project.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    public static Stage primaryStage;
    static {Locale.setDefault(new Locale("sk"));}
    public static ResourceBundle bundle = ResourceBundle.getBundle("project/gui/resources/bundles/slovak");

    @Override
    public void start(Stage primaryStage) throws Exception{

        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Void Chess");
        primaryStage.getIcons().add(new Image("/project/gui/resources/pictures/graphics/ikonka.png"));
        Parent root = FXMLLoader.load(getClass().getResource("/project/gui/views/LoginScene.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
