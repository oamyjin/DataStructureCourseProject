package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddAController {


    @FXML
    private TextField addAN;
    @FXML
    private TextField addAP;
    @FXML
    private MenuButton addAHWC;
    @FXML
    private MenuButton addAHR;
    @FXML
    private TextField addAD;

    Graph g = Main.getGraph();
    Node n;
    Connection conn;

    //插入新信息到数据库
    public boolean insertNew(Connection conn, Node n) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO attraction.spot(name, description, pop, hasrest, haswc) VALUES (?,?,?,?,?)");
        ps.setString(1, n.getName());
        ps.setString(2, n.getDescription());
        ps.setInt(3, n.getPop());
        ps.setBoolean(4, n.getHasRest());
        ps.setBoolean(5, n.getHasWC());
        return ps.execute();
    }
    //更新景点信息
    public boolean update(Connection conn, Node n) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE attraction.spot SET description=?,pop=?,hasrest=?,haswc=? WHERE name=?");
        ps.setString(1, n.getDescription());
        ps.setInt(2, n.getPop());
        ps.setBoolean(3, n.getHasRest());
        ps.setBoolean(4, n.getHasWC());
        ps.setString(5, n.getName());
        return ps.execute();
    }

    @FXML
    private void initialize() {
        conn = ConnectionFactory.getInstance().makeConnection();
    }
    //确认键，增加信息
    public void addASure(){
        try {
            boolean w;
            boolean r;
            if(this.addAHR.getText().equals("有"))
                r = true;
            else
                r = false;
            if(this.addAHWC.getText().equals("有"))
                w = true;
            else
                w = false;
            //若输入为空
            this.n = new Node(this.addAN.getText(), this.addAD.getText(), Integer.valueOf(this.addAP.getText()), w, r);
            //若重复，不加
            int flag = 0;
            MyIterator<Node> it = new MyIterator<>(g.nodes);
            while(it.hasNext()){
                Node n = it.next().getItem();
                if(n.getName().equals(this.n.getName())){
                    flag = 1;
                }
            }
            if(flag == 0) {
                g.addNode(this.n);//加入到图中
                if (!insertNew(conn, this.n))
                    System.out.println("插入成功！");
                else
                    System.out.println("插入失败！");
            }
            else
            {
                mosifySure();
            }
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/attraction.fxml"))));
            stage.show();
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public void mosifySure(){
        try {
            boolean w;
            boolean r;
            if (this.addAHR.getText().equals("有"))
                r = true;
            else
                r = false;
            if (this.addAHWC.getText().equals("有"))
                w = true;
            else
                w = false;
            this.n = new Node(this.addAN.getText(), this.addAD.getText(), Integer.valueOf(this.addAP.getText()), w, r);
            if (!update(conn, this.n))
                System.out.println("更新成功！");
            else
                System.out.println("更新失败！");
            //在图中更新数据
            MyIterator<Node> it = new MyIterator<>(g.nodes);
            while(it.hasNext()){
                MyListItem<Node> n = it.next();
                if(n.getItem().getName().equals(this.addAN.getText())){
                    n.getItem().setDescription(this.addAD.getText());
                    n.getItem().setPop(Integer.valueOf(this.addAP.getText()));
                    n.getItem().setHasWC(w);
                    n.getItem().setHasRest(r);
                }
            }
            //刷新
            initialize();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setHWCY(){
        addAHWC.setText("有");
    }
    public void setHWCN(){
        addAHWC.setText("无");
    }
    public void setHRY(){
        addAHR.setText("有");
    }
    public void setHRN(){
        addAHR.setText("无");
    }
}
