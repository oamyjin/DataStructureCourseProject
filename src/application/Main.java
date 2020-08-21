
package application;

        import javafx.fxml.FXMLLoader;
        import javafx.scene.Scene;
        import javafx.scene.layout.AnchorPane;
        import javafx.stage.Stage;
        import javafx.application.Application;


/**
 *
 * Creates By @ Amy Liu
 *
 * JavaFX的每一个页面对应application包中相应Controller类
 * 算法的编写在对应触发的函数中
 * 算法包括Dijkstra，floyd，DFS，Hamilton，快速排序
 * MyList, MyListItem, MyIerator, MyStatck, MyQeueu以及其他实体类均为自定义数据结构
 *
 **/
public class Main extends Application{

    static Stage stage;
    static Stage primaryStage;
    static Graph graph;

    public static Graph getGraph(){
        return graph;
    }


    public static Stage getStage() {
        return stage;
    }

    public void start(Stage primaryStage) {
        try{
            graph  = new Graph();

            //从数据库调取信息构成图
            Database database = new Database(graph);
            graph = database.dataToGraph();

            this.primaryStage=primaryStage;
            this.primaryStage.setTitle("景点信息");
            AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("../ui/index.fxml"));
            Scene scene = new Scene(root);
            stage = primaryStage;
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
