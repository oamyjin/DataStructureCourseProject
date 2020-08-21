package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Notice {
    private String time;
    private String content;
    private String toWhom;

    public Notice(String time, String content, String toWhom){
        this.time = time;
        this.content = content;
        this.toWhom = toWhom;
    }

    public String getTime() {
        return time;
    }
    public String getContent() {
        return content;
    }
    public String getToWhom() {
        return toWhom;
    }

    //返回界面所需数据
    public StringProperty timeProperty() {
        StringProperty time = new SimpleStringProperty(this.time);
        return time;
    }
    public StringProperty contentProperty() {
        StringProperty content = new SimpleStringProperty(this.content);
        return content;
    }
    public StringProperty toWhomProperty() {
        StringProperty toWhom = new SimpleStringProperty(this.toWhom);
        return toWhom;
    }
}
