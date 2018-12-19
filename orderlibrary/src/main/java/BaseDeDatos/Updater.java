package BaseDeDatos;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class Updater<T> {
    public static final String LOG_TAG_FAIL = "UpdaterFail";
    public static final int SERVER_NOT_FOUND = 0x0;
    public static final int SERVER_TIMEOUT = 0x1;
    public static final int NULL_DATA = 0x2;
    public static final int UPDATED_DATA_SUCCESS = 0x3;

    //public static final int

    public Queue<T> data;
    public int capacity;

    protected abstract void onDataRequestUpdate(Queue<T> data);

    protected abstract void onDataUpdated(T data, int status);

    protected void onFail(int reason) {
        Log.e(LOG_TAG_FAIL, "reason: "+reason);
    }


    public void addData(T element){
        if(element == null) return;

        if(this.data == null){
            this.data = new LinkedList<>();
        }

        this.data.add(element);
    }

    public void addData(List<T> elements){
        if(elements == null){
            return;
        }

        for (T item : elements) {
            addData(item);
        }
    }

    public void apply(){
        if(this.data == null || this.data.isEmpty()){
           return;
        }

        onDataRequestUpdate(this.data);
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
