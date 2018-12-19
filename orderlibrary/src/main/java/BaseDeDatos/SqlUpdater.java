package BaseDeDatos;

import android.content.Context;
import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SqlUpdater<T> extends Updater<T> {
    private Context context;
    private SqlConnection connection;

    public SqlUpdater(Context context, SqlConnection connection) {
        this.context = context;
        this.connection = connection;
    }

    public SqlConnection getConnection() {
        return connection;
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
}
