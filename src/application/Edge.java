package application;


import javafx.beans.property.*;

public class Edge {
    Node from;
    Node to;
    double distance;
    double time;

    public Edge(Node from, Node to, double distance, double time){
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.time = time;
    }

    public Node getFrom(){
        return from;
    }
    public Node getTo(){
        return to;
    }
    public double getDistance(){
        return distance;
    }
    public double getTime(){
        return time;
    }
    public void setDistance(double distance){
        this.distance = distance;
    }
    public void setTime(double time){
        this.time = time;
    }

    public String toString(){
        return "from = " + from.getName() + " to = " + to.getName() + " distance = " + distance + " time = " + time;
    }

    //返回界面所需数据
    public StringProperty fromProperty() {
        StringProperty from = new SimpleStringProperty(this.from.getName());
        return from;
    }
    public StringProperty toProperty() {
        StringProperty to = new SimpleStringProperty(this.to.getName());
        return to;
    }
    public StringProperty distanceProperty() {
        StringProperty distance = new SimpleStringProperty(Double.toString(this.distance));
        return distance;
    }
    public StringProperty timeProperty() {
        StringProperty time = new SimpleStringProperty(Double.toString(this.time));
        return time;
    }
}
