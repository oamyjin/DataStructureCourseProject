package application;

/**
 *
 * 自定义链表
 *
 */
public class MyList<E> {
    private int len;//有多少元素
    private MyListItem<E> head;//头结点
    private MyIterator<E> it;//该链表的迭代器

    //构造方法
    public MyList(){
        len = 0;
        head = null;
        it = new MyIterator<E>(this);
    }
    //是否为空
    public boolean isEmpty() {
        return len == 0;
    }
    //返回头元素的值
    public E head(){
        return getHead().getItem();
    }
    //返回Head，第一个元素
    public MyListItem<E> getHead(){
        if(isEmpty()){
            System.out.println("链表为空");
        }
        return head;
    }
    //获取第i个元素节点
    public MyListItem<E> getMyListItem(int i) {
        if(isEmpty()){
            return null;
        }
        else{
            MyListItem<E> r = head;
            for (int k = 0; k < i-1; k++)
                r = r.getNext();
            return r;
        }
    }
    //返回特定元素所在节点
    public int getIndex(MyListItem<E> item){
        if(isEmpty()) {
            return 0;
        }
        else{
            int i = 0;
            while(it.hasNext()){
                i++;
                if(it.next().getItem().toString().equals(item.toString())){
                    break;
                }
            }
            if(len == i)//没有该元素
                return len+1;
            else{
                return i;
            }
        }
    }
    //增加元素
    public void add(E item){
        MyListItem<E> newItem = new MyListItem<E>(item);
        System.out.println("addList--"+item.toString());
        if(isEmpty()){
            head = newItem;
        }
        else {
            getMyListItem(len).setNext(newItem);//加在最后一个
            newItem.setPrevious(getMyListItem(len));
        }
        len++;
    }
    //删除元素
    public void remove(E item){
        it = new MyIterator<E>(this);
        int i = 0;
        System.out.println("iiittteeemmm"+item.toString());
        System.out.println("len" + len);
        while(it.hasNext()){
            i++;
            System.out.println("i "+i);
            MyListItem<E> itemE = it.next();
            if(itemE.getItem().toString().equals(item.toString())){//在链表中找该元素
                if(len == 1){//只有一个
                    head = null;
                    len--;
                }
                else{
                    if(i == 1){//第一个
                        System.out.println("删除第一个");
                        itemE.setPrevious(null);
                        head = itemE.getNext();
                        len--;
                    }
                    else if(i == len) {//最后一个
                        itemE.getPrevious().setNext(null);
                        System.out.println("删除最后一个");
                        len--;
                    }
                    else{
                        System.out.println("删除第"+i+"个");
                        itemE.getPrevious().setNext(itemE.getNext());
                        itemE.getNext().setPrevious(itemE.getPrevious());
                        len--;
                    }
                }
                break;
            }
        }
        if(len == i){//不在链表中
            System.out.println("不存在删除对象！");
        }
    }
    //返回长度
    public int getLen(){ return len;}
    public String toString(){
        String r = "MyList:";
        while(it.hasNext()){
            MyListItem<E> item = it.next();
            System.out.println("toString"+item.toString());
            r = r + "/n" + item.toString();
        }
        return r;
    }
}


