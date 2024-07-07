import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionFormCreator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PortfolioLauncher extends ExtensionFormCreator {

    @Override
    public ExtensionForm createForm(Stage primaryStage) throws Exception {
        // Load the custom font
        Font.loadFont(getClass().getResourceAsStream("/fonts/VolterGoldfish.ttf"), 10);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Portfolio.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

        primaryStage.setTitle("Portfolio!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.getIcons().add(new Image("/icon.png"));

        return loader.getController();
    }

    public static void main(String[] args) {
        runExtensionForm(args, PortfolioLauncher.class);
    }
}
