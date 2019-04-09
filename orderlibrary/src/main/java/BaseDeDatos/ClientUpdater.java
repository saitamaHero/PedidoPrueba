package BaseDeDatos;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Constantes;
import Models.NCF;
import Models.Zone;
import Sqlite.Controller;
import Utils.FileUtils;

public class ClientUpdater extends SqlUpdater<Client> {

    public ClientUpdater(Context context, SqlConnection connection, Controller controller) {
        super(context, connection, controller);
    }

    @Override
    public int generateId() {
        int id;
        try {
            SqlConnection.comando(getConnection().getSqlConnection(),"UPDATE CCBDPROC SET CLIENTE = CLIENTE + 1\n");
            ResultSet rs = SqlConnection.consulta(getConnection().getSqlConnection(), "SELECT CLIENTE FROM CCBDPROC\n");

            if(rs.next()){
                id = rs.getInt(1);
            }else{
                return super.generateId();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return super.generateId();
        }

        return id;
    }



    @Override
    public Client getItemFromResultSet(ResultSet rs) {
        Client client = new Client();

        try {
            //Codigo de la base de datos
            client.setRemoteId(rs.getString("CL_CODIGO").trim());
            client.setStatus(ColumnsSqlite.ColumnStatus.STATUS_COMPLETE);

            client.setName(rs.getString("CL_NOMBRE").trim());

            //Credito
            client.setCreditStatus(rs.getString("CL_ESTADO").charAt(0));
            client.setCreditLimit(rs.getDouble("CL_LIMCRE"));
            //RNC o cedula
            client.setIdentityCard(rs.getString("CL_RNC").trim());
            client.setEmail(rs.getString("CL_EMAIL").trim());
            //Fecha de ingreso y nacimiento
            client.setEnteredDate(rs.getDate("CL_FECING"));
            client.setBirthDate(rs.getDate("CL_FECNAC"));
            //Telefono y direccion
            client.addPhone(rs.getString("CL_TELEF1").trim());
            client.setAddress(rs.getString("CL_DIREC1").trim());

            client.setClientZone(new Zone(rs.getString("ZO_CODIGO"), ""));
            client.setNcf(new NCF(rs.getString("IM_CODIGO"), ""));


            InputStream binaryStream = rs.getBinaryStream("CL_FOTO2");

            if(binaryStream != null){
                String name = FileUtils.addExtension(client.getRemoteId().toString(), FileUtils.JPG_EXT);
                File route = FileUtils.createFileRoute(Constantes.MAIN_DIR, Constantes.CLIENTS_PHOTOS);
                Bitmap bm = null;

                try {
                    bm = FileUtils.getBitmapFrom(binaryStream);

                    FileUtils.savePhoto(bm, route , name, FileUtils.DEFAULT_QUALITY);

                    client.setProfilePhoto(Uri.fromFile(new File(route,name)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                //client.setProfilePhoto(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpg")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


        return client;
    }

    @Override
    public PreparedStatement getQueryToRetriveData() {
        String query = "SELECT \n"
                + "CL_CODIGO, CL_NOMBRE, CL_DIREC1, CL_TELEF1,\n"
                + "CL_LIMCRE, CL_ESTADO, CL_FECNAC, CL_FECING, \n"
                + "CL_RNC, CL_EMAIL, CL_FOTO2, ZO_CODIGO, IM_CODIGO\n"
                + "FROM CCBDCLIE WHERE VE_CODIGO = ?";

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
    public boolean onInsertCalled(Client data) {
        Connection connection = getConnection().getSqlConnection();
        String query = "INSERT INTO CCBDCLIE(" +
                "CL_CODIGO, CL_NOMBRE, CL_DIREC1, CL_TELEF1, CL_RNC, CL_TIPORNC, CL_EMAIL," +
                "CL_ESTADO, VE_CODIGO, CL_LIMCRE,CL_FECNAC, CL_FECING, CL_FOTO2, IM_CODIGO, ZO_CODIGO "        +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            int id = generateId();

            if(id !=  NO_ID){
                String codigo = String.valueOf(id);

                data.setStatus(ColumnsSqlite.ColumnStatus.STATUS_COMPLETE);
                data.setRemoteId(codigo);

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, codigo);

                preparedStatement.setString(2, data.getName());
                preparedStatement.setString(3, data.getAddress());

                try{
                    preparedStatement.setString(4, data.getPhone(0));
                }catch (IndexOutOfBoundsException e){
                    preparedStatement.setString(4, "");
                }

                preparedStatement.setString(5, data.getIdentityCard());
                preparedStatement.setInt(6,    data.getIdCardtype());
                preparedStatement.setString(7,  data.getEmail());
                preparedStatement.setString(8,  String.valueOf(data.getCreditStatus()));
                preparedStatement.setString(9,  getVendor().getId());
                preparedStatement.setDouble(10, data.getCreditLimit());
                preparedStatement.setDate(11, new java.sql.Date(data.getBirthDate().getTime()));
                preparedStatement.setDate(12, new java.sql.Date(data.getEnteredDate().getTime()));

                File file = new File(data.getProfilePhoto().getPath());

                try {
                    preparedStatement.setBinaryStream(13, FileUtils.getFileInputStream(file), (int)file.length());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    preparedStatement.setNull(13, Types.VARBINARY);
                }


                preparedStatement.setString(14, data.getNcf().getId());
                preparedStatement.setString(15, data.getClientZone().getId());

                int rowsAffected = preparedStatement.executeUpdate();

                if(rowsAffected > 0)
                    connection.commit();
                else
                    connection.rollback();
            }else{
                return false;
            }

        } catch (SQLException e) {
            return false;
        }


        return true;
    }

    @Override
    public boolean onUpdateCalled(Client data) {
        Connection connection = getConnection().getSqlConnection();

        String query = "UPDATE CCBDCLIE SET " +
                "CL_NOMBRE=?, CL_DIREC1=?, CL_TELEF1=?, CL_RNC=?, CL_TIPORNC=?, CL_EMAIL=?," +
                "CL_ESTADO=?, VE_CODIGO=?, CL_LIMCRE=?, CL_FECNAC=?, CL_FECING=?, CL_FOTO2=?, IM_CODIGO=?, ZO_CODIGO=? "        +
                "WHERE CL_CODIGO=?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, data.getName());
            preparedStatement.setString(2, data.getAddress());

            try{
                preparedStatement.setString(3, data.getPhone(0));
            }catch (IndexOutOfBoundsException e){
                preparedStatement.setString(3, "");
            }

            preparedStatement.setString(4, data.getIdentityCard());
            preparedStatement.setInt(5, data.getIdCardtype());
            preparedStatement.setString(6,  data.getEmail());
            preparedStatement.setString(7,  String.valueOf(data.getCreditStatus()));
            preparedStatement.setString(8,  getVendor().getId());
            preparedStatement.setDouble(9, data.getCreditLimit());
            preparedStatement.setDate(10, new java.sql.Date(data.getBirthDate().getTime()));
            preparedStatement.setDate(11, new java.sql.Date(data.getEnteredDate().getTime()));

            File file = new File(data.getProfilePhoto().getPath());

            try {
                preparedStatement.setBinaryStream(12, FileUtils.getFileInputStream(file), (int)file.length());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                preparedStatement.setNull(12, Types.VARBINARY);
            }

            preparedStatement.setString(13, data.getNcf().getId());
            preparedStatement.setString(14, data.getClientZone().getId());
            preparedStatement.setString(15, String.valueOf(data.getRemoteId()));

            data.setStatus(ColumnsSqlite.ColumnStatus.STATUS_COMPLETE);

            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0)
                connection.commit();
            else
                connection.rollback();

        } catch (SQLException e) {
            return false;
        }


        return true;
    }


}
