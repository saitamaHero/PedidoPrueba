package BaseDeDatos;

import android.content.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Client;
import Models.ColumnsSqlite;
import Sqlite.Controller;

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
    public boolean onInsertCalled(Client data) {
        Connection connection = getConnection().getSqlConnection();
        String query = "INSERT INTO CCBDCLIE(" +
                "CL_CODIGO, CL_NOMBRE, CL_DIREC1, CL_TELEF1, CL_RNC, CL_TIPORNC, CL_EMAIL," +
                "CL_ESTADO, VE_CODIGO, CL_LIMCRE,CL_FECNAC, CL_FECING "        +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

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
                preparedStatement.setInt(6, data.getIdCardtype());
                preparedStatement.setString(7,  data.getEmail());
                preparedStatement.setString(8,  String.valueOf(data.getCreditStatus()));
                preparedStatement.setString(9,  getVendor().getId());
                preparedStatement.setDouble(10, data.getCreditLimit());
                preparedStatement.setDate(11, new java.sql.Date(data.getBirthDate().getTime()));
                preparedStatement.setDate(12, new java.sql.Date(data.getEnteredDate().getTime()));

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
                "CL_ESTADO=?, VE_CODIGO=?, CL_LIMCRE=?, CL_FECNAC=?, CL_FECING=? "        +
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
            preparedStatement.setString(12, String.valueOf(data.getRemoteId()));

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
