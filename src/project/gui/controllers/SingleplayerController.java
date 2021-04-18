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
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.util.converter.DateTimeStringConverter;
import project.gui.Main;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class SingleplayerController {
    public ToggleButton randomSideBtn;
    public ToggleButton whiteSideBtn;
    public ToggleButton blackSideBtn;
    public ToggleButton impossibleLevelBtn;
    public ToggleButton hardLevelBtn;
    public ToggleButton mediumLevelBtn;
    public ToggleButton easyLevelBtn;
    public Label warningText;
    public TextField timeText;

    public static boolean blackSide = false;
    public static int level = 1;
    public static int minutes = 5;
    public static int seconds = 0;
    public static boolean use = false;

    public void initialize() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        timeText.setTextFormatter(new TextFormatter<>(new DateTimeStringConverter(format), format.parse("05:00")));
    }

    @FXML
    private void changeSceneGameBoard() throws IOException {
        if ((whiteSideBtn.isSelected() || randomSideBtn.isSelected() || blackSideBtn.isSelected()) &&
                (easyLevelBtn.isSelected() || mediumLevelBtn.isSelected() || hardLevelBtn.isSelected() || impossibleLevelBtn.isSelected()) ) {
            if (whiteSideBtn.isSelected())
                blackSide = false;
            if (blackSideBtn.isSelected())
                blackSide = true;
            if (randomSideBtn.isSelected())
                blackSide = new Random().nextBoolean();

            if (easyLevelBtn.isSelected())
                level = 1;
            if (mediumLevelBtn.isSelected())
                level = 2;
            if (hardLevelBtn.isSelected())
                level = 3;
            if (impossibleLevelBtn.isSelected())
                level = 10;

            use = true;

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

    @FXML
    private void changeSceneMenu() throws IOException {
        use = false;
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");
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

    public void impossibleLevelBtnClick(ActionEvent actionEvent) {
        easyLevelBtn.setSelected(false);
        hardLevelBtn.setSelected(false);
        mediumLevelBtn.setSelected(false);
    }

    public void hardLevelBtnClick(ActionEvent actionEvent) {
        easyLevelBtn.setSelected(false);
        impossibleLevelBtn.setSelected(false);
        mediumLevelBtn.setSelected(false);
    }

    public void mediumLevelBtnClick(ActionEvent actionEvent) {
        easyLevelBtn.setSelected(false);
        hardLevelBtn.setSelected(false);
        impossibleLevelBtn.setSelected(false);
    }

    public void easyLevelBtnClick(ActionEvent actionEvent) {
        impossibleLevelBtn.setSelected(false);
        hardLevelBtn.setSelected(false);
        mediumLevelBtn.setSelected(false);
    }
}
