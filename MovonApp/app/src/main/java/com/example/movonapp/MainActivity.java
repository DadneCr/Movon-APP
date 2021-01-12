package com.example.movonapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btnConsultar, btnCerrar, btnScanner, btnValidar;
    EditText edtGuia;
    TextView tvNoRegistro, tvLote, tvLoteValidado, tvErrorLote;
    LinearLayout layoutItems;
    List<EditText> edtList = new ArrayList<EditText>();
    String guia;
    Statement st;
    ResultSet rs;
    PreparedStatement pst;
    RequestQueue requestQueue;
    int cantidadItem, id_registro;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    private Object StringRequest;
    String itemsValidados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConsultar =findViewById(R.id.btnConsultar);
        edtGuia = findViewById(R.id.edtGuia);
        tvNoRegistro = findViewById(R.id.tvNoRegistro);
        tvLote = findViewById(R.id.tvLotes);
        layoutItems = findViewById(R.id.layoutItems);
        btnScanner = findViewById(R.id.btnEscanear);
        btnValidar = findViewById(R.id.btnValidar);
        tvLoteValidado = findViewById(R.id.tvLoteValidado);
        tvErrorLote = findViewById(R.id.tvErrorLote);
        btnCerrar = (Button) findViewById(R.id.btnCerrar);


        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(), login.class);
                startActivityForResult(intent, 0);
            }
        });
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoRegistro.setText(" ");
                tvLoteValidado.setText(" ");
                tvLote.setText(" ");

                if(!edtGuia.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Buscando registro", Toast.LENGTH_SHORT).show();
                    guia = String.valueOf(edtGuia.getText()).toUpperCase().trim();
                    System.out.println(guia);
                    Thread construir = consultarRegistro(guia);
                    construir.start();
                    construir = null;

                }
            }
        });

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLoteValidado.setText(" ");
                tvLoteValidado.setText(" ");
                tvErrorLote.setText(" ");

                //Toast.makeText(getApplicationContext(),"Validando espere un momento", Toast.LENGTH_SHORT).show();
                Thread validar = validarLote(id_registro);
                validar.start();
                validar = null;


            }
        });
        btnScanner.setOnClickListener(mOnClickListener);

    }

    //ESCANER
    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data );
        if (result.getContents() != null){
            int  sizeList = edtList.size();
            for (int i=0; i<sizeList; i++) {
                if(edtList.get(i).getText().toString().isEmpty()) {
                    //edtCodigoBarras.setText(result.getContents());
                    edtList.get(i).setText(result.getContents());
                    break;
                }
            }
        }else {

            Toast.makeText(getApplicationContext(),"Error al escanear codigo", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnEscanear:
                    new IntentIntegrator(MainActivity.this).initiateScan();
                    break;
            }
        }
    };

    public Thread consultarRegistro(final String guia) {

        Thread sqlThread = new Thread() {
            public void run() {
                try {


                    Connection conn = null;
                    conn =getConnection();
                    String stsql = "SELECT id_registro, id_status FROM registros WHERE guia = '"+guia+"' LIMIT 1";
                    st = conn.createStatement();
                    rs = st.executeQuery(stsql);

                    if(rs.next()){
                        if(rs.getInt(2) != 1){
                        System.out.println(rs.getString(1));
                        id_registro = rs.getInt(1);
                        String sql1 = "SELECT COUNT(*) FROM lotes INNER JOIN registros on registros.id_registro = lotes.id_registro WHERE registros.guia = '"+guia+"' and  lotes.validado=0 LIMIT 1";
                        st = conn.createStatement();
                        rs = st.executeQuery(sql1);
                        rs.next();
                        cantidadItem = rs.getInt(1);
                            MainActivity.this.runOnUiThread(new Runnable() {

                                public void run(){
                                    borarEdt();
                                    edtDinamico(cantidadItem);
                                    tvLote.setText("El registro contiene " +cantidadItem+ " números de serie para validar\n");
                                }

                            });


                        System.out.println("cantidad item "+cantidadItem);
                            String sql6 = "SELECT  num_serie FROM lotes WHERE id_registro ="+id_registro+" and  lotes.validado=1";
                            st = conn.createStatement();
                            rs = st.executeQuery(sql6);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run(){
                                    tvLoteValidado.setText("ITEMS VALIDADOS\n");
                                }
                            });
                            itemsValidados ="Itmens validados \n";
                            while (rs.next()) {

                                itemsValidados +="\n"+ rs.getString(1);
                            }
                            System.out.println(itemsValidados);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run(){
                                    tvLoteValidado.setText(itemsValidados);
                                }
                            });
                        }else{

                            MainActivity.this.runOnUiThread(new Runnable() {

                                public void run(){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Registro validado");
                                    builder.setMessage("El registro "+guia+" ha sido validado");
                                    builder.setNegativeButton(android.R.string.cancel, null);
                                    Dialog dialog = builder.create();
                                    dialog.show();
                                    borarEdt();
                                    tvErrorLote.setText(" ");
                                    tvLote.setText(" ");
                                }

                            });
                        }
                    }else{


                        MainActivity.this.runOnUiThread(new Runnable() {

                            public void run(){

                                tvNoRegistro.setText("No existe el registro "+guia);
                                tvLote.setText(" ");
                                cantidadItem = 0;
                                borarEdt();
                                tvErrorLote.setText(" ");
                                tvLoteValidado.setText(" ");

                            }

                        });
                    }
                    conn.close();
                } catch (SQLException se) {
                    System.out.println(" Error: " + se.toString());
                }

                MainActivity.this.runOnUiThread(new Runnable() {

                    public void run(){
                        if(!(alertDialog == null)) {
                            alertDialog.dismiss();
                        }
                    }

                });
            }

        };


        return sqlThread;
    }

    public Thread validarLote(int registro ) {

        Thread threadLote = new Thread() {
            public void run() {
                String numero_serie;

                MainActivity.this.runOnUiThread(new Runnable() {

                    public void run(){
                        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Validando");
                        alertDialogBuilder.setMessage("Espere un momento se está validando los números de serie").setCancelable(false);
                         alertDialog = alertDialogBuilder.create();
                         alertDialog.show();
                    }

                });

                for (int i = 0; i<edtList.size(); i++ ) {
                    numero_serie = edtList.get(i).getText().toString().toUpperCase().trim();

                    if(!edtList.get(i).getText().toString().isEmpty()) {

                        try {
                            Connection conn = null;
                            conn = getConnection();
                            String stsql = "SELECT lotes.id_lote FROM lotes INNER JOIN registros on lotes.id_registro = registros.id_registro WHERE lotes.num_serie='" + numero_serie + "' and lotes.id_registro=" + id_registro + " and lotes.validado=0 LIMIT 1";
                            st = conn.createStatement();
                            rs = st.executeQuery(stsql);
                            if (rs.next()) {
                                String updateLote = "UPDATE lotes SET validado=? WHERE num_serie=? and id_registro=?";
                                pst = conn.prepareStatement(updateLote);
                                pst.setInt(1,1);
                                pst.setString(2, numero_serie);
                                pst.setInt(3, id_registro);
                                int rupdate = pst.executeUpdate();
                                if(rupdate > 0){

                                    String sql1 = "SELECT COUNT(*) FROM lotes WHERE id_registro=" + id_registro + " and datos=1 and validado=0";
                                    st = conn.createStatement();
                                    rs = st.executeQuery(sql1);
                                    rs.next();
                                    if (rs.getInt(1) ==0 ){
                                        String updateRegistro = "UPDATE registros SET id_status=? WHERE id_registro=?";
                                        pst = conn.prepareStatement(updateRegistro);
                                        pst.setInt(1,1);
                                        pst.setInt(2, id_registro);
                                        int rtRegistro = pst.executeUpdate();
                                        if (rtRegistro>0){
                                            enviarCorreo("http://192.168.0.19/Movon-APP/enviarCorreo.php", guia, id_registro);
                                        }
                                    }

                                }

                            } else {
                                //tvErrorLote.setText("Número de serie incorrecto:");
                                System.out.println("no se encuentra");

                                final String finalNumero_serie = numero_serie;
                                MainActivity.this.runOnUiThread(new Runnable() {

                                    public void run(){
                                        tvErrorLote.setText(tvErrorLote.getText() +"\n"+ finalNumero_serie +" es incorrecto");

                                    }

                                });
                            }


                            conn.close();

                        } catch (SQLException se) {
                            System.out.println(" Error: " + se.toString());
                        }
                    }


                }
                MainActivity.this.runOnUiThread(new Runnable() {

                    public void run(){
                        Thread construir = consultarRegistro(guia);
                        construir.start();
                        construir = null;

                    }

                });
            }
        };
        return threadLote;
    }
    private void enviarCorreo(String link, final String guia, final int registro){
        StringRequest stringRequest;

        StringRequest = stringRequest = new  StringRequest(Request.Method.POST, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getApplicationContext(), "Validando", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("guia", guia);
                parametros.put("id_registro", String.valueOf(registro));
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void edtDinamico(int n){

        final EditText[] editTexts = new  EditText[n];
        edtList.clear();
        for (int i = 1; i <= n; i++) {

            EditText editText = new EditText(this);
            editText.setId(i);
            editText.setHint("Item "+i);

            edtList.add(editText);
            layoutItems.addView(editText);
        }
        int cantidadItems = edtList.size();

    }
    private  void borarEdt( ){
        //Log.d("MyApp", String.valueOf(edtList.size()));
        int tamaño= edtList.size();
        for (int i=0; i<tamaño; i++){
            //Log.d("MyApp", "Entra al for"+i);
            layoutItems.removeView(edtList.get(i));

        }

    }

    public static  Connection getConnection(){
        Connection conn = null;
        try {


            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://www.movon.com.mx:3306/movoncom_tracking_test", "movoncom_admin_tracking", "]c$+L*T%C4u6");


        }catch (Exception e) {

            System.out.println("oops! No se puede conectar. Error: " +e);
        }
        return  conn;
    }
}