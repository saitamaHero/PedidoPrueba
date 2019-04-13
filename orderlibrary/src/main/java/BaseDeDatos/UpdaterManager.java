package BaseDeDatos;

import java.util.ArrayList;
import java.util.List;

public class UpdaterManager {
    private SqlConnection connection;
    private List<SqlUpdater> updaters;


    public UpdaterManager(SqlConnection connection) {
        this.connection = connection;
        this.updaters =  new ArrayList<>();
    }

    public UpdaterManager(SqlConnection connection, List<SqlUpdater> updaters) {
        this.connection = connection;
        this.updaters = updaters;
    }

    public void addUpdater(SqlUpdater updater){
        this.updaters.add(updater);
    }

    public void execute(){

    }

}
