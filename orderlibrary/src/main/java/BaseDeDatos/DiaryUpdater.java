package BaseDeDatos;

import android.content.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import Models.Client;
import Models.Diary;
import Sqlite.ClientController;
import Sqlite.Controller;
import Utils.DateUtils;

public class DiaryUpdater extends SqlUpdater<Diary> {

    public DiaryUpdater(Context context, SqlConnection connection, Controller<Diary> controller) {
        super(context, connection, controller);
    }

    @Override
    public boolean onInsertCalled(Diary data) {
        return false;
    }

    @Override
    public boolean onUpdateCalled(Diary data) {
        return false;
    }

    @Override
    public Diary getItemFromResultSet(ResultSet rs) {
        Diary diary = new Diary();
        //diary.setId(cursor.getLong(cursor.getColumnIndex(Diary._ID)));
        try {


            diary.setRemoteId(rs.getString("VIS_COD"));

            //Cliente
         // Client client = new Client();
          //client.setI
            //diary.setClientToVisit(new Client());

            //Duracion aprox. de la visita
            diary.setDuration(rs.getInt("T_ESTIMADO"));

            //Fecha en la que está planeada la visita
            diary.setDateEvent(rs.getTimestamp("VIS_FEC"));

            //Inicio y fin de la visita
            diary.setStartTime(rs.getTimestamp("VIS_INI"));
            diary.setEndTime(rs.getTimestamp("VIS_FIN"));

            diary.setComment(rs.getString("VIS_COM"));
        } catch (SQLException e) {
            e.printStackTrace();
        }




        return null;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT * FROM CCBDVISITA WHERE VE_CODIGO = ?";

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
