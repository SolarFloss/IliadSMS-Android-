package application;

import application.networking.Recieving;
import application.networking.Sending;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage stage;
    private static Recieving recieving;
    private static Sending sending;

    private static Thread receiveThread;

    public static Stage getStage(){
        return stage;
    }

    public static Thread getReceiveThread(){
        return receiveThread;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("../fxml/main.fxml"));
        Scene scene = new Scene(root,600,400);
        scene.getStylesheets().add("css/style.css");





        stage = primaryStage;
        stage.setTitle("Hello World");
        stage.setScene(scene);
        stage.show();

        Controller controller = new Controller();
        controller.initialize();

        //Create and start thread
        Recieving.setRunning(true);
        recieving = new Recieving();
        receiveThread = new Thread(recieving);
        receiveThread.start();




    }


    public static void main(String[] args) {
        launch(args);
    }
}
