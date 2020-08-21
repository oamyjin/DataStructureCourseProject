package application;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    Graph g;

    //从数据库调取所有信息
    public ResultSet getAllS(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.spot");
        return ps.executeQuery();
    }
    //从数据库调取所有线路信息
    public ResultSet getAllR(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM attraction.route");
        return ps.executeQuery();
    }


    public Database(Graph graph){
        this.g = graph;
    }

    //把数据库中的信息构成图
    public Graph dataToGraph(){
        try{
            Connection conn;
            conn = ConnectionFactory.getInstance().makeConnection();
            ResultSet rs = getAllS(conn);
            ResultSet rr = getAllR(conn);
            Node from = null;
            Node to = null;
            g  = new Graph();
            //将结点信息加到图中
            while (rs.next()) {
                g.addNode(new Node(rs.getString("name"), rs.getString("description"), rs.getInt("pop"), rs.getBoolean("hasrest"), rs.getBoolean("haswc")));
            }
            //将边信息加到图中
            int i=0;
            while (rr.next()) {
                i++;
                System.out.println(i);
                int flag = 0;
                rs = getAllS(conn);
                while (rs.next()) {//找边的两结点
                    if(rs.getString("name").equals(rr.getString("fromn"))){
                        MyIterator<Node> itN = new MyIterator<>(g.nodes);
                        while(itN.hasNext()){
                            MyListItem<Node> itemN = itN.next();
                            if(itemN.getItem().getName().equals(rs.getString("name"))){
                                from = itemN.getItem();
                            }
                        }
                        flag++;
                    }
                    if(rs.getString("name").equals(rr.getString("ton"))){
                        MyIterator<Node> itN = new MyIterator<>(g.nodes);
                        while(itN.hasNext()){
                            MyListItem<Node> itemN = itN.next();
                            if(itemN.getItem().getName().equals(rs.getString("name"))){
                                to = itemN.getItem();
                            }
                        }
                        flag++;
                    }
                    if(flag == 2)
                        break;
                }
                Edge e = new Edge(from, to, rr.getDouble("distance"), rr.getDouble("time"));
                System.out.println("前g.addEge(e)边加到图中成功！");
                g.addEdge(e);
                System.out.println("后g.addEge(e)边加到图中成功！");
                //把边的两顶点adjacency更新
                MyIterator<Node> it = new MyIterator<>(g.nodes);
                flag = 0;
                while(it.hasNext()){
                    MyListItem<Node> item = it.next();
                    if(item.getItem().getName().equals(from.getName()) || item.getItem().getName().equals(to.getName())){
                        item.getItem().getAdjacency().add(e);
                        System.out.println("增加adjacency成功"+item.getItem().getAdjacency().toString());
                        flag++;
                    }
                    if(flag == 2){
                        break;
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return g;
    }
}
