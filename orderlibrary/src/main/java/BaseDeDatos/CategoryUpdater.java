package BaseDeDatos;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Category;
import Models.Category;
import Models.Unit;
import Sqlite.Controller;

public class CategoryUpdater extends SqlUpdater<Category> {

    public CategoryUpdater(Context context, SqlConnection connection, Controller<Category> controller) {
        super(context, connection, controller);
    }

    @Override
    public boolean onInsertCalled(Category data) {
        return false;
    }

    @Override
    public boolean onUpdateCalled(Category data) {
        return false;
    }

    @Override
    public Category getItemFromResultSet(ResultSet rs) {
        Category category = new Category();

        try {
            category.setId((rs.getString("DE_CODIGO").trim()));
            category.setName((rs.getString("AR_DESCRI").trim()));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


        return category;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT DE_CODIGO, AR_DESCRI FROM IVBDDEPT";

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
