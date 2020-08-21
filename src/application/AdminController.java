package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class AdminController {


    public AdminController() {
    }

    //返回登录页面
    public void back(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/index.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //跳转景点信息界面
    public void attraction(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/attraction.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //跳转路线信息界面
    public void route(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/route.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //跳转公告界面
    public void warning(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/notice.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
