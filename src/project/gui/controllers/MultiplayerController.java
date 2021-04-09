package project.gui.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.stage.Screen;
import javafx.util.converter.DateTimeStringConverter;
import project.gui.Main;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class MultiplayerController {
    public ToggleButton randomSideBtn;
    public ToggleButton whiteSideBtn;
    public ToggleButton blackSideBtn;
    public Label warningText;
    public Label warningText1;
    public TextField timeText;
    public static boolean blackSide = false;
    public static int minutes = 5;
    public static int seconds = 0;

    public void initialize() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        timeText.setTextFormatter(new TextFormatter<>(new DateTimeStringConverter(format), format.parse("05:00")));
    }

    @FXML
    private void changeSceneGameBoard() throws IOException {
        if (whiteSideBtn.isSelected() || randomSideBtn.isSelected() || blackSideBtn.isSelected()) {
            if (whiteSideBtn.isSelected())
                blackSide = false;
            if (blackSideBtn.isSelected())
                blackSide = true;
            if (randomSideBtn.isSelected())
                blackSide = new Random().nextBoolean();

            minutes = Integer.parseInt(timeText.getText(0, 2));
            seconds = Integer.parseInt(timeText.getText(3, 5));

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            Main.primaryStage.setX(bounds.getMinX());
            Main.primaryStage.setY(bounds.getMinY());
            Main.primaryStage.setWidth(bounds.getWidth());
            Main.primaryStage.setHeight(bounds.getHeight());
            Main.primaryStage.setMaximized(true);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/project/gui/views/GameBoard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            GameBoardController controller = loader.getController();

            Main.primaryStage.setScene(scene);
            Main.primaryStage.show();
        } else {
            warningText.setText("Vyber spomedzi všetkých nastavení.");
        }
    }



    public void blackSideChoose() {
        randomSideBtn.setSelected(false);
        whiteSideBtn.setSelected(false);
    }

    public void whiteSideClick() {
        randomSideBtn.setSelected(false);
        blackSideBtn.setSelected(false);
    }

    public void randomSideClick() {
        blackSideBtn.setSelected(false);
        whiteSideBtn.setSelected(false);
    }

    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
    }
}
