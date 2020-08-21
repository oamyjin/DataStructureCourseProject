package application;


import java.lang.String;

import javafx.beans.property.*;


public class Node {
    private String name;
    private String description;
    private int pop;
    private boolean hasRest;
    private boolean hasWC;
    private MyList<Edge> adjacency;

    private int id;


    public Node(String name, String description, int pop, boolean hasRest, boolean hasWC) {
        this.name = name;
        this.description = description;
        this.pop = pop;
        this.hasRest = hasRest;
        this.hasWC = hasWC;
        adjacency = new MyList<>();
        id = 0;
    }


    public int getId(){ return id;}
    public int getPop() {
        return pop;
    }
    public boolean getHasRest() {
        return hasRest;
    }
    public boolean getHasWC() {
        return hasWC;
    } public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public MyList<Edge> getAdjacency(){return adjacency;};
    public void setDescription(String description){ this.description = description;};
    public void setPop(int pop){ this.pop = pop;};
    public void setHasWC(boolean hasWC){ this.hasWC = hasWC;};
    public void setHasRest(boolean hasRest){ this.hasRest = hasRest;};
    public void setId(int id){ this.id = id;}
    public String toString(){
        return "name = " + name + " description = " + description + " pop = " + pop + " hasRest = " + hasRest + " hasWC = " + hasWC;
    }

    //返回界面所需数据
    public StringProperty nameProperty() {
        StringProperty name = new SimpleStringProperty(this.name);
        return name;
    }
    public StringProperty descriptionProperty() {
        StringProperty description = new SimpleStringProperty(this.description);
        return description;
    }
    public StringProperty popProperty() {
        StringProperty pop = new SimpleStringProperty(Integer.toString(this.pop));
        return pop;
    }
    public StringProperty hasRestProperty() {
        String s;
        if(this.hasRest == true)
            s = "有";
        else
            s = "无";
        StringProperty hasRest = new SimpleStringProperty(s);
        return hasRest;
    }
    public StringProperty hasWCProperty() {
        String s;
        if(this.hasWC == true)
            s = "有";
        else
            s = "无";
        StringProperty hasWC = new SimpleStringProperty(s);
        return hasWC;
    }
}
