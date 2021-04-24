package project.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import project.model.databaseSystem.ChessPlayer;
import project.model.databaseSystem.LoginConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static project.gui.Main.primaryStage;


public class ProfileSceneController implements Initializable {

    @FXML
    private ImageView photoContainer;
    @FXML
    private TextField newPassword;
    @FXML
    private TextField oldPassword;
    @FXML
    private TextField name;
    @FXML
    private TextField password;
    @FXML
    private TextField email;
    @FXML
    private Button changeButton;
    @FXML
    private Label gamesWithPlayers;
    @FXML
    private Label gamesWithPc;
    @FXML
    private Label warning;

    private final Image defaultPhoto=new Image("/project/gui/resources/pictures/graphics/Profile-Avatar-PNG.png");
    private final ChessPlayer activePlayer = LoginConnection.getInstance().getActivePlayer();
    private FileChooser fileChooser;

    private void loadImage() {
        Path path = Paths.get(".","Data", "ProfilePic.png");
        if (Files.exists(path)) {
            photoContainer.setImage(new Image(path.toFile().toURI().toString()));
        } else {
            new File("Data").mkdir();
            photoContainer.setImage(defaultPhoto);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadImage();
        warning.setText("");
        name.setText(activePlayer.getName());
        password.setText(activePlayer.getPassword());
        email.setText(activePlayer.getEmail());
        gamesWithPlayers.setText(String.valueOf(activePlayer.getGamesVsPlayer()));
        gamesWithPc.setText(String.valueOf(activePlayer.getGamesVsPc()));
        name.setEditable(false);
        password.setEditable(false);
        email.setEditable(false);
        newPassword.setVisible(false);
        oldPassword.setVisible(false);
        changeButton.setVisible(false);
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.JPG", "*.jpeg"));
    }

    @FXML
    private void deletePhoto() {
        warning.setText("");
        if(photoContainer.getImage().equals(defaultPhoto))
        {
            warning.setText("Nemáte nastavenú žiadnú fotku!");
            return;
        }

        Path path = Paths.get(".","Data", "ProfilePic.png");
        if (Files.exists(path)) {
            path.toFile().delete();
        }

        photoContainer.setImage(defaultPhoto);
        setImageToCenter(photoContainer);
        //+pouzivatelovi nastav foto ze nema
    }

    @FXML
    private void choosePhoto() {
        warning.setText("");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            Image photo = new Image(file.toURI().toString());
            photoContainer.setImage(photo);
            activePlayer.setPhoto(photo);
            setImageToCenter(photoContainer);
            try {
                Files.copy(file.toPath(), Paths.get(".", "Data", "ProfilePic.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setImageToCenter(ImageView photo) {
        Image image = photo.getImage();
        if (image != null) {
            double ratioX = photo.getFitWidth() / image.getWidth();
            double ratioY = photo.getFitHeight() / image.getHeight();
            double koeficient = Math.min(ratioX, ratioY);
            double w = image.getWidth() * koeficient;
            double h = image.getHeight() * koeficient;
            photo.setX((photo.getFitWidth() - w) / 2);
            photo.setY((photo.getFitHeight() - h) / 2);

        }
    }
    @FXML
    private void changePassword(){
        warning.setText("");
        newPassword.setVisible(true);
        oldPassword.setVisible(true);
        changeButton.setVisible(true);
    }
    @FXML
    private void saveChangedPassword(){
        //niekde je prazdne
        if(newPassword.getText().equals("")||oldPassword.getText().equals("")){
            warning.setText("Vyplň všetky polia!");
            return;
        }
        //prilis kratke
        if(newPassword.getText().length()<6){
            warning.setText("Príliš krátke!");
            return;
        }
        //stare heslo sa nezhoduje s naklikanym starym
        if(!oldPassword.getText().equals(activePlayer.getPassword())){
            warning.setText("Toto nie je vaše aktuálne heslo");
            return;
        }
        if(newPassword.getText().equals(activePlayer.getPassword())){
            warning.setText("Toto heslo práve používate!");
            return;
        }


        LoginConnection.getInstance().changePassword(newPassword.getText());
        activePlayer.setPassword(newPassword.getText());

        newPassword.setVisible(false);
        oldPassword.setVisible(false);
        changeButton.setVisible(false);
        password.setText(activePlayer.getPassword());

    }


    @FXML
    private void changeSceneMenu() throws IOException {
        LoginSceneController.switchScene("/project/gui/views/MenuScene.fxml");

    }


}