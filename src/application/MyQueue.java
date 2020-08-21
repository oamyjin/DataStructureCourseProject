package application;

public class MyQueue<E> {


    private int size = 0;
    private MyList<E> list;
    private E head;

    public MyQueue(){
        size = 0;
        head = null;
        list = new MyList<>();
    }

    //增加元素
    public void add(E item){
        if(head == null)
            head = item;
        list.add(item);
        size++;

    }
    //删除并返回元素
    public E poll(){
        if(isEmpty())
            return null;
        else{
            E r = head;
            if(size == 1) {
                head = null;
            }
            else
                head = list.getHead().getNext().getItem();
            list.remove(r);
            size--;
            return r;
        }

    }
    //是否为空
    public boolean isEmpty(){
        return head == null;
    }
    //返回大小
    public int size(){
        return size;
    }
    public String toString(){
        String r = "";
        MyIterator<E> it = new MyIterator<>(list);
        while(it.hasNext()){
            MyListItem<E> item = it.next();
            r = r + item.toString();
        }
        return r;
    }

}
