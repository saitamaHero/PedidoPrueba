package BaseDeDatos;

import android.content.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.Invoice;
import Models.Item;
import Sqlite.Controller;

public class InvoiceUpdater extends SqlUpdater<Invoice> {

    public InvoiceUpdater(Context context, SqlConnection connection, Controller<Invoice> controller) {
        super(context, connection, controller);
        setHasMasterDetailRelationship(true);
    }

    @Override
    public boolean onInsertCalled(Invoice data) {
        return false;
    }

    @Override
    public boolean onUpdateCalled(Invoice data) {
        return false;
    }

    @Override
    public Invoice getItemFromResultSet(ResultSet rs) {
        Invoice invoice = new Invoice();

        /*try {
            //unit.setId(rs.getString("UN_CODIGO").trim());
            //unit.setName(rs.getString("UN_DESCRI").trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }*/

        return invoice;
    }

    @Override
    public Invoice getDetailsFromResultSet(Invoice data, ResultSet rs) {
        List<Item> itemList = new ArrayList<>();

        try {


            while (rs.next()) {
                Item item = new Item();
                item.setId(     rs.getString("AR_CODIGO").trim());
                item.setName(   rs.getString("AR_DESCRI").trim());
                item.setPrice(  rs.getDouble("AR_PREDET"));
                item.setTaxRate(rs.getDouble("ITBIS"));
                //item.setStock(rs.getDouble("CTD_INV"));
                //item.setCost(   rs.getDouble("AR_ULTCOS"));

                //item.setCategory(new Category(rs.getString("DE_CODIGO").trim(), ""));
                //item.setUnit(new Unit(rs.getString("AR_UNIDAD").trim(), ""));
                itemList.add(item);
            }
        } catch(SQLException e){
            e.printStackTrace();
            return null;
        }

        data.setItems(itemList);

        return data;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT * FROM IVBDHEPE";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = getConnection().getSqlConnection().prepareStatement(query);
            //preparedStatement.setString(1, getVendor().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    public PreparedStatement getQueryDetailsToRetriveData(Invoice id) {



        return super.getQueryDetailsToRetriveData(id);
    }


}
