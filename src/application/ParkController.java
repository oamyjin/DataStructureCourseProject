package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class ParkController {

    @FXML
    private TextField pCN;
    @FXML
    private TextField lCN;
    @FXML
    private TextField qCN;
    @FXML
    private Label parkDone;
    @FXML
    private Label car1;
    @FXML
    private Label car2;
    @FXML
    private Label car3;
    @FXML
    private Label car4;
    @FXML
    private ImageView pic1;
    @FXML
    private ImageView pic2;
    @FXML
    private ImageView pic3;
    @FXML
    private ImageView pic4;
    @FXML
    private Label waiting;
    @FXML
    private Label carInfo;


    private MyStack<Car> parkingCar;
    private MyQueue<Car> waitingCar;
    String waitingInfo = "";
    MyList<Car> cars = new MyList<>();


    //初始化页面
    @FXML
    private void initialize() {
        pic1.setVisible(false);
        pic2.setVisible(false);
        pic3.setVisible(false);
        pic4.setVisible(false);
        parkingCar = new MyStack<>();
        waitingCar = new MyQueue<>();
    }
    //返回游客页面
    public void back(){
        try {
            Stage stage = Main.getStage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../ui/tourists.fxml"))));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //检索车辆信息
    public void queryC(){
        String r;
        MyIterator<Car> it = new MyIterator<>(cars);
        while(it.hasNext()) {
            Car itemC = it.next().getItem();
            if (itemC.getId().equals(qCN.getText())) {
                if (itemC.getNo() == -1)
                    r = "该车在等待";
                else if (itemC.getNo() == 5)
                    r = "该车已经离开" + itemC.getNo() + "号车道";
                else
                    r = "该车停在" + itemC.getNo() + "号车道";
                if (itemC.getNo() == 5) {
                    carInfo.setText("车牌号：" + itemC.getId() + '\n' + '\n' + r + "  " + '\n' + '\n' + "开始时间：" + itemC.getIn() + '\n' + '\n' + "离开时间：" + itemC.getLeave()+ '\n' + '\n' + "收取费用：" + itemC.getFee());
                } else
                    carInfo.setText("车牌号：" + itemC.getId() + '\n' + '\n' + r + "  " + '\n' + '\n' + "开始时间：" + itemC.getIn());
                qCN.setText("");
                return;
            }
        }
        carInfo.setText("停车场无该车辆信息！");
        qCN.setText("");
    }
    //停车
    public void parkSure(){
        parkDone.setText("");
        waitingInfo = "";
        //判断是否存在
        int flag = 0;
        MyIterator<Car> it = new MyIterator<>(cars);
        while(it.hasNext()){
            Car temp = it.next().getItem();
            if(temp.getId().equals(pCN.getText())){
                flag = 1;
            }
        }
        if(flag == 0) {
            Car car = new Car(pCN.getText());
            cars.add(car);
            if (parkingCar.isFull()) {
                car.waiting();
                waitingCar.add(car);
                car.waiting();
                parkDone.setText("车库已满，请耐心等待！");
                waiting.setText("");
                waiting.setText(waitingCar.toString());
            } else {
                parkingCar.push(car);
                int n = parkingCar.getTop() + 1;
                car.park(n);
                waitingInfo = waitingInfo + "车辆已停入" + car.getNo() + "号车道！" + '\n';
                parkDone.setText(waitingInfo);
                showCar(parkingCar.getTop() + 1, pCN.getText());
            }
            pCN.setText("");
        }
        else
            parkDone.setText("已存在该车");
    }
    //离开
    public void leaveSure(){
       parkDone.setText("");
       waitingInfo = "";
       Car c;
       Car leaveC = null;
       MyStack<Car> tempCar = new MyStack<>();
       //该车车辆离开停车场
       while(!parkingCar.isEmpty()){//找到要离开的车辆
            leaveC = parkingCar.pop();
            hideCar(leaveC.getNo() + 1);
            if(leaveC.getId().equals(lCN.getText())){
                leaveC.leave();//该车辆离开
                waitingInfo = waitingInfo + "车辆" + leaveC.getId() + "离开停车场" + '\n';
                break;
            }
            tempCar.push(leaveC);//之前的车辆先暂时离开
            waitingInfo = waitingInfo + "车辆" + leaveC.getId() + "暂时离开" + '\n';
        }
        //暂时离开的车辆开回停车场
       while(!tempCar.isEmpty()){
           c = tempCar.pop();
           parkingCar.push(c);
           c.setNo(c.getNo()-1);
           waitingInfo = waitingInfo + "车辆" + c.getId() + "回到停车场" + c.getNo() + "车位" + '\n';
           showCar(c.getNo(), c.getId());
       }
       //等待最久的车进停车场
       c = waitingCar.poll();
       parkingCar.push(c);
       c.park(parkingCar.getTop() + 1);
        waitingInfo = waitingInfo + "车辆"+  c.getId() +"已停入" + c.getNo() + "号车道！" + '\n';
        parkDone.setText(waitingInfo);
        showCar(parkingCar.getTop() + 1, c.getId());
       //显示信息在页面上
       waiting.setText(waitingCar.toString());
       waitingInfo = waitingInfo + '\n' + "本次收费" + leaveC.getFee() + "元";
       parkDone.setText(waitingInfo);
    }
    //停车场信息显示在界面上
    public void showCar(int no, String id) {
        switch (no) {
            case 1:
                car1.setText(id);
                pic1.setVisible(true);
                break;
            case 2:
                car2.setText(id);
                pic2.setVisible(true);
                break;
            case 3:
                car3.setText(id);
                pic3.setVisible(true);
                break;
            case 4:
                car4.setText(id);
                pic4.setVisible(true);
                break;
        }
        lCN.setText("");
    }
    //停车场信息改变
    public void hideCar(int no){
        switch (no) {
            case 1:
                car1.setText("");
                pic1.setVisible(false);
                break;
            case 2:
                car2.setText("");
                pic2.setVisible(false);
                break;
            case 3:
                car3.setText("");
                pic3.setVisible(false);
                break;
            case 4:
                car4.setText("");
                pic4.setVisible(false);
                break;
            }
    }

}
