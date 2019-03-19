package BaseDeDatos;

import android.content.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.NCF;
import Sqlite.Controller;

public class NCFUpdater extends SqlUpdater<NCF> {

    public NCFUpdater(Context context, SqlConnection connection, Controller<NCF> controller) {
        super(context, connection, controller);
    }

    @Override
    public boolean onInsertCalled(NCF data) {
        return false;
    }

    @Override
    public boolean onUpdateCalled(NCF data) {
        return false;
    }

    @Override
    public NCF getItemFromResultSet(ResultSet rs) {
        NCF ncf = new NCF();

        try {
            ncf.setId((rs.getString("IM_CODIGO").trim()));
            ncf.setName((rs.getString("IM_DESCRI").trim()));
            ncf.setType(rs.getString("IM_TIPONCF"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return ncf;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT IM_CODIGO, IM_DESCRI, IM_TIPONCF FROM IMBDNCF WHERE IM_CODIGO IN ('01','02','14','15')";

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
