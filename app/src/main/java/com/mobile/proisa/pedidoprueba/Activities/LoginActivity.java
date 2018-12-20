package com.mobile.proisa.pedidoprueba.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Tasks.TareaAsincrona;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Stack;

import BaseDeDatos.SqlConnection;
import Models.User;
import Models.Vendor;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TareaAsincrona.OnFinishedProcess {
    private Button btnSingIn;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("");
        bindUI();
    }

    private void bindUI() {
        btnSingIn = findViewById(R.id.btn_sing_in);
        btnSingIn.setOnClickListener(this);
    }

    public User getUserFromEditText() {
        EditText txtUser = findViewById(R.id.user);
        EditText txtPassword = findViewById(R.id.password);

        User user = new User();
        user.setUser(txtUser.getText().toString());
        user.setPassword(txtPassword.getText().toString());

        return user;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sing_in:
                mUser = getUserFromEditText();
                singIn(mUser);
                break;
        }
    }

    private void singIn(User mUser) {
        new LogInTask(0, this, this, mUser).execute();
    }

    public void sendUser(User mUser) {
        Intent data = new Intent();
        data.putExtra("user", mUser);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onFinishedProcess(TareaAsincrona task) {
        if (!task.hasErrors()) {
            if (task.getId() == 0) {
                mUser = task.getData().getParcelable("user");

                if (mUser.isLogged()) {
                    sendUser(mUser);
                } else {
                    Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onErrorOccurred(int id, Stack<Exception> exceptions) {
        Exception lastException = exceptions.pop();
        Toast.makeText(getApplicationContext(), lastException.getMessage(), Toast.LENGTH_SHORT).show();
    }


    public static class LogInTask extends TareaAsincrona<Void, Void, Void> {
        private User usuario;

        public LogInTask(int id, Activity context, OnFinishedProcess listener, User usuario) {
            super(id, context, listener);
            this.usuario = usuario;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SqlConnection connection = new SqlConnection(SqlConnection.getDefaultServer());
            connection.connect();

            if (connection.isConnected()) {
                try {
                    Connection conn = connection.getSqlConnection();

                    ResultSet rs = connection.consulta(conn, getConsulta(usuario));

                    if (rs.next()) {
                        usuario.setLogged(rs.getInt("USUARIO_VALIDO") == 1 ? true : false);
                        usuario.setLevel(rs.getString("nivel").charAt(0));

                        Vendor vendor = new Vendor();
                        vendor.setId(rs.getString("ve_codigo").trim());
                        vendor.setName(rs.getString("ve_nombre").trim());
                        usuario.setVendor(vendor);
                    }
                } catch (Exception e) {
                    publishError(e);
                }

                getData().putParcelable("user", usuario);
            } else {
                publishError(new Exception("Servidor No Disponible"));
            }

            return null;
        }

        private String getConsulta(User usuario) {
            String query = "DECLARE @USER CHAR(40) = '%s'\n" + "DECLARE @PASS CHAR(10) = '%s'\n" + "SELECT ISNULL((SELECT CASE CLAVE WHEN @PASS THEN 1 ELSE 0 END validUser FROM CONTASEG WHERE USUARIO=@USER),0)USUARIO_VALIDO,NIVEL,\n" + "A.VE_CODIGO,ISNULL(B.VE_NOMBRE,'')VE_NOMBRE\n" + "FROM CONTASEG AS A \n" + "LEFT JOIN CCBDVEND AS B ON A.VE_CODIGO=B.VE_CODIGO\n" + "WHERE USUARIO=@USER";

            return String.format(Locale.getDefault(), query, usuario.getUser(), usuario.getPassword());
        }
    }
}
