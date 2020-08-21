package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttracionController {


    @FXML
    private TableColumn<Node, String> cAN;
    @FXML
    private TableColumn<Node, String> cAD;
    @FXML
    private TableColumn<Node, String> cAP;
    @FXML
    private TableColumn<Node, String> cAR;
    @FXML
    private TableColumn<Node, String> cAW;
    @FXML
    private TableView<Node> tbA;
    @FXML
    private TextField queryKeyWord;
    @FXML
    private Pane modifyP;
    @FXML
    private Label mN;
    @FXML
    private TextField mP;
    @FXML
    private MenuButton mR;
    @FXML
    private MenuButton mW;
    @FXML
    private TextField mD;

    Graph g = Main.getGraph();
    Node n;
    private ObservableList<Node> nodeData = FXCollections.observableArrayList();
    Connection conn;

    //从数据库调取所有景点信息
    public ResultSet getAll(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot");
        return ps.executeQuery();
    }
    //调取所有路线信息
    public ResultSet getAllR(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.route");
        return ps.executeQuery();
    }
    //调取特定信息
    public ResultSet getA(Connection conn, String keyWord) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot WHERE name LIKE '%" + keyWord + "%' OR description LIKE '%" + keyWord + "%'");
        return ps.executeQuery();
    }
    //删除特定景点信息
    public boolean remove(Connection conn, String n) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM attraction.spot WHERE name = '" + n + "'");
        return ps.execute();
    }
    //删除特定线路信息
    public boolean removeR(Connection conn, String n) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM attraction.route WHERE fromn = '" + n + "'" + "OR ton = '" + n + "'");
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

    //初始化页面
    @FXML
    private void initialize() {
        nodeData.clear();
        modifyP.setVisible(false);
        conn = ConnectionFactory.getInstance().makeConnection();
        //从数据库中取出数据
        try {
            ResultSet r = getAll(conn);
            while (r.next()) {
                this.n = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
                this.nodeData.add(this.n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //显示全部景点信息
        cAN.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        cAD.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        cAP.setCellValueFactory(cellData -> cellData.getValue().popProperty());
        cAR.setCellValueFactory(cellData -> cellData.getValue().hasRestProperty());
        cAW.setCellValueFactory(cellData -> cellData.getValue().hasWCProperty());
        tbA.setItems(nodeData);
    }

    //返回管理员页面
    public void back() {
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/admin.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //查询，根据关键字（name或者descrition）
    public void queryA() throws SQLException {
        String keyWord = this.queryKeyWord.getText();
        this.nodeData.clear();
        try {
            if (this.queryKeyWord.getText().isEmpty()) {//若输入框为空，显示全部
                ResultSet r = getAll(conn);
                while (r.next()) {
                    this.n = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
                    this.nodeData.add(this.n);
                }
            } else {
                ResultSet rs = getA(conn, keyWord);
                while (rs.next()) {
                    this.n = new Node(rs.getString("name"), rs.getString("description"), rs.getInt("pop"), rs.getBoolean("hasrest"), rs.getBoolean("haswc"));
                    this.nodeData.add(this.n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cAN.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        cAD.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        cAP.setCellValueFactory(cellData -> cellData.getValue().popProperty());
        cAR.setCellValueFactory(cellData -> cellData.getValue().hasRestProperty());
        cAW.setCellValueFactory(cellData -> cellData.getValue().hasWCProperty());
        tbA.setItems(nodeData);
    }

    //增加景点信息
    public void addA() {
        modifyP.setVisible(false);
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/addA.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNodedata(Node o) {
        this.n = o;
    }
    //删除景点信息
    public void removeA() {
        modifyP.setVisible(false);
        this.tbA.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.setNodedata(newValue);
        });
        MyIterator<Node> itN = new MyIterator<>(g.nodes);
        while(itN.hasNext()){//遍历图找到结点
            MyListItem<Node> itemN = itN.next();
            if(itemN.getItem().getName().equals(this.n.getName())){
                if(itemN.getItem().getAdjacency() != null){
                    MyIterator<Edge> itA = new MyIterator<>(itemN.getItem().getAdjacency());
                    //遍历adjacency，找另一个节点，更新adjacency
                    while (itA.hasNext()) {
                        MyListItem<Edge> itemA = itA.next();
                        System.out.println("itemA--"+itemA.toString());
                        if(itemA.getItem().getFrom().getName().equals(itemN.getItem().getName())){//找另一个节点，更新adjacency
                            itemA.getItem().getTo().getAdjacency().remove(itemA.getItem());//更新adjacency
                            //图中删除节点所在边
                            MyIterator<Edge> itE = new MyIterator<>(g.edges);
                            while (itE.hasNext()){
                                MyListItem<Edge> itemE = itE.next();
                                if(itemE.getItem().toString().equals(itemA.getItem().toString())){
                                    itE.remove();
                                }
                            }
                            System.out.println("节点_边删除成功！");
                        }
                        if(itemA.getItem().getTo().getName().equals(itemN.getItem().getName())){//找另一个节点
                            //更新adjacency
                            Edge dItem = itemA.getItem();
                            itemA.getItem().getFrom().getAdjacency().remove(dItem);
                            //图中删除边
                            MyIterator<Edge> itE = new MyIterator<>(g.edges);
                            while (itE.hasNext()){
                                MyListItem<Edge> itemE = itE.next();
                                if(itemE.getItem().toString().equals(itemA.getItem().toString())){
                                    itE.remove();
                                    System.out.println("节点_边删除成功！"+itemE.toString());
                                }
                            }
                        }
                    }
                    //图中删除结点
                    itN.remove();
                    System.out.println("节点删除成功！");
                }
                break;
            }
        }
        //从数据库中删除
        try {
            removeR(conn, this.n.getName());
            removeR(conn, this.n.getName());
            if (!remove(conn, this.n.getName()))//将结点从数据库删除
                System.out.println(this.n.getName()+"删除成功！");
            else
                System.out.println(this.n.getName()+"删除失败！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //刷新
        initialize();
    }
    //修改景点信息
    public void modifyA() {
        this.tbA.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.setNodedata(newValue);
        });
        String  w;
        String r;
        if (this.n.getHasRest() == true)
            r = "有";
        else
            r = "无";
        if (this.n.getHasWC() == true)
            w = "有";
        else
            w = "无";
        //显示选中对象信息
        modifyP.setVisible(true);
        mN.setText(this.n.getName());
        mP.setText(String.valueOf(this.n.getPop()));
        mW.setText(w);
        mR.setText(r);
        mD.setText(String.valueOf(this.n.getDescription()));
    }
    public void mosifySure(){
        try {
            boolean w;
            boolean r;
            if (this.mR.getText().equals("有"))
                r = true;
            else
                r = false;
            if (this.mW.getText().equals("有"))
                w = true;
            else
                w = false;
            this.n = new Node(this.mN.getText(), this.mD.getText(), Integer.valueOf(this.mP.getText()), w, r);
            if (!update(conn, this.n))
                System.out.println("更新成功！");
            else
                System.out.println("更新失败！");
            //在图中更新数据
            MyIterator<Node> it = new MyIterator<>(g.nodes);
            while(it.hasNext()){
                MyListItem<Node> n = it.next();
                if(n.getItem().getName().equals(this.mN.getText())){
                    n.getItem().setDescription(this.mD.getText());
                    n.getItem().setPop(Integer.valueOf(this.mP.getText()));
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
    public void setHWY(){
        mW.setText("有");
    }
    public void setHWN(){
        mW.setText("无");
    }
    public void setHRY(){
        mR.setText("有");
    }
    public void setHRN(){
        mR.setText("无");
    }
}
