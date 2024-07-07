import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;

public class PortfolioLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the custom font
        Font.loadFont(getClass().getResourceAsStream("/fonts/VolterGoldfish.ttf"), 10);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Portfolio.fxml"));
        Parent root = loader.load();

        // Ensure the root is an instance of AnchorPane
        if (root instanceof AnchorPane) {
            // Set background image
            Image backgroundImage = new Image(getClass().getResource("/images/background.jpg").toExternalForm());
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            ((AnchorPane) root).setBackground(new Background(background));
        } else {
            System.err.println("Root is not an instance of AnchorPane");
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

        primaryStage.setTitle("Portfolio!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.getIcons().add(new Image("/icon.png"));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
