package BaseDeDatos;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Constantes;
import Models.Vendor;
import Sqlite.Controller;

import static android.content.Context.MODE_PRIVATE;

public abstract class SqlUpdater<T> extends Updater<T> {
    public static final int NO_ID = -1;
    private Context context;
    private SqlConnection connection;
    private Controller<T> controller;

    public SqlUpdater(Context context, SqlConnection connection, Controller<T> controller) {
        this.context = context;
        this.connection = connection;
        this.controller = controller;
    }

    public SqlConnection getConnection() {
        return connection;
    }

    @Override
    protected void onDataRequestUpdate(Queue<T> data) {
        SqlConnection connection = getConnection();

        connection.connect();

        if(connection.isConnected()){
            Connection conn = connection.getSqlConnection();
            try {
                conn.setAutoCommit(false);

                while (data.size() > 0) {
                    T itemPeek = data.poll();

                    try {
                        ColumnsSqlite.ColumnsRemote remoteData = (ColumnsSqlite.ColumnsRemote) itemPeek;

                        if (remoteData.isPending()) {
                            if (remoteData.getRemoteId() == null || remoteData.getRemoteId().equals("null")) {
                               if(doInsert(itemPeek)){
                                   onDataUpdated(itemPeek, Updater.UPDATED_DATA_SUCCESS);
                               }else{
                                   fail(SERVER_NOT_FOUND);
                                   break;
                               }
                            }else if(remoteData.getRemoteId() != null && !remoteData.getRemoteId().equals("")){
                                if(doUpdate(itemPeek)){
                                    onDataUpdated(itemPeek, Updater.UPDATED_DATA_SUCCESS);
                                }else{
                                    fail(SERVER_NOT_FOUND);
                                    break;
                                }
                            }
                        }
                    }catch (ClassCastException e){
                        e.printStackTrace();
                        break;
                    }


                }
            }catch (SQLException e) {
               fail(Updater.ERROR);

            }

        }else{
            fail(Updater.SERVER_NOT_FOUND);
        }


        closeSqlConnection();

    }

    @Override
    protected void onDataUpdated(T data, int status) {
        switch (status){
            case UPDATED_DATA_SUCCESS:
                if(controller != null){
                    if(controller.update(data)){
                        Log.d("RemoteData", "Data updated successful!");
                    }
                }
                break;
        }
    }



    @Override
    protected void onFail(int reason) {
        super.onFail(reason);
        closeSqlConnection();
    }

    public void closeSqlConnection(){
        if(this.connection ==  null) return;

        Connection connection = this.connection.getSqlConnection();

        try {
            if(connection != null){
                if(!connection.isClosed()){
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG_FAIL, e.getMessage(), e.getCause());
        }
    }

    public Vendor getVendor(){
        SharedPreferences preferences = context.getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);

        Vendor vendor = new Vendor();
        vendor.setId(preferences.getString(Constantes.VENDOR_CODE,""));
        vendor.setName(preferences.getString(Constantes.VENDOR_NAME, ""));

        return vendor;
    }

    public boolean doInsert(T data){
        if(data == null) return false;


       return onInsertCalled(data);
    }

    public boolean doUpdate(T data){
        if(data == null) return false;

        return onUpdateCalled(data);
    }

    public abstract boolean onInsertCalled(T data);
    public abstract boolean onUpdateCalled(T data);

    public int generateId(){
        return NO_ID;
    }

}
