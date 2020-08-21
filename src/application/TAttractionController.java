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

public class TAttractionController {
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
    private TableColumn<Notice, String> cNT;
    @FXML
    private TableColumn<Notice, String> cNC;
    @FXML
    private TableView<Notice> tbN;
    @FXML
    private TextField queryKeyWord;
    @FXML
    private Pane infoOne;
    @FXML
    private MenuButton type;
    @FXML
    private TextField queryOneKeyWord;
    @FXML
    private Label aN;
    @FXML
    private Label aP;
    @FXML
    private Label aHWC;
    @FXML
    private Label aHR;
    @FXML
    private Label aD;

    Graph g = Main.getGraph();
    Node n;
    Notice notice;
    private ObservableList<Node> nodeData = FXCollections.observableArrayList();
    private ObservableList<Notice> noticeData = FXCollections.observableArrayList();
    Connection conn;

    //从数据库调取所有景点信息P
    public ResultSet getAllP(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot ORDER BY pop DESC");
        return ps.executeQuery();
    }
    //调取所有景点信息HWC
    public ResultSet getAllW(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot WHERE haswc = 1");
        return ps.executeQuery();
    }
    //调取所有景点信息HR
    public ResultSet getAllR(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot WHERE hasrest = 1");
        return ps.executeQuery();
    }
    //调取所有路线信息
    public ResultSet getOne(Connection conn, String n) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot WHERE name = '" + n + "'");
        return ps.executeQuery();
    }
    //调取特定景点信息P
    public ResultSet getAP(Connection conn, String keyWord) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot WHERE name LIKE '%" + keyWord + "%' OR description LIKE '%" + keyWord + "%' ORDER BY pop DESC ");
        return ps.executeQuery();
    }
    //调取特定景点信息HWC
    public ResultSet getAW(Connection conn, String keyWord) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot WHERE name LIKE '%" + keyWord + "%' OR description LIKE '%" + keyWord + "%' AND haswc = 1");
        return ps.executeQuery();
    }
    //调取特定景点信息HR
    public ResultSet getAR(Connection conn, String keyWord) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot WHERE name LIKE '%" + keyWord + "%' OR description LIKE '%" + keyWord + "%' AND hasrest = 1");
        return ps.executeQuery();
    }
    //从数据库调取公告信息
    public ResultSet getNotice(Connection conn, String tW) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.notice WHERE toWhom = '" + tW + "'");
        return ps.executeQuery();
    }

    //初始化页面
    @FXML
    private void initialize() {
        nodeData.clear();
        infoOne.setVisible(false);
        conn = ConnectionFactory.getInstance().makeConnection();
        //从数据库中取出数据
        try {
            ResultSet r = getAllP(conn);
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
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tourists.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //查询，根据关键字（name或者descrition）
    public void queryA() throws SQLException {
        this.nodeData.clear();
        try {
            ResultSet r;
            if (this.queryKeyWord.getText().isEmpty()) {//若输入框为空，显示全部
                if(type.getText().equals("欢迎度"))
                    r = getAllP(conn);
                else if(type.getText().equals("有公厕"))
                    r = getAllW(conn);
                else
                    r = getAllR(conn);
                while (r.next()) {
                    this.n = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
                    this.nodeData.add(this.n);
                }
            } else {
                if(type.getText().equals("欢迎度"))
                    r = getAP(conn, queryKeyWord.getText());
                else if(type.getText().equals("有公厕"))
                    r = getAW(conn, queryKeyWord.getText());
                else
                    r = getAR(conn, queryKeyWord.getText());
                while (r.next()) {
                    this.n = new Node(r.getString("name"), r.getString("description"), r.getInt("pop"), r.getBoolean("hasrest"), r.getBoolean("haswc"));
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
    //按欢迎度排序显示景点信息
    public void setTPop(){
        type.setText("欢迎度");
    }
    //显示有公厕的景点信息
    public void setTHW(){
        type.setText("有公厕");
    }
    //显示有休息区的显示景点信息
    public void setTHR(){
        type.setText("有休息区");
    }
    //查询某一景点信息
    public void queryOne(){
        noticeData.clear();
        //显示景点信息
        infoOne.setVisible(true);
        try {
            ResultSet r = getOne(conn, this.queryOneKeyWord.getText());
            if(!r.next()){
                aN.setText("不存在该景点");
            }
            else {
                aN.setText(r.getString("name"));
                aD.setText(r.getString("description"));
                aP.setText(String.valueOf(r.getInt("pop")));
                if(r.getBoolean("hasrest"))
                    aHR.setText("有");
                else
                    aHR.setText("无");
                if(r.getBoolean("haswc"))
                    aHWC.setText("有");
                else
                    aHWC.setText("无");
            }
            //显示公告
            r = getNotice(conn, aN.getText());
            while (r.next()) {
                this.notice = new Notice(r.getString("ntime"), r.getString("content"), r.getString("toWhom"));
                this.noticeData.add(this.notice);
            }
            cNT.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
            cNC.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
            tbN.setItems(noticeData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     *
     * 快速排序
     * 根据热度排序
     *
     * */
    public void quickSort(MyListItem<Node> begin, MyListItem<Node> end){
        if(begin == null || begin == end)
            return;
        MyListItem<Node> index = paration(begin, end);
        quickSort(begin, index);
        quickSort(index.getNext(), end);
    }
    /**
     * 划分函数，以头结点值为基准元素进行划分
     */
    public MyListItem<Node> paration(MyListItem<Node> begin, MyListItem<Node> end){
        if(begin == null || begin == end)
                return begin;
        int val = begin.getItem().getPop();  //基准元素
        MyListItem<Node> index = begin;
        MyListItem<Node> cur = begin.getNext();
        while(cur != end){
            if(cur.getItem().getPop() < val){  //交换
                int tmp = cur.getItem().getPop();
                cur.getItem().setPop(index.getItem().getPop());
                index.getItem().setPop(tmp);
            }
            cur = cur.getNext();
        }
        begin.getItem().setPop(index.getItem().getPop());
        index.getItem().setPop(val);
        return index;
    }
    /**
     *
     * 根据关键字搜索
     * 可在名称中也可以在描述中
     *
     */
    public MyList<Node> getResultByKeyword(MyList<Node> nodes, String keyword){
        MyList<Node> r = new MyList<>();
        MyIterator<Node> it = new MyIterator<>(g.nodes);
        while(it.hasNext()){
            Node n = it.next().getItem();
            if(n.getName().contains(keyword) || n.getDescription().contains(keyword))
                r.add(n);
        }
        return r;
    }

}
