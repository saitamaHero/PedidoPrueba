package BaseDeDatos;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class Updater<T> {
    public static final String LOG_TAG_FAIL = "UpdaterFail";
    public static final int SERVER_NOT_FOUND = 0x0;
    public static final int SERVER_TIMEOUT = 0x1;
    public static final int NULL_DATA = 0x2;
    public static final int UPDATED_DATA_SUCCESS = 0x3;
    public static final int ERROR = 0x4;

    //public static final int

    public Queue<T> data;
    public int capacity;
    public boolean blockQueque;

    public Updater() {
        blockQueque = false;
    }

    /**
     * Metodo que es llamado con los datos locales, este metodo verifica que operacion será ejecutada.
     * Si una actualizacion o crear los datos en el servidor remoto.
     * @param data cola con los datos a trabajar
     */
    protected abstract void onDataRequestUpdate(Queue<T> data);

    //protected abstract void onDataRequestRetrive();

    /**
     * Cuando un registro es actualizado remotamente.
     * @param data que es actualizada
     * @param status estado en el que la transaccion fue completada.
     */
    protected abstract void onDataUpdated(T data, int status);

    protected void fail(int reason){
        onFail(reason);
    }

    /**
     * En caso de alguna falla este metodo es llamado
     * @param reason razon por la cual falló
     */
    protected void onFail(int reason) {
        Log.e(LOG_TAG_FAIL, "reason: "+reason);
    }

    /**
     * Añade un elemento a la cola
     * @param element
     */
    public void addData(T element){
        if(element == null) return;

        if(this.data == null && !blockQueque){
            this.data = new LinkedList<>();
        }else if(!blockQueque){
            this.data.add(element);
        }
    }

    /**
     * Añade una lista de elementos a la cola. para ser procesados
     * @param elements que que seran procesados
     */
    public void addData(List<T> elements){
        if(elements == null){
            return;
        }

        if(!blockQueque){
            for (T item : elements) {
                addData(item);
            }
        }
    }

    /**
     * La llamada de este metodo implica que los datos estan listos para procesados
     */
    public void apply(){
        if(this.data == null || this.data.isEmpty()){
           return;
        }

        onDataRequestUpdate(this.data);
        blockQueque = true;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
