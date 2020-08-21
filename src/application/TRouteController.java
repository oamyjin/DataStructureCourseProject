package application;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 游客入口——路线信息页面
 *
 */
public class TRouteController {

    @FXML
    private TextField aA;
    @FXML
    private TextField aB;
    @FXML
    private TextField aC;
    @FXML
    private TextField aD;
    @FXML
    private Label rResult;
    @FXML
    private Label cResult;
    @FXML
    private TableView<Edge> tbR;
    @FXML
    private TableColumn<Edge, String> cRF;
    @FXML
    private  TableColumn<Edge, String> cRTo;
    @FXML
    private  TableColumn<Edge, String> cRD;
    @FXML
    private  TableColumn<Edge, String> cRT;


    Graph g = Main.getGraph();
    Edge e;
    Connection conn;
    int sourceIndex = 0;
    int desIndex = 0;
    int[] vis;//dijkstra访问矩阵，记录是否访问过
    int[] par;//dijkstra结点矩阵，记录路线结果经过的结点
    double[] dis;//dijkstra距离矩阵，记录起点到结点之间距离
    int[][]  preTable;//floyed结点矩阵，
    double[][] pathMatirx;
    MyList<String> res = new MyList<>();//dfs存放回路结果
    private ObservableList<Edge> edgeData = FXCollections.observableArrayList();
    int count = 0;
    int len;

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


    //初始化页面
    @FXML
    private void initialize() {

        conn = ConnectionFactory.getInstance().makeConnection();

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


    //返回初始页面
    public void back() {
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tourists.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *
     * 最短路径搜索,两点之间路线 —— 给定两个景点，求出最短路径
     * dijkstra算法
     * floyd算法
     *
     * */
    public void routeCreate(){
        nodesIndexSR();//给结点赋值id，并记录入口与出口的编号
        dijkstra();//使用dijkstra算法
//        floyd();//使用floyd算法
        outputShortestPath(1);
    }
    /*
     *
     * Dijkstra 迪杰斯特拉算法
     *
     * */
    public void dijkstra(){
        System.out.println("Source "+sourceIndex+"DesIndex"+desIndex);
        dis = new double[len];
        vis = new int[len];
        par = new int[len];
        //初始化最短距离数组
        for(int i = 0; i < len; i++){
            dis[i] = (i==sourceIndex ? 0 : 32767);
        }
        for(int i = 0; i < len; i++){
            double m = 32767;
            int minPos = -1;
            for(int j = 0; j < len; j++){//选择下一个节点
                if(vis[j] == 0 && dis[j] < m)
                    m = dis[minPos = j];
            }
            vis[minPos] = 1;//某一结点遍历结束
            for(int j = 0; j < len; j++){//经过中间点距离总和小于直接距离
                if(vis[j] == 0 && dis[minPos] + getLength(minPos, j) < dis[j]){
                    dis[j] = dis[minPos]+getLength(minPos, j);
                    par[j] = minPos;
                }
            }
        }
    }
    /* 获得两点之间的距离 */
    private double getLength(int fromIndex, int toIndex){
        MyListItem<Edge> t = g.nodes.getMyListItem(fromIndex+1).getItem().getAdjacency().getHead();
        double length = 32767;
        while(t != null){
            if((t.getItem().getTo().getId() == toIndex && t.getItem().getFrom().getId() == fromIndex) ||
                    (t.getItem().getFrom().getId() == toIndex && t.getItem().getTo().getId() == fromIndex)){
                length = t.getItem().getDistance();
                break;
            }
            t = t.getNext();
        }
        return length;
    }
    /* 输出路线结果 */
    public void outputShortestPath(int flag){
        int[] path = new int[20];
        //放入最短路径长度
        String r = "最短路径长度： " + dis[desIndex] + '\n' + '\n';
        //放入结果
        int i = 0;
        int t = desIndex;
        while(t != sourceIndex){
            path[i] = t;
            t = par[t];
            i++;
        }
        //倒序输出结果
        path[i] = t;
        for(int j = i; j >= 0; j--) {
            r = r + g.getNodeItem(path[j]).getName() + " - ";
            res.add(g.getNodeItem(path[j]).getName());
        }
        r = r + "- -完成路线";
        if(flag == 1)
            rResult.setText(r);
    }
    /* 给结点赋值,临时id，返回长度*/
    public int nodesId(){
        int index = 0;
        //赋值id
        MyIterator<Node> itN = new MyIterator<>(g.nodes);
        while(itN.hasNext()){
            MyListItem<Node> itemN = itN.next();
            itemN.getItem().setId(index);
            index++;
        }
        return index;
    }
    /*
     *
     * Floyd弗洛伊德算法
     *
     * */
    public void floyd(){
        int len = nodesId();
        double[][] matrix = adjacency();//获得邻接矩阵
        pathMatirx = new double[len][len];//路径矩阵（D），表示顶点到顶点的最短路径权值之和的矩阵，初始时，就是图的邻接矩阵。
        preTable = new int[len][len];//前驱表（P），P[m][n] 的值为 m到n的最短路径的前驱顶点，如果是直连，值为n。也就是初始值
        //初始化矩阵
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                pathMatirx[i][j] = matrix[i][j];
                preTable[i][j] = j;
            }
        }
        for (int k = 0; k < matrix.length; k++) {    //循环 中间经过顶点
            for (int m = 0; m < matrix.length; m++) {    //循环所有路径
                for (int n = 0; n < matrix.length; n++) {
                    double mn = pathMatirx[m][n];
                    double mk = pathMatirx[m][k];
                    double kn = pathMatirx[k][n];
                    double addedPath = (mk == 32767 || kn == 32767)? 32767 : mk + kn;
                    if (mn > addedPath) {
                        pathMatirx[m][n] = addedPath;    //如果经过k顶点路径比原两点路径更短，将两点间权值设为更小的一个
                        preTable[m][n] = preTable[m][k];    //前驱设置为经过下标为k的顶点
                    }
                }
            }
        }
    }
   /* 打印所有最短路径*/
    public void print(double[][] matrix) {
        for (int m = 0; m < matrix.length; m++) {
            for (int n = m + 1; n < matrix.length; n++) {
                int k = preTable[m][n];
                System.out.print("(" + m + "," + n + ")" + pathMatirx[m][n] + ":  ");
                System.out.print(m);
                while (k != n) {
                    System.out.print("->" + k);
                    k = preTable[k][n];
                }
                System.out.println("->" + n);
            }
            System.out.println();
        }
    }


