package BaseDeDatos;

import android.content.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Unit;
import Sqlite.Controller;

public class UnitUpdater extends SqlUpdater<Unit> {

    public UnitUpdater(Context context, SqlConnection connection, Controller<Unit> controller) {
        super(context, connection, controller);
    }

    @Override
    public boolean onInsertCalled(Unit data) {
        return false;
    }

    @Override
    public boolean onUpdateCalled(Unit data) {
        return false;
    }

    @Override
    public Unit getItemFromResultSet(ResultSet rs) {
        Unit unit = new Unit();

        try {
            unit.setId(rs.getString("UN_CODIGO").trim());
            unit.setName(rs.getString("UN_DESCRI").trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return unit;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT UN_CODIGO, UN_DESCRI FROM IVBDUNID";

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
