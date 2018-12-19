package BaseDeDatos;

import android.content.Context;

import java.sql.Connection;
import java.util.Queue;

import Models.Client;

public class ClientUpdater extends SqlUpdater<Client> {

    public ClientUpdater(Context context, SqlConnection connection) {
        super(context, connection);
    }

    @Override
    protected void onDataRequestUpdate(Queue<Client> data) {
        SqlConnection connection = getConnection();

        if(connection.isConnected()){
            Connection conn = connection.getSqlConnection();
            while(data.size() > 0){
                Client client = data.peek();

            }
        }else{
            onDataUpdated(null, SERVER_NOT_FOUND);
        }


    }

    @Override
    protected void onDataUpdated(Client data, int status) {

    }
}
