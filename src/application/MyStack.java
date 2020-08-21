package application;

public class MyStack<E> {

    private int top;
    private int size;
    private E[] list;
    private int capacity = 4;//停车场4个车位

    public MyStack(){
        top = -1;
        size = 0;
        list = (E[]) new Object[capacity];
    }

    //返回头元素
    public E peek(){
        return list[top];
    }
    //删除并返回头元素
    public E pop(){
        if(isEmpty()){
            System.out.println("栈为空！");
            return null;
        }
        else {
            E r = list[top];
            top--;
            size--;
            return r;
        }
    }
    //添加元素
    public void push(E newItem){
        if(size == capacity)
            System.out.println("栈满！添加元素失败");
        else{
            size++;
            top++;
            list[top] = newItem;
            System.out.println("添加成功！第"+ top + "个");
        }
    }
    //是否为空
    public boolean isEmpty(){
        return size == 0;
    }
    //满
    public boolean isFull(){
        return size == capacity;
    }
    //返回大小
    public int size(){
        return size;
    }
    //返回最新位置
    public int getTop(){
        return top;
    }
//    //判断是否存在某一元素
//    public boolean exist(E item) {
//        boolean flag = false;
//        int i = top;
//        int n = 0;
//        while(n != size){
//            if(list[i] == item) {
//                flag = true;
//                break;
//            }
//            n++;
//            i++;
//            if(i == capacity)
//                i = 0;
//        }
//        return flag;
//    }
}
