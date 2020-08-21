package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class MapController {

    @FXML
    private Label aNC;
    @FXML
    private Label aNR;
    @FXML
    private Text aM;


    Graph g = Main.getGraph();

    //返回游客页面
    public void back(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tourists.fxml"))));
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //页面初始化
    @FXML
    private void initialize() {
        System.out.println("邻接矩阵");
        createMutex();
    }
    //邻接矩阵
    public void createMutex() {
        //给每个node赋id值
        MyIterator<Node> itN = new MyIterator<>(g.nodes);
        System.out.println(g.nodes.getLen());
        int len = 0;
        while(itN.hasNext()){
            System.out.println(len);
            itN.next().getItem().setId(len);
            len++;
        }
        //创建邻接矩阵
        double[][] mutex = new double[len][len];
        itN = new MyIterator<>(g.nodes);
        int i = 0;
        while(itN.hasNext()){
            //遍历adjacency，把有连接的找到
            MyListItem<Node> itemN = itN.next();
            System.out.print(itemN.getItem().getName() + "   ");
            MyIterator<Edge> itA = new MyIterator<>(itemN.getItem().getAdjacency());
            while(itA.hasNext()){
                MyListItem<Edge> itemA = itA.next();
                if(itemA.getItem().getFrom().getName().equals(itemN.getItem().getName())){
                    mutex[i][itemA.getItem().getTo().getId()] = itemA.getItem().getDistance();
                }
                if(itemA.getItem().getTo().getName().equals(itemN.getItem().getName())){
                    mutex[i][itemA.getItem().getFrom().getId()] = itemA.getItem().getDistance();
                }
            }
            //填补剩下的值
            for(int j = 0; j < len; j++){
                if(j != i && mutex[i][j] == 0){
                    mutex[i][j] = 32767;
                }
            }
            i++;
        }
        System.out.println();
        //打印邻接矩阵
        for(int j = 0; j < len; j++){
            for(int k = 0; k < len; k++){
                System.out.print(String.format("%-10s", mutex[j][k]));
            }
            System.out.println();
        }
        //在页面打印
        String r = "";
        itN = new MyIterator<>(g.nodes);
        while(itN.hasNext()) {
            r = r + itN.next().getItem().getName() + "     ";
        }
        aNR.setText(r);
        r = "";
        itN = new MyIterator<>(g.nodes);
        while(itN.hasNext()) {
            r = r + itN.next().getItem().getName() + "\n\n";
        }
        aNC.setText(r);
        r = "";
        for(int j = 0; j < len; j++){
            for(int k = 0; k < len; k++){
                r = r + String.format("%-10s", mutex[j][k]);
            }
            r = r + "\n\n";
        }
        aM.setText(r);
    }

}
