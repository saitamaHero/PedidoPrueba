package BaseDeDatos;

import android.content.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Zone;
import Sqlite.Controller;

public class ZoneUpdater extends SqlUpdater<Zone> {

    public ZoneUpdater(Context context, SqlConnection connection, Controller<Zone> controller) {
        super(context, connection, controller);
    }

    @Override
    public boolean onInsertCalled(Zone data) {
        return false;
    }

    @Override
    public boolean onUpdateCalled(Zone data) {
        return false;
    }

    @Override
    public Zone getItemFromResultSet(ResultSet rs) {
        Zone zone = new Zone();

        try {
            zone.setId((rs.getString("ZO_CODIGO").trim()));
            zone.setName((rs.getString("ZO_NOMBRE").trim()));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


        return zone;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT ZO_CODIGO, ZO_NOMBRE FROM CCBDZONA";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = getConnection().getSqlConnection().prepareStatement(query);
            //preparedStatement.setString(1, getVendor().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }
}