    /*
     *
     * 景点回路搜索 —— 给出一个景点作为出入口或者给出两个景点分别为出入口，求出能够经过所有景点的路径
     * dfs算法
     * hamilton算法
     *
     */
    public void circuitCreate(){
        nodesIndexC();    //给结点赋值id，同时记录入口与出口结点id
        res = new MyList<>();    //存储结果
        //入口与出口景点为同一个，入口与出口景点不为同一个，是略有不同的深度优先算法
        if(aC.getText().equals(aD.getText()))
            dfs();
        else
            dfs2();
        outputCirciutPath();    //输出回路结果
//        hamilton();//哈密尔顿回路算法
    }
    /*
     * 输出回路结果
     * */
    public void outputCirciutPath(){
        //放入结果
        int i = 0;
        String r = aC.getText();
        String temp = "";
        MyIterator<String> it = new MyIterator<>(res);
        while(it.hasNext()){
            i++;
            String last;
            last = temp;
            temp = it.next().getItem();
            if(!last.equals(temp))
                r = r + " -> "+ temp;
            if(i == 7) {    //控制页面输出排版
                i = 0;
                r = r + '\n';
            }
        }
        if(!temp.equals(aD.getText()))
            r = r + " -> " + aD.getText();
        cResult.setText(r);
    }
    /*
     * 深度优先算法寻求回路
     * 起点和终点为同一个点
     */
    public void dfs(){
        int start = sourceIndex;
        double [][] matrixTemp = adjacency();
        double [][] matrix  = new double[matrixTemp.length][matrixTemp.length];
        for(int i = 0; i < matrixTemp.length; i++) {
            for (int j = 0; j < matrixTemp.length; j++) {
                if(matrixTemp[i][j] == 0 || matrixTemp[i][j] == 32767) {
                    matrix[i][j] = 0;
                }else {
                    matrix[i][j] = 1;
                }
            }
        }
        int length = matrix.length;
        if(sourceIndex != 0) {
            for(int i = 0; i < length; i++) {
                double weightTemp = matrix[0][i];
                matrix[0][i] = matrix[sourceIndex][i];
                matrix[sourceIndex][i] = weightTemp;
            }
            for(int i = 0; i < length; i++) {
                double weightTemp = matrix[i][0];
                matrix[i][0] = matrix[i][sourceIndex];
                matrix[i][sourceIndex] = weightTemp;
            }
        }
        int[] value = new int[length];
        for(int i = 0; i < value.length;i++){
            if(value[i] == 0){
                dfsVisit(value,matrix,i,start);
            }
        }
        for(int i = 0; i < value.length; i++) {
            if(value[i] == count) {
                if(matrix[i][0] == 1) {
                    System.out.println(g.getNodeItem(0).getName());
                }else {
                    g.nodes.getMyListItem(1).getItem().setId(0);
                    g.nodes.getMyListItem(start + 1).getItem().setId(start);
                    sourceIndex = i;
                    desIndex = start;
                    dijkstra();
                }
                break;
            }
        }
    }

