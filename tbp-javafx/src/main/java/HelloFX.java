import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(
                (ChangeListener) (observable, oldValue, newValue) -> {
                    if (newValue != Worker.State.SUCCEEDED) {
                        return;
                    }

                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("app", new ManageObject());
                }
        );
        webEngine.load(HelloFX.class.getResource("index.html").toExternalForm());
        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 960, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}