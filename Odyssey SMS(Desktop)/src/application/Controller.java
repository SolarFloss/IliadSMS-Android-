package application;

import application.networking.Recieving;
import application.networking.Sending;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {

    public Stage stage;


    @FXML
    public Button btnSend;

    @FXML
    ScrollBar scrollBar;

    @FXML
    TextField fldMessage;

    public void initialize(){
        stage = Main.getStage();

        if(stage != null) {
            stage.setOnCloseRequest(event -> {
                System.out.println("Closing");
                Recieving.close();
                Sending.close();
            });
        }



    }

    public void btnSendClicked(ActionEvent event) {
        if(!fldMessage.getText().equals("")){
            Sending.send(fldMessage.getText());
            fldMessage.setText("");
        }
    }
}
