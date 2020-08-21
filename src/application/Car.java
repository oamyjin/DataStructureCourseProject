package application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Car类
 * 存储车辆信息
 * 计算停车费用
 *
 */

public class Car {

    private String id;//车牌号
    private Date in;//进入时间
    private Date leave;//离开时间
    private int no;//停放车位
    private double fee;//停车费用
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//获取String类型的时间

    //构造方法
    public Car(String id){
        this.id = id;
    }
    //车辆入库停车
    public void park(int no){
        Date date = new Date();    //设置要获取到什么样的时间
        this.in = date;
        this.no = no;
    }
    //车辆在便道等待
    public void waiting(){
        Date date = new Date();    //设置要获取到什么样的时间
        this.in = date;
        no = -1;
    }
    //车辆出库离开
    public void leave(){
        Date date = new Date();    //设置要获取到什么样的时间
        this.leave = date;
        this.fee = culculatefee();
        no = 5;
    }
    //计算停车费用
    public double culculatefee(){
        int minutes = 0;
        String startTime = time(this.in);
        String endTime = time(this.leave);
        try {
            long start = sdf.parse(startTime).getTime();
            long end = sdf.parse(endTime).getTime();
            minutes = (int)(end - start) / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return minutes*1.0;
    }
    //获取时间
    public String time(Date date){
        String t = sdf.format(date);
        return t;
    }
    //设置id
    public String getId(){
        return id;
    }
    //返回停放车道
    public int getNo(){
        return no;
    }
    //设置停放车道
    public void setNo(int no){ this.no = no; }
    //返回进入时间
    public String getIn(){
        return time(in);
    }
    //返回离开时间
    public String getLeave(){
        return time(leave);
    }
    //格式化数据输出
    public String toString(){return id + "  "; }
    //返回费用
    public double getFee(){ return fee; }
}