    /*
     * DFS深度优先算法寻求回
     * 起点和终点不一样
     */
    public void dfs2(){
        nodesId();//给景点node赋值id
        int start = sourceIndex;
        int end = desIndex;
        System.out.println(sourceIndex);
        show(adjacency());
        System.out.println();

        double [][] matrixTemp = adjacency();
        show(adjacency());
        System.out.println();
        show(matrixTemp);
        System.out.println();
        double [][] matrix  = new double[matrixTemp.length][matrixTemp.length];
        for(int i = 0; i < matrixTemp.length; i++) {
            for (int j = 0; j < matrixTemp.length; j++) {
                if(matrixTemp[i][j] == 0 || matrixTemp[i][j] == 32767) {
                    matrix[i][j] = 0;
                }else {
                    matrix[i][j] = 1;
                }
            }
        }

        g.nodes.getMyListItem(1).getItem().setId(sourceIndex);
        g.nodes.getMyListItem(sourceIndex + 1).getItem().setId(0);


        int length = matrix.length;
        if(sourceIndex != 0) {
            for(int i = 0; i < length; i++) {
                double weightTemp = matrix[0][i];
                matrix[0][i] = matrix[sourceIndex][i];
                matrix[sourceIndex][i] = weightTemp;
            }
            for(int i = 0; i < length; i++) {
                double weightTemp = matrix[i][0];
                matrix[i][0] = matrix[i][sourceIndex];
                matrix[i][sourceIndex] = weightTemp;
            }
        }

        show(matrix);
        int[] value = new int[length];
        for(int i = 0; i < value.length;i++){
            if(value[i] == 0){
                dfsVisit(value,matrix,i,start);
            }
        }
        for(int i = 0; i < value.length; i++) {
            if(value[i] == count) {
                if(i != end) {
                    if(matrix[i][0] == 1) {
                        System.out.println(g.getNodeItem(0).getName());
                    }else {
                        g.nodes.getMyListItem(1).getItem().setId(0);
                        g.nodes.getMyListItem(start + 1).getItem().setId(start);
                        sourceIndex = i;
                        desIndex = end;
                        dijkstra();
                        outputShortestPath(0);
                    }
                }

                break;
            }
        }
    }

