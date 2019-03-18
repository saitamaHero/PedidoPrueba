package BaseDeDatos;

import android.content.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Invoice;
import Models.Item;
import Sqlite.ClientController;
import Sqlite.Controller;
import Sqlite.MySqliteOpenHelper;

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

        try {
            MySqliteOpenHelper mySqliteOpenHelper = MySqliteOpenHelper.getInstance(getContext());
            ClientController clientController = new ClientController(mySqliteOpenHelper.getReadableDatabase());

            invoice.setId(rs.getString("HE_FACTURA").trim());
            invoice.setDate(rs.getDate("HE_FECHA"));

            Client client = clientController.getById(Client._ID_REMOTE, rs.getString("CL_CODIGO").trim());

            if(client != null) {
                invoice.setClient(client);
            }

            invoice.setComment("");
            int type = rs.getString("HE_TIPO").equals("1") ? 0 : 1;
            invoice.setInvoiceType(Invoice.InvoicePayment.values()[type]);

            invoice.setStatus(ColumnsSqlite.ColumnStatus.STATUS_COMPLETE);
            invoice.setRemoteId(rs.getString("HE_FACTURA").trim());

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return invoice;
    }

    @Override
    public Invoice getDetailsFromResultSet(Invoice data, ResultSet rs) {
        List<Item> itemList = new ArrayList<>();

        try {
            while (rs.next()) {
                Item item = new Item();
                item.setId(         rs.getString("AR_CODIGO").trim());
                item.setName(       rs.getString("DE_DESCRI").trim());
                item.setPrice(      rs.getDouble("DE_PRECIO"));
                item.setTaxRate(    rs.getDouble("ITBIS"));
                item.setQuantity(   rs.getDouble("DE_CANTID"));

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
        String query = "SELECT * FROM IVBDHEPE WHERE COD_EMPR = 1 AND VE_CODIGO = ? AND HE_FACTURA = '0000201901'";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = getConnection().getSqlConnection().prepareStatement(query);
            preparedStatement.setString(1, getVendor().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    public PreparedStatement getQueryDetailsToRetriveData(Invoice id) {
        String query = "SELECT \n"
                + "AR_CODIGO, DE_PRECIO, DE_CANTID,  DE_DESCRI,\n "
                + "ROUND((DE_ITBIS / (DE_PRECIO - DE_ITBIS)) * 100.00,0) ITBIS\n "
                + "FROM IVBDDEPE WHERE COD_EMPR = 1 AND VE_CODIGO = ? AND DE_FACTURA = '0000201901'";

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
