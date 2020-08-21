package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoticeController {

    @FXML
    private TextField content;
    @FXML
    private TextField tW;
    @FXML
    private TableColumn<Notice, String> cC;
    @FXML
    private TableColumn<Notice, String> cT;
    @FXML
    private TableColumn<Notice, String> cTW;
    @FXML
    private TableView<Notice> tbN;

    private ObservableList<Notice> noticeData = FXCollections.observableArrayList();
    Connection conn;
    Notice n;


    //从数据库调取所有信息
    public ResultSet getAll(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.notice");
        return ps.executeQuery();
    }
    //插入新数据
    public boolean insertNew(Connection conn, Notice n) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO attraction.notice(ntime, content, toWhom) VALUES (?,?,?)");
        ps.setString(1, n.getTime());
        ps.setString(2, n.getContent());
        ps.setString(3, n.getToWhom());
        return ps.execute();
    }

    //初始化页面
    @FXML
    private void initialize() {
        noticeData.clear();
        content.setText(null);
        tW.setText(null);
        conn = ConnectionFactory.getInstance().makeConnection();
        try {
            ResultSet r = getAll(conn);
            while (r.next()) {
                this.n = new Notice(r.getString("ntime"), r.getString("content"), r.getString("towhom"));
                this.noticeData.add(this.n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //显示全部景点信息
        cT.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        cC.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        cTW.setCellValueFactory(cellData -> cellData.getValue().toWhomProperty());
        tbN.setItems(noticeData);
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

    //发布公告
    public void publish(){
        Date date = new Date();//设置要获取到什么样的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//获取String类型的时间
        String t = sdf.format(date);

        this.n = new Notice(t, content.getText(), tW.getText());
        try {
            if (!insertNew(conn, this.n))
                System.out.println("插入成功！");
            else
                System.out.println("插入失败！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //刷新
        initialize();
    }
}
