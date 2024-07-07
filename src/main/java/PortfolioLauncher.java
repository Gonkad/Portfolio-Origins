import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionFormCreator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;

public class PortfolioLauncher extends ExtensionFormCreator {

    @Override
    public ExtensionForm createForm(Stage primaryStage) throws Exception {
        // Load the custom font
        Font.loadFont(getClass().getResourceAsStream("/fonts/VolterGoldfish.ttf"), 10);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Portfolio.fxml"));
        Parent root = loader.load();

        if (root instanceof AnchorPane) {
            Image backgroundImage = new Image("/images/background.png");
            BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            ((AnchorPane) root).setBackground(new Background(background));
        } else {
            System.err.println("Root is not an instance of AnchorPane");
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

        primaryStage.setTitle("Portfolio!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.getIcons().add(new Image("/icon.png"));

        return loader.getController();

    }

    public static void main(String[] args) {
        runExtensionForm(args, PortfolioLauncher.class);
    }
}
