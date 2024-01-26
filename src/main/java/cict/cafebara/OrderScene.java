package cict.cafebara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class OrderScene extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OrderScene.class.getResource("Order.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("CafeBara - Ordering System");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("logo/applogo.png")));
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(1870);
        stage.setHeight(1030);
        stage.show();
        stage.setFullScreen(false);
    }


}
