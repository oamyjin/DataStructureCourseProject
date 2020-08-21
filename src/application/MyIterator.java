package application;

public class MyIterator<E> {

    MyListItem<E> cursor;//现在操作对象
    MyList<E> myList;

    public MyIterator(MyList<E> myList){
        this.myList = myList;
        cursor = null;
    }
    //下一个节点
    public MyListItem<E> next(){
        if(cursor == null) {
            System.out.println("cursor is null");
            cursor = myList.getHead();
            System.out.println("cursorNext"+cursor.toString());
        }
        else {
            System.out.println("cursor"+cursor.toString());
            cursor = cursor.getNext();
            System.out.println("cursorNext"+cursor.toString());
        }
        return cursor;
    }
    //有没有下一个
    public boolean hasNext(){
        if(cursor == null)
            return (myList.getHead() != null);
        else
            return (cursor.getNext() != null);
    }
    //删除当前
    public void remove(){
        myList.remove(cursor.getItem());
        cursor = cursor.getPrevious();
    }
}