    /*
     * adjMatrix是待遍历图的邻接矩阵
     * value是待遍历图顶点用于是否被遍历的判断依据，0代表未遍历，非0代表已被遍历
     * result用于存放深度优先遍历的顶点顺序
     * number是当前正在遍历的顶点在邻接矩阵中的数组下标编号
     */
    public void dfsVisit(int[] value, double[][] matrix, int number, int start){
        value[number] = ++count;               //把++count赋值给当前正在遍历顶点判断值数组元素，变为非0，代表已被遍历
        for(int i = 0; i < value.length; i++){
            if(matrix[number][i] == 1 && value[i] == 0){          //当当前顶点的相邻有相邻顶点可行走且其为被遍历
                /*用迪杰斯特拉解决两点间最短路径*/
                for(int j = 0; j < value.length; j++) {
                    if(value[j] == count) {
                        if(matrix[j][i] == 1) {
                            res.add(g.getNodeItem(i).getName());
                        }else {
                            sourceIndex = j;
                            desIndex = i;
                            dijkstra();
                            outputShortestPath(0);
                        }
                        break;
                    }
                }
                dfsVisit(value,matrix,i,start);   //执行递归，行走第i个顶点
            }
        }
    }
    //邻接矩阵
    public double[][] adjacency(){
        double[][] martix;
        //邻接矩阵
        martix = new double[len][len];
        MyIterator<Node> itN = new MyIterator<>(g.nodes);
        int i = 0;
        while(itN.hasNext()){
            //遍历adjacency，把有连接的找到
            MyListItem<Node> itemN = itN.next();
            MyIterator<Edge> itA = new MyIterator<>(itemN.getItem().getAdjacency());
            while(itA.hasNext()){
                MyListItem<Edge> itemA = itA.next();
                if(itemA.getItem().getFrom().getName().equals(itemN.getItem().getName())){
                    martix[i][itemA.getItem().getTo().getId()] = 1;
                }
                if(itemA.getItem().getTo().getName().equals(itemN.getItem().getName())){
                    martix[i][itemA.getItem().getFrom().getId()] = 1;
                }
            }
            for(int j = 0; j < len; j++){
                if( j != i && martix[i][j] != 1){
                    martix[i][j] = 32767;
                }
            }
            i++;
        }
        return martix;
    }
    //得到回路初始和终止结点id
    public void nodesIndexC(){
        int index = 0;
        //赋值id
        MyIterator<Node> itN = new MyIterator<>(g.nodes);
        while(itN.hasNext()){
            MyListItem<Node> itemN = itN.next();
            itemN.getItem().setId(index);
            if(itemN.getItem().getName().equals(aC.getText()))
                sourceIndex = index;
            if(itemN.getItem().getName().equals(aD.getText()))
                desIndex = index;
            index++;
        }
        len = index;
    }
    //得到最短路径时起点和终点
    public void nodesIndexSR(){
        int index = 0;
        //赋值id
        MyIterator<Node> itN = new MyIterator<>(g.nodes);
        while(itN.hasNext()){
            MyListItem<Node> itemN = itN.next();
            itemN.getItem().setId(index);
            if(itemN.getItem().getName().equals(aA.getText()))
                sourceIndex = index;
            if(itemN.getItem().getName().equals(aB.getText()))
                desIndex = index;
            index++;
        }
        len = index;
    }
    /* 显示矩阵 */
    public void show(double[][] matrix){
        for(int j = 0; j < matrix.length; j++){
            for(int k = 0; k < matrix.length; k++){
                System.out.print(String.format("%-10s", matrix[j][k]));
            }
            System.out.println();
        }
    }

    /**
     *
     * 哈密尔顿回路
     *
     */
    public void hamilton() {
        nodesId();
        double[][] reachability = adjacency();
        System.out.println("邻接矩阵");
        show(reachability);
        boolean[] used = new boolean[reachability.length];//标记图中顶点是否被访问
        int[] path = new int[reachability.length];//记录哈密顿回路路径，开始时未选中起点及到达任何顶点
        for (int i = 0; i < reachability.length; i++) {
            used[i] = false;//初始化，所有顶点均未被遍历
            path[i] = -1;
        }
        used[0] = true;//表示从第1个顶点开始遍历
        path[0] = 0;//表示哈密尔顿回路起点为第0个顶点
        dfs(reachability, path, used, 1);//从第0个顶点开始进行深度优先遍历,如果存在哈密顿回路，输出一条回路，否则无输出
    }
    //深度优先算法,参数step:当前行走的步数，即已经遍历顶点的个数
    public boolean dfs(double[][] reachability, int[] path, boolean[] used, int step) {
        System.out.println("step"+step);
        if(step == reachability.length) {//遍历图中所有顶点
            if(reachability[path[step - 1]][0] == 1) {//最后一步到达的顶点能够回到起点
                for(int i = 0; i < reachability.length; i++)
                    System.out.print(g.nodes.getMyListItem(path[i]).getItem().getName()+"——>");
                System.out.print(aC.getText());
                System.out.println();
                return true;
            }
            return false;
        }
        else {
            for(int i = 0; i < reachability.length; i++) {
                if(!used[i] && reachability[path[step - 1]][i] == 1) {
                    used[i] = true;
                    path[step] = i;
                    if(dfs(reachability, path, used, step + 1))
                        return true;
                    else {
                        used[i] = false;    //进行回溯处理
                        path[step] = -1;
                    }
                }
                if (path[reachability.length-1] != -1) {
                    step = reachability.length;
                    outputCirciut(path);
                    break;
                }
            }
        }
        return false;
    }
    //输出回路
    public void outputCirciut(int[] path) {
        path[path.length] = 2;
        for (int i = 0; i < path.length; i++) {
            System.out.print(g.nodes.getMyListItem(path[i]).getItem().getName() + "-->");
        }
        System.out.print(((char) (path[0] + 'a')));
        System.out.println();
    }
}