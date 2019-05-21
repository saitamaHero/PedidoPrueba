package BaseDeDatos;

import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdaterManager {
    private SqlConnection connection;
    private List<SqlUpdater> updaters;
    private SqlUpdater.OnDataUpdateListener updateListener;
    private  SqlUpdater.OnErrorListener errorListener;

    public UpdaterManager(SqlConnection connection) {
        this.connection = connection;

        if(this.connection == null){
            throw new NullPointerException("SqlConnection can't be null");
        }

        this.updaters =  new ArrayList<>();
    }

    public void setErrorListener(SqlUpdater.OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setUpdateListener(SqlUpdater.OnDataUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void addUpdater(SqlUpdater updater){
        this.updaters.add(updater);
    }

    public void execute(){
        if(!this.connection.isConnected()){
            this.connection.connect();
        }

        if(this.connection.isConnected()) {
            for (SqlUpdater updater : this.updaters) {
                updater.setConnection(this.connection);
                updater.setOnErrorListener(this.errorListener);
                updater.setOnDataUpdateListener(this.updateListener);

                Log.d("UpdaterMangaer", "Actualizador: " + updater.toString());

                if(updater.needsUpdate()){
                    Log.d("UpdaterMangaer", "Necesita actualizacion " + updater.toString());
                    updater.apply();
                }

                updater.retriveData();
            }
        }


        if(this.connection.isConnected()) {
            try {
                this.connection.getSqlConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
