package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EnterController {

    @FXML
    private TextField uN;
    @FXML
    private PasswordField ps;
    @FXML
    private Label w;



    private static final String USERNAME = "1";
    private static final String PASSWORD = "1";

    public EnterController(){}

    ///进入游客页面
    public void tourist(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tourists.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ///进入登录页面
    public void login(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/login.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //进入管理员页面
    public void admin() {
        try {
            if (uN.getText().equals(USERNAME) && ps.getText().equals(PASSWORD)) {
                Stage stage = Main.getStage();
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/admin.fxml"))));
                stage.show();
                w.setVisible(false);
            } else {
                w.setVisible(true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
