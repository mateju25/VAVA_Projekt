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
import project.model.databaseSystem.MultiplayerConnection;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalTime;
import java.util.Random;

public class MultiplayerController {
    public TextField timeText;
    public Label warningText;
    public ToggleButton blackSideBtn;
    public ToggleButton whiteSideBtn;
    public ToggleButton randomSideBtn;
    public TextField generateLinkText;
    public TextField joinLinkText;
    public Label warningJoinText;

    public static boolean blackSide = false;
    public static int minutes = 5;
    public static int seconds = 0;
    public static boolean use = false;
    public static boolean host = false;


    public void initialize() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        timeText.setTextFormatter(new TextFormatter<>(new DateTimeStringConverter(format), format.parse("05:00")));

    }

    @FXML
    private void changeSceneGameBoard() throws IOException {

        if (whiteSideBtn.isSelected())
            blackSide = false;
        if (blackSideBtn.isSelected())
            blackSide = true;
        if (randomSideBtn.isSelected())
            blackSide = new Random().nextBoolean();

        use = true;
        host = true;

        minutes = Integer.parseInt(timeText.getText(0, 2));
        seconds = Integer.parseInt(timeText.getText(3, 5));

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Main.primaryStage.setX(bounds.getMinX());
        Main.primaryStage.setY(bounds.getMinY());
        Main.primaryStage.setWidth(bounds.getWidth());
        Main.primaryStage.setHeight(bounds.getHeight());
        Main.primaryStage.setMaximized(true);
        LoginSceneController.switchScene("/project/gui/views/GameBoard.fxml");
    }

    public void blackSideChoose(ActionEvent actionEvent) {

        randomSideBtn.setSelected(false);
        whiteSideBtn.setSelected(false);
    }


    public void whiteSideClick(ActionEvent actionEvent) {

        randomSideBtn.setSelected(false);
        blackSideBtn.setSelected(false);
    }

    public void randomSideClick(ActionEvent actionEvent) {
        blackSideBtn.setSelected(false);
        whiteSideBtn.setSelected(false);
    }

    public void generateLink(ActionEvent actionEvent) {
        minutes = Integer.parseInt(timeText.getText(0, 2));
        seconds = Integer.parseInt(timeText.getText(3, 5));
        host = false;
        if (whiteSideBtn.isSelected()) {
            MultiplayerConnection.getInstance().createNewGame(false, LocalTime.of(0, minutes, seconds));
            generateLinkText.setText(String.valueOf(MultiplayerConnection.getInstance().getId()));
        }
        if (blackSideBtn.isSelected()){
            MultiplayerConnection.getInstance().createNewGame(true, LocalTime.of(0, minutes, seconds));
            generateLinkText.setText(String.valueOf(MultiplayerConnection.getInstance().getId()));
        }
        if (randomSideBtn.isSelected()) {
            MultiplayerConnection.getInstance().createNewGame(new Random().nextBoolean(), LocalTime.of(0, minutes, seconds));
            generateLinkText.setText(String.valueOf(MultiplayerConnection.getInstance().getId()));
        }
    }

    public void joinLink(ActionEvent actionEvent) throws IOException {
        use = true;

        MultiplayerConnection.getInstance().setId(Integer.parseInt(joinLinkText.getText()));
        blackSide = !MultiplayerConnection.getInstance().getColor();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Main.primaryStage.setX(bounds.getMinX());
        Main.primaryStage.setY(bounds.getMinY());
        Main.primaryStage.setWidth(bounds.getWidth());
        Main.primaryStage.setHeight(bounds.getHeight());
        Main.primaryStage.setMaximized(true);
        LoginSceneController.switchScene("/project/gui/views/GameBoard.fxml");
    }

    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
    }
}
