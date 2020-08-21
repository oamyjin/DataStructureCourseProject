package application;


/**
 * MyList链表的节点
 * @param <E>
 */
public class MyListItem<E> {
    private E item;
    private MyListItem<E> next;
    private MyListItem<E> previous;

    public MyListItem(E item){
        this.item = item;
        next = null;
        previous = null;
    }

    //返回元素数据，获取结点具体信息
    public E getItem(){
        return item;
    }
    //返回下一个节点
    public MyListItem<E> getNext(){
        return next;
    }
    //返回前一个结点
    public MyListItem<E> getPrevious(){
        System.out.println("getPrevious--" + previous.toString());
        return previous;
    }
    //格式化输出
    public String toString(){ return item.toString();};
    //设置下一个节点
    public void setNext(MyListItem<E> next){
        this.next = next;
    }
    //设置上一个节点
    public void setPrevious(MyListItem<E> previous){
        this.previous = previous;
    }
}
