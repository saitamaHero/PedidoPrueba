package BaseDeDatos;

import android.content.Context;
import android.content.Entity;

import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;

import java.lang.reflect.Parameter;
import java.security.Policy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Invoice;
import Models.Item;
import Models.NCF;
import Sqlite.ClientController;
import Sqlite.Controller;
import Sqlite.MySqliteOpenHelper;
import Utils.NumberUtils;

public class InvoiceUpdater extends SqlUpdater<Invoice> {

    public InvoiceUpdater(Context context, SqlConnection connection, Controller<Invoice> controller) {
        super(context, connection, controller);
        setHasMasterDetailRelationship(true);
    }

    @Override
    public boolean onInsertCalled(Invoice data) {
        String numCredito = "", numContado = "";
        String query;

        try {
            PreparedStatement preparedStatement;

            if(data.isCredit()){
                query = "UPDATE IVBDPROC SET FACTURA=FACTURA+1,CREDITO=CREDITO+1 WHERE COD_CODEMPR = 1 AND COD_SUCU = 1;";
            }else{
                query = "UPDATE IVBDPROC SET FACTURA=FACTURA+1,CONTADO=CONTADO+1 WHERE COD_CODEMPR = 1 AND COD_SUCU = 1;";
            }

            query = query.concat("SELECT FACTURA,CREDITO,CONTADO FROM IVBDPROC WHERE COD_CODEMPR = 1 AND COD_SUCU = 1;");

            preparedStatement = getConnection().getSqlConnection().prepareStatement(query);

            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()){
                int numeracion;

                numeracion = rs.getInt("FACTURA");
                data.setRemoteId(NumberUtils.generateSequence(10, numeracion));

                if(data.isCredit()){
                    numeracion = rs.getInt("CREDITO");
                    numCredito = NumberUtils.generateSequence(10, numeracion);
                }else{
                    numeracion = rs.getInt("CONTADO");
                    numContado = NumberUtils.generateSequence(10, numeracion);
                }

                query = "UPDATE IMBDNCF SET IM_SECUENI = IM_SECUENI + 1 WHERE COD_CODEMPR = 1 AND COD_SUCU = 1 AND IM_CODIGO= ?; SELECT IM_SECUENI FROM IMBDNCF WHERE COD_CODEMPR = 1 AND COD_SUCU = 1 AND IM_CODIGO = ?";
                preparedStatement = getConnection().getSqlConnection().prepareStatement(query);

                preparedStatement.setString(1, data.getClient().getNcf().getId());
                preparedStatement.setString(2, data.getClient().getNcf().getId());

                rs = preparedStatement.executeQuery();

                if(rs.next()){
                    NCF ncf = data.getClient().getNcf();
                    numeracion = rs.getInt(1);

                    String ncfSequence = String.format("%s%s",ncf.getType(),NumberUtils.generateSequence(8, numeracion));
                    data.setNcfSequence(ncfSequence);
                }

                query = "INSERT INTO IVBDHEPE\n"
                        + "(\n"
                        + "-- DATOS DE LA FACTURA\n"
                        + "COD_EMPR, COD_SUCU, HE_FACTURA, HE_FECHA, HE_TIPO,  HE_CREGEN,  HE_CONGEN,  HE_DIASV, HE_OBSERV, HE_MONTO,  HE_NETO,   AL_CODIGO,\n"
                        + "HE_COSTOT,  HE_ITBIS, HE_EXENTO,  HE_GRAVADO, HE_ORDEN,   HE_MODO,  HE_NUMCUE, HE_TIPTAR, HE_CODBAN, HE_DUENO,\n"
                        + "HE_NUMCHE,  HE_BENEF, HE_NETONCF, HE_BRUTNCF, HE_ITBINCF, HE_OGAS,\n"
                        + "\n"
                        + "--DATOS DEL CLIENTE\n"
                        + "CL_CODIGO, HE_NOMBRE, HE_NCF,    IM_CODIGO,  HE_RNC,    HE_VALDESC, HE_DESC,   HE_DIRE1, HE_DIRE2, HE_TELE1,\n"
                        + "HE_TIPDES, HE_MONREC, HE_ETARJE, HE_ECHEQUE, HE_BONIFI, HE_NUMDEV,  HE_VCREDI, cl_rncid,\n"
                        + "\n" + "-- DATOS DE QUIEN ATIENDE\n"
                        + "VE_CODIGO,HE_TURNO,HE_CAJA,HE_USUARIO, HE_TERMINAL,HE_HORA,HE_ITBIS1,\n"
                        + "CA_CODIGO,HE_GIFTCAR,HE_PUNTOS,HE_VCONSUMO,MO_CODIGO,HE_VMONEDA,HE_TMONEDA,he_aparta\n" + ") \n"
                        + "VALUES\n"
                        + "(\n"
                        + "-- DATOS DE LA FACTURA\n"
                        + "1,1, ?, CAST(GETDATE() AS DATE), ?, ?, ?, 0, ?, ?, ?, '01',\n"
                        + "?, ?,?,?, '', '', '', '', '', '',\n" + "'','', '', 0, 0, 0,\n"
                        + "\n"
                        + "--DATOS DEL CLIENTE\n"
                        + "?, ?, ?,   ?, ?, '', ?, '', '', '',\n"
                        + "0, ?, 0,0,0,'',0, 0,\n" + "\n"
                        + "-- DATOS DE QUIEN ATIENDE\n"
                        + "?, 1,'','', '','1900-01-01 00:00:00',0,'','',0,0,'RD',0, 0,0\n" + ");";

                preparedStatement = getConnection().getSqlConnection().prepareStatement(query);

                preparedStatement.setString(1, String.valueOf(data.getRemoteId())); //Numeracion de factura
                int tipo = 1 + data.getInvoiceType().ordinal();
                preparedStatement.setString(2, String.valueOf(tipo));//Tipo factura

                preparedStatement.setString(3, numCredito);
                preparedStatement.setString(4, numContado);
                preparedStatement.setString(5, data.getComment());
                preparedStatement.setDouble(6, data.getTotalFreeTaxes());
                preparedStatement.setDouble(7, data.getTotal());
                preparedStatement.setDouble(8, data.getTotalCost());
                preparedStatement.setDouble(9, data.getTotalTaxes());
                preparedStatement.setDouble(10,data.getTotal(Item.FREE_TAXES));
                preparedStatement.setDouble(11,data.getTotal(Item.INCLUDE_TAXES));

                Client client = data.getClient();
                preparedStatement.setString(12, client.getId());
                preparedStatement.setString(13, client.getName());
                preparedStatement.setString(14, data.getNcfSequence());
                preparedStatement.setString(15, client.getNcf().getId());
                preparedStatement.setString(16, client.getIdentityCard());
                preparedStatement.setDouble(17, data.getDiscount());
                preparedStatement.setDouble(18, data.getMoneyReceived());
                preparedStatement.setString(19, getVendor().getId());


                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0){

                    query = "-- DETAILS\n" +
                            "INSERT INTO IVBDDEPE \n" +
                            "(\n" + "-- DATOS DE LA FACTURA\n" +
                            "COD_EMPR, COD_SUCU,DE_FACTURA, DE_FECHA, DE_TIPO,DE_CREGEN,DE_CONGEN,DE_PEDIDO,CL_CODIGO,VE_CODIGO,\n" +
                            "\n" +
                            "-- DATOS DEL ARTICULO\n" +
                            "AR_CODIGO,AR_CODIGO2,DE_DESCRI,DE_CANTID,DE_FRACION,DE_ITBIS,DE_COSTO,DE_PRECIO,\n" +
                            "DE_PRECIO2,AL_CODIGO,DE_UNIDAD,DE_EXENTO,DE_GRAVADO,\n" + "\n" +
                            "--CLIENTE\n" +
                            "DE_NOMBRE,DE_NCF,IM_CODIGO,DE_OFERTA,DE_CAJA, DE_TURNO,DE_USUARIO,DE_PRENCF,\n" +
                            "DE_TERMINAL,DE_PUNTOS,DE_DESC,AR_ID\n" + ")\n" +
                            "VALUES\n" +
                            "(\n" +
                            "-- DATOS DE LA FACTURA\n" +
                            " 1, 1,  ?, CAST(GETDATE() AS DATE), ?, ?, ?,'', ?,\n" +
                            "-- DATOS DEL ARTICULO\n" +
                            "?,'',?, ?,'',?,?,?,0,'01',?,\n" +
                            "?, ?,\n" +
                            "--CLIENTE\n" +
                            "?, ?, ?, ?, 0,'', '','', 0, 0, 0, 0, 0\n" + "); ";


                    preparedStatement =  getConnection().getSqlConnection().prepareStatement(query);

                    for (Item item : data.getItems() ) {
                        preparedStatement.clearParameters();

                        boolean isFreeTaxes = item.isFreeTaxes();
                        preparedStatement.setString(1, String.valueOf(data.getRemoteId()));
                        preparedStatement.setString(2, String.valueOf(tipo));
                        preparedStatement.setString(3, numCredito);
                        preparedStatement.setString(4, numContado);
                        preparedStatement.setString(5, getVendor().getId());

                        preparedStatement.setString(6, item.getId());
                        preparedStatement.setString(7, item.getName());
                        preparedStatement.setDouble(8, item.getQuantity());
                        preparedStatement.setDouble(9, item.getTaxes());
                        preparedStatement.setDouble(10, item.getCost());
                        preparedStatement.setDouble(10, item.getCost());
                        preparedStatement.setDouble(11, item.getPrice());
                        preparedStatement.setString(12, item.getUnit().getId());
                        preparedStatement.setDouble(13, isFreeTaxes ? item.getPrice() : 0);
                        preparedStatement.setDouble(14, isFreeTaxes ? 0 : item.getPrice());

                        preparedStatement.setString(15, client.getId());
                        preparedStatement.setString(16, client.getName());
                        preparedStatement.setString(17, data.getNcfSequence());
                        preparedStatement.setString(18, client.getNcf().getId());

                        rowsAffected =  preparedStatement.executeUpdate();

                        if(rowsAffected <= 0){
                            break;
                        }

                    }

                    query = "INSERT INTO IVBDHIS \n" +
                            "(\n" +
                            "COD_EMPR, HI_DOCUM,HI_TIPO,AR_CODIGO,HI_CANTID,HI_FECHA,HI_COSTO,AL_CODIGO,\n" +
                            "VE_CODIGO,SU_CODIGO,CL_CODIGO,HI_PRECIO,HI_TIPVEN,HI_FECVEN,\n" +
                            "HI_REFER,HI_TIPO2,HI_TIPOI,HI_CONDU,HI_FACTURA,HI_NCF,AR_ID)\n" +
                            "VALUES \n" +
                            "(\n" +
                            "1, ?,'3',?,?,CAST(GETDATE() AS DATE), ?, '01',\n" +
                            "?,' ',?,?,' ', CAST(GETDATE() AS DATE),\n" +
                            "' ',' ',' ',' ',?,?, 0);";

                    preparedStatement =  getConnection().getSqlConnection().prepareStatement(query);

                    for (Item item : data.getItems() ) {
                        preparedStatement.clearParameters();
                        preparedStatement.setString(1, String.valueOf(data.getRemoteId()));
                        preparedStatement.setString(2, item.getId());
                        preparedStatement.setDouble(3, item.getQuantity());
                        preparedStatement.setDouble(4, item.getCost());
                        preparedStatement.setString(5, getVendor().getId());
                        preparedStatement.setString(6, client.getId());
                        preparedStatement.setString(7, String.valueOf(data.getRemoteId()));
                        preparedStatement.setString(8, data.getNcfSequence());
                        preparedStatement.setDouble(9, item.getPrice());

                        rowsAffected =  preparedStatement.executeUpdate();

                        if(rowsAffected <= 0){
                            break;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            return false;
        }


        return true;
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