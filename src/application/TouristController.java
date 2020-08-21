package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TouristController {

    public TouristController(){
    }

    //返回初始页面
    public void back() {
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/index.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //景点信息查询页面
    public void tAttraction(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tAttraction.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //线路查询页面
    public void tRoute(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tRoute.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //景点分布图页面
    public void tMap(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tMap.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //停车服务页面
    public void park(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tPark.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
