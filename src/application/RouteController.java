package application;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.String;

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

public class RouteController {


    @FXML
    private  TableView<Edge> tbR;
    @FXML
    private  TableColumn<Edge, String> cRF;
    @FXML
    private  TableColumn<Edge, String> cRTo;
    @FXML
    private  TableColumn<Edge, String> cRD;
    @FXML
    private  TableColumn<Edge, String> cRT;
    @FXML
    private Pane addR;
    @FXML
    private TextField aF;
    @FXML
    private TextField aTo;
    @FXML
    private TextField aT;
    @FXML
    private TextField aD;
    @FXML
    private Label spotWarning;
    @FXML
    private Pane modifyR;
    @FXML
    private Label mF;
    @FXML
    private Label mTo;
    @FXML
    private TextField mT;
    @FXML
    private TextField mD;
    @FXML
    private TextField queryKeyWord;


    Edge e;
    Graph g = Main.getGraph();
    private ObservableList<Edge> edgeData = FXCollections.observableArrayList();
    Connection conn;

    //调取路线信息
    public ResultSet getAll(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.route");
        return ps.executeQuery();
    }
    //调取景点信息
    public ResultSet getAllS(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot");
        return ps.executeQuery();
    }
    //增加
    public boolean insert(Connection conn, Edge e) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO attraction.route(fromn, ton, time, distance) VALUES (?,?,?,?)");
        ps.setString(1, e.getFrom().getName());
        ps.setString(2, e.getTo().getName());
        ps.setDouble(3, e.getTime());
        ps.setDouble(4, e.getDistance());
        return ps.execute();
    }
    //更新
    public boolean update(Connection conn, Edge e) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE attraction.route SET distance=?,time=? WHERE fromn=? AND ton=?");
        ps.setDouble(1, e.getTime());
        ps.setDouble(2, e.getDistance());
        ps.setString(3, e.getFrom().getName());
        ps.setString(4, e.getTo().getName());
        return ps.execute();
    }
    //调取特定信息
    public ResultSet getA(Connection conn, String keyWord) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.route WHERE fromn LIKE '%" + keyWord + "%' OR ton LIKE '%" + keyWord + "%'");
        return ps.executeQuery();
    }
    //删除特定线路信息
    public boolean remove(Connection conn, String f, String t) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM attraction.route WHERE fromn = '" + f + "'" + "AND ton = '" + t + "'");
        return ps.execute();
    }

    //初始化页面
    @FXML
    private void initialize() {
        conn = ConnectionFactory.getInstance().makeConnection();
        addR.setVisible(false);
        modifyR.setVisible(false);
        spotWarning.setVisible(false);
        edgeData.clear();
        //从数据库中取出数据
        try {
            ResultSet r = getAll(conn);
            Node from = null;
            Node to = null;

            while (r.next()) {
                int flag = 0;
                ResultSet rs = getAllS(conn);
                System.out.println(r.getString("fromn")+r.getString("ton"));
                while (rs.next()) {
                    System.out.println("  "+rs.getString("name"));
                    if(rs.getString("name").equals(r.getString("fromn"))){
                        from = new Node(rs.getString("name"), rs.getString("description"), rs.getInt("pop"), rs.getBoolean("hasrest"), rs.getBoolean("haswc"));
                        flag++;
                    }
                    if(rs.getString("name").equals(r.getString("ton"))){
                        to = new Node(rs.getString("name"), rs.getString("description"), rs.getInt("pop"), rs.getBoolean("hasrest"), rs.getBoolean("haswc"));
                        flag++;
                    }
                    if(flag == 2)
                        break;
                }
                this.e = new Edge(from, to, r.getDouble("distance"), r.getDouble("time"));
                this.edgeData.add(this.e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //显示全部边信息
        cRF.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        cRTo.setCellValueFactory(cellData -> cellData.getValue().toProperty());
        cRD.setCellValueFactory(cellData -> cellData.getValue().distanceProperty());
        cRT.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        tbR.setItems(edgeData);
    }

    //返回管理员页面
    public void back(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/admin.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //增加线路信息
    public void addR() throws SQLException {
        modifyR.setVisible(false);
        addR.setVisible(true);
    }
    public void addRSure(){
        Node from = null;
        Node to = null;
        ResultSet r;
        try {
            r = getAllS(conn);
            int flag = 0;
            while (r.next()) {
                if(aF.getText().equals(r.getString("name"))){
                    from = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
                    flag++;
                }
                if(aTo.getText().equals(r.getString("name"))){
                    to = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
                    flag++;
                }
                if(flag == 2)
                    break;
            }
            if(flag != 2){
                spotWarning.setVisible(true);
                return;
            }
            this.e = new Edge(from, to, Double.valueOf(aD.getText()), Double.valueOf(aT.getText()));
            //在图中增加边信息
            g.addEdge(this.e);//增加边
            MyIterator<Node> it = new MyIterator<>(g.nodes);//在该边两端结点的adjacency中增加该边
            while(it.hasNext()){
                MyListItem<Node> item = it.next();
                if(item.getItem().getName().equals(from.getName()) || item.getItem().getName().equals(to.getName())){
                    item.getItem().getAdjacency().add(e);
                }
            }
            //增加到数据库
            if (!insert(conn, this.e))
                System.out.println("插入成功！");
            else
                System.out.println("插入失败！");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        //刷新
        initialize();
    }
    public void setEdgeData(Edge o) {
        this.e = o;
    }
    //删除
    public void removeR(){
        Node from = null;
        Node to = null;
        int flag = 0;

        this.tbR.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.setEdgeData(newValue);
        });
        try {
            if (!remove(conn, this.e.getFrom().getName(), this.e.getTo().getName()))//删除数据库中数据
                System.out.println("删除成功！");
            else
                System.out.println("删除失败！");
            ResultSet r = getAllS(conn);
            while (r.next()) {
                if (this.e.getFrom().getName().equals(r.getString("name"))) {
                    from = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
                    flag++;
                }
                if (this.e.getFrom().getName().equals(r.getString("name"))) {
                    to = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
                    flag++;
                }
                if (flag == 2)
                    break;
            }
            //在图中删除边
            MyIterator<Node> itN = new MyIterator<>(g.nodes);
            while(itN.hasNext()){//找到边上的两个节点，把节点中adjacency该边删除
                MyListItem<Node> itemN = itN.next();
                if(itemN.getItem().getName().equals(from.getName()) || itemN.getItem().getName().equals(to.getName())){
                    itN.remove();
                    flag++;
                }
                if(flag == 2)
                    break;
            }
            MyIterator<Edge> itE = new MyIterator<>(g.edges);//在图中找到边，把该边删除
            while(itE.hasNext()){
                MyListItem<Edge> itemE = itE.next();
                if(itemE.getItem().toString().equals(this.e.toString())){
                    itE.remove();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //刷新
        initialize();
    }
    //修改路线信息
    public void modifyR(){
        addR.setVisible(false);
        modifyR.setVisible(true);
        this.tbR.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.setEdgeData(newValue);
        });
        //显示选中对象信息
        mF.setText(this.e.getFrom().getName());
        mTo.setText(this.e.getTo().getName());
        mD.setText(String.valueOf(this.e.distance));
        mT.setText(String.valueOf(this.e.time));
    }
    //确认修改
    public void modifyRSure(){
        Node from = null;
        Node to = null;
        ResultSet r;
        try {
            r = getAllS(conn);
            int flag = 0;
            while (r.next()) {
                if (mF.getText().equals(r.getString("name"))) {
                    from = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));flag++;
                }
                if (mTo.getText().equals(r.getString("name"))) {
                    to = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
                    flag++;
                }
                if (flag == 2)
                    break;
            }
            this.e = new Edge(from, to, Double.valueOf(mD.getText()), Double.valueOf(mT.getText()));
            //数据库中修改边
            if (!update(conn, this.e))
                System.out.println("更新成功！");
            else
                System.out.println("更新失败！");
            //在图中修改边
            MyIterator<Edge> it = new MyIterator<>(g.edges);
            while(it.hasNext()){
                MyListItem<Edge> n = it.next();
                if(n.getItem().getFrom().getName().equals(this.mF.getText()) && n.getItem().getTo().getName().equals(this.mT.getText())){
                    n.getItem().setDistance(Double.valueOf(this.mD.getText()));
                    n.getItem().setTime(Double.valueOf(this.mT.getText()));
                }
            }
            //刷新
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //检索
    public void query() throws SQLException {
        String keyWord = this.queryKeyWord.getText();
        Node from = null;
        Node to = null;
        this.edgeData.clear();
        try {
            if (this.queryKeyWord.getText().isEmpty()) {//若输入框为空，显示全部
                ResultSet r = getAll(conn);
                while (r.next()) {
                    int flag = 0;
                    ResultSet rs = getAllS(conn);
                    while (rs.next()) {
                        if (rs.getString("name").equals(r.getString("fromn"))) {
                            from = new Node(rs.getString("name"), rs.getString("description"), rs.getInt("pop"), rs.getBoolean("hasrest"), rs.getBoolean("haswc"));
                            flag++;
                        }
                        if (rs.getString("name").equals(r.getString("ton"))) {
                            to = new Node(rs.getString("name"), rs.getString("description"), rs.getInt("pop"), rs.getBoolean("hasrest"), rs.getBoolean("haswc"));
                            flag++;
                        }
                        if (flag == 2)
                            break;
                    }
                    this.e = new Edge(from, to, r.getDouble("distance"), r.getDouble("time"));
                    this.edgeData.add(this.e);
                }
            } else {
                ResultSet r = getA(conn, keyWord);
                while (r.next()) {
                    int flag = 0;
                    ResultSet rs = getAllS(conn);
                    while (rs.next()) {
                        if (rs.getString("name").equals(r.getString("fromn"))) {
                            from = new Node(rs.getString("name"), rs.getString("description"), rs.getInt("pop"), rs.getBoolean("hasrest"), rs.getBoolean("haswc"));
                            flag++;
                        }
                        if (rs.getString("name").equals(r.getString("ton"))) {
                            to = new Node(rs.getString("name"), rs.getString("description"), rs.getInt("pop"), rs.getBoolean("hasrest"), rs.getBoolean("haswc"));
                            flag++;
                        }
                        if (flag == 2)
                            break;
                    }
                    this.e = new Edge(from, to, r.getDouble("distance"), r.getDouble("time"));
                    this.edgeData.add(this.e);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        cRF.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        cRD.setCellValueFactory(cellData -> cellData.getValue().distanceProperty());
        cRTo.setCellValueFactory(cellData -> cellData.getValue().toProperty());
        cRT.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        tbR.setItems(edgeData);
    }
}
