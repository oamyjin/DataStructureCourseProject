package application;


public class Graph {

    private int numOfNode;
    private int numOfEdge;
    MyList<Node> nodes;
    MyList<Edge> edges;

    public Graph(){
        nodes = new MyList<>();
        edges = new MyList<>();
    }
    //增加景点（结点）
    public void addNode(Node node){
        nodes.add(node);
        numOfNode++;
    }
    //增加路线（边）
    public void addEdge(Edge edge){
        edges.add(edge);
        numOfEdge++;
    }
    //删除景点（结点）
    public void removeNode(Node node){
        nodes.remove(node);
        numOfNode--;
    }
    //删除边（路线）
    public void removeEdge(Edge edge){
        edges.remove(edge);
        numOfNode--;
    }
    //获取图中边数量
    public int getNumOfEdge(){
        return numOfEdge;
    }
    //获取图中结点数量
    public int getNumOfNode(){
        return numOfNode;
    }
    public Node getNodeItem(int i){
        MyIterator<Node> it = new MyIterator<>(nodes);
        while(it.hasNext()){
            Node n = it.next().getItem();
            if(n.getId() == i)
                return n;
        }
        return null;
    }
}
