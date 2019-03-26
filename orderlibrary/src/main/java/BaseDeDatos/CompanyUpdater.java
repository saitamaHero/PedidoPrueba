package BaseDeDatos;

import android.content.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Company;
import Sqlite.Controller;

public class CompanyUpdater extends SqlUpdater<Company> {

    public CompanyUpdater(Context context, SqlConnection connection, Controller<Company> controller) {
        super(context, connection, controller);
    }

    @Override
    public boolean onInsertCalled(Company data) {
        return false;
    }

    @Override
    public boolean onUpdateCalled(Company data) {
        return false;
    }

    @Override
    public Company getItemFromResultSet(ResultSet rs) {
        Company company = new Company();

        try {
            company.setId("");
            company.setName(rs.getString("NAME").trim());
            company.setAddress(rs.getString("ADDRESS").trim());
            company.setContactInfo(rs.getString("CONTACT_INFO").trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return company;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT NOMBRE AS NAME, direc1 AS FULL_NAME, direc2 AS CONTACT_INFO, telef1 AS ADDRESS FROM CONTAEMP WHERE COD_EMPR = 1";

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
