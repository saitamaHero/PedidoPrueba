package BaseDeDatos;

import android.content.Context;
import android.util.Log;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Diary;
import Models.Invoice;
import Sqlite.ClientController;
import Sqlite.Controller;
import Sqlite.InvoiceDiaryController;
import Sqlite.MySqliteOpenHelper;

public class DiaryUpdater extends SqlUpdater<Diary> {
    private static final String TAG = "DiaryUpdater";

    public DiaryUpdater(Context context, SqlConnection connection, Controller<Diary> controller) {
        super(context, connection, controller);
    }

    @Override
    public boolean onInsertCalled(Diary data) {
        Connection connection = getConnection().getSqlConnection();
        //--------------------------------------- 'N',  1, @ps, today,'1000','01',NULL,NULL, 30, ''
        String query = "{call PA_CCBDVISITA_MANTEN (?,?,?, ?, ?, ?, ?, ?, ?, ?)}";

        Log.d(TAG,"Inserting: " + data.toString());

        try {
            CallableStatement callableStatement = connection.prepareCall(query);

            callableStatement.setString(1,"N");
            callableStatement.setInt(2, 1);
            //Id, y Fecha de creacion
            callableStatement.registerOutParameter(   3, Types.VARCHAR);
            callableStatement.setTimestamp(4, new Timestamp(data.getDateEvent().getTime()));
            //Cliente y vendedor
            callableStatement.setString(   5, String.valueOf(data.getClientToVisit().getRemoteId()).trim());
            callableStatement.setString(   6, getVendor().getId());

            //hora que inicio y se termino la visita
            if(data.getStartTime() != null){
                callableStatement.setTimestamp(7, new Timestamp(data.getStartTime().getTime()));

            }else{
                callableStatement.setTimestamp(7, null);
            }

            if(data.getEndTime() != null){
                callableStatement.setTimestamp(8, new Timestamp(data.getEndTime().getTime()));
            }else{
                callableStatement.setTimestamp(8, null);
            }
            //Duracion y comentario
            callableStatement.setInt(      9, data.getDuration());
            callableStatement.setString(   10, data.getComment());

            callableStatement.executeUpdate();


            data.setStatus(ColumnsSqlite.ColumnStatus.STATUS_COMPLETE);
            data.setRemoteId(callableStatement.getString(3));

            callableStatement.close();

            InvoiceDiaryController invoiceDiaryController = new InvoiceDiaryController(MySqliteOpenHelper.getInstance(getContext()).getReadableDatabase());

            List<Invoice> invoices =  invoiceDiaryController.getAllById(data.getId());

            if(invoices != null && !invoices.isEmpty()){
                PreparedStatement statement = connection.prepareStatement("INSERT INTO CCBDVISFAC(COD_EMPR, VIS_COD, HE_FACTURA) VALUES (1, ?, ?)");

                for(Invoice invoice : invoices) {
                    statement.clearParameters();
                    statement.setString(1, String.valueOf(data.getRemoteId()));
                    statement.setString(2, String.valueOf(invoice.getRemoteId()));
                    statement.executeUpdate();
                }


            }

            connection.commit();

    } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
    }


    return true;
}

    @Override
    public boolean onUpdateCalled(Diary data) {
        Connection connection = getConnection().getSqlConnection();
        //--------------------------------------- 'N',1,@ps OUTPUT,@today,'1000','01',NULL,NULL,30,''
        String query = "{call PA_CCBDVISITA_MANTEN (?,?,?, ?, ?, ?, ?, ?, ?, ?)}";

        Log.d(TAG,"Updating: " + data.toString());
        try {
            CallableStatement callableStatement = connection.prepareCall(query);

            callableStatement.setString(1,"N");
            callableStatement.setInt(2, 1);
            //Id, y Fecha de creacion
            callableStatement.setString(   3, data.getRemoteId().toString().trim());
            callableStatement.setTimestamp(4, new Timestamp(data.getDateEvent().getTime()));
            //Cliente y vendedor
            callableStatement.setString(   5, String.valueOf(data.getClientToVisit().getRemoteId()).trim());
            callableStatement.setString(   6, getVendor().getId());

            //hora que inicio y se termino la visita
            if(data.getStartTime() != null){
                callableStatement.setTimestamp(7, new Timestamp(data.getStartTime().getTime()));
            }else{
                callableStatement.setTimestamp(7, null);
            }

            if(data.getEndTime() != null){
                callableStatement.setTimestamp(8, new Timestamp(data.getEndTime().getTime()));
            }else{
                callableStatement.setTimestamp(8, null);
            }
            //Duracion y comentario
            callableStatement.setInt(      9, data.getDuration());
            callableStatement.setString(   10, data.getComment());


             callableStatement.executeUpdate();

             data.setStatus(ColumnsSqlite.ColumnStatus.STATUS_COMPLETE);


             /*Insertar las facturas generadas durantes las visitas*/
            InvoiceDiaryController invoiceDiaryController = new InvoiceDiaryController(MySqliteOpenHelper.getInstance(getContext()).getReadableDatabase());

            List<Invoice> invoices =  invoiceDiaryController.getAllById(data.getId());

            if(invoices != null && !invoices.isEmpty()){
                PreparedStatement statement = connection.prepareStatement("INSERT INTO CCBDVISFAC(COD_EMPR, VIS_CODIGO, HE_FACTURA) VALUES (1, ?, ?)");

                for(Invoice invoice : invoices) {
                    statement.clearParameters();
                    statement.setString(1, String.valueOf(data.getRemoteId()));
                    statement.setString(2, String.valueOf(invoice.getRemoteId()));
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        return true;
    }

    @Override
    public Diary getItemFromResultSet(ResultSet rs) {
        Diary diary = new Diary();

        try {
            MySqliteOpenHelper mySqliteOpenHelper = MySqliteOpenHelper.getInstance(getContext());
            ClientController clientController = new ClientController(mySqliteOpenHelper.getReadableDatabase());

            //ID remoto de la visita
            diary.setRemoteId(rs.getString("VIS_COD").trim());
            diary.setStatus(ColumnsSqlite.ColumnStatus.STATUS_COMPLETE);

            //Cliente
            Client client = clientController.getById(Client._ID_REMOTE, rs.getString("CL_CODIGO").trim());

            if(client != null){
                diary.setClientToVisit(client);
            }else{
                return null;
            }

            //Duracion aprox. de la visita
            diary.setDuration(rs.getInt("VIS_DUR"));

            //Fecha en la que está planeada la visita
            diary.setDateEvent(rs.getTimestamp("VIS_FEC"));

            //Inicio y fin de la visita
            diary.setStartTime(rs.getTimestamp("VIS_INI"));
            diary.setEndTime(rs.getTimestamp("VIS_FIN"));

            diary.setComment(rs.getString("VIS_COM"));

            return diary;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT * FROM CCBDVISITA WHERE COD_EMPR = 1 AND VE_CODIGO = ? AND VIS_FEC >= CAST(GETDATE() AS DATE) AND VIS_INI IS NULL AND VIS_FIN IS NULL";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = getConnection().getSqlConnection().prepareStatement(query);
            preparedStatement.setString(1, getVendor().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }
}
