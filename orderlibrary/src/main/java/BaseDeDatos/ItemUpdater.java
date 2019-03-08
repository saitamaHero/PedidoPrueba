package BaseDeDatos;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Category;
import Models.Constantes;
import Models.Item;
import Models.Unit;
import Sqlite.Controller;
import Utils.FileUtils;

public class ItemUpdater extends SqlUpdater<Item> {

    public ItemUpdater(Context context, SqlConnection connection, Controller<Item> controller) {
        super(context, connection, controller);
    }

    @Override
    public boolean onInsertCalled(Item data) {
        return false;
    }

    @Override
    public boolean onUpdateCalled(Item data) {
        return false;
    }

    @Override
    public Item getItemFromResultSet(ResultSet rs) {
        Item item = new Item();

        try {
            item.setId(rs.getString("AR_CODIGO").trim());
            item.setName(rs.getString("AR_DESCRI").trim());
            item.setPrice(rs.getDouble("AR_PREDET"));
            item.setTaxRate(rs.getDouble("ITBIS"));
            item.setStock(rs.getDouble("CTD_INV"));

            item.setCategory(new Category(rs.getString("DE_CODIGO").trim(),""));
            item.setUnit(new Unit(rs.getString("AR_UNIDAD").trim(), ""));


            String base64str = rs.getString("AR_IMAGEN2");

            if(base64str != null){
                String name = FileUtils.addExtension(item.getId(), FileUtils.JPG_EXT);
                File route = FileUtils.createFileRoute(Constantes.MAIN_DIR, Constantes.ITEMS_PHOTOS);
                Bitmap bm = FileUtils.decodeBase64(base64str);

                FileUtils.savePhoto(bm, route , name, FileUtils.DEFAULT_QUALITY);

                item.setPhoto(Uri.fromFile(new File(route,name)));
            }else{
                item.setPhoto(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpg")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


        return item;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT TOP(80) AR_CODIGO, AR_DESCRI, AR_PREDET, 100.00 CTD_INV, de_codigo, AR_UNIDAD,\n"
                + "(CASE AR_ITBIS \n"
                + "   WHEN 'S' THEN (SELECT TOP 1 ITBIS FROM FABDPROC) \n"
                + "   WHEN 'T' THEN (SELECT TOP 1 ITBIS1 FROM FABDPROC) \n"
                + "   ELSE 0.0  \n"
                + "END) AS ITBIS, AR_IMAGEN2\n"
                + "FROM IVBDARTI --WHERE AR_IMAGEN2 != ''";

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
