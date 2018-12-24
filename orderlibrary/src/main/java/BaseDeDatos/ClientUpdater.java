package BaseDeDatos;

import android.content.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            try {
                conn.setAutoCommit(false);

                int rowsAffected;

                while (data.size() > 0) {
                    Client client = data.peek();

                    rowsAffected = 0;

                    if (client.isPending()) {
                        if (client.getRemoteId() == null || client.getRemoteId().equals("")) {
                            //Insertar el dato
                           //exec(conn);

                            //rowsAffected = SqlConnection.comando(conn, )
                        }else if(client.getRemoteId() != null && !client.getRemoteId().equals("")){
                            //Actualizar el dato
                        }
                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();

            }

        }else{
            onDataUpdated(null, SERVER_NOT_FOUND);
        }


    }
/*
    private boolean exec(Connection conn)  {
        int clientCode = -1;

        try {
            SqlConnection.comando(conn,"UPDATE CCBDPROC SET CLIENTE = CLIENTE + 1\n");
            ResultSet rs = SqlConnection.consulta(conn, "SELECT CLIENTE FROM CCBDPROC\n");

            if(rs.next()){
                clientCode = rs.getInt(1);
            }else{
                return false;
            }


        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    public PreparedStatement createQueryInsert(Connection connection) throws SQLException {
        String query = "INSERT INTO CCBDCLIE" +
                "(cl_codigo,cl_nombre,cl_direc1,cl_telef1," +
                "cl_rnc,cl_email,cl_tipornc,"     +
                "cl_estado,zo_codigo,ca_codigo,ve_codigo)";


        return connection.prepareStatement(query);
    }
*/
    @Override
    protected void onDataUpdated(Client data, int status) {
        switch (status){
            case UPDATED_DATA_SUCCESS:

                break;
        }
    }
}
