package com.example.movonapp;

import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btnConsultar, btnEdt, btnScanner;
    EditText edtGuia;
    TextView tvNoRegistro, tvLote;
    LinearLayout layoutItems;
    List<EditText> edtList = new ArrayList<EditText>();
    String guia;
    Statement st;
    ResultSet rs;
    int cantidadItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConsultar =findViewById(R.id.btnConsultar);
        edtGuia = findViewById(R.id.edtGuia);
        tvNoRegistro = findViewById(R.id.tvNoRegistro);
        tvLote = findViewById(R.id.tvLotes);
        layoutItems = findViewById(R.id.layoutItems);
        btnEdt =findViewById(R.id.btnEdt);
        btnScanner = findViewById(R.id.btnEscanear);
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guia = String.valueOf(edtGuia.getText()).toUpperCase().trim();
                System.out.println(guia);
                Thread construir = consultarRegistro(guia);
                construir.start();
                construir = null;

                btnEdt.callOnClick();
            }
        });
        btnEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borarEdt();
                edtDinamico(cantidadItem);

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
                    String stsql = "SELECT id_registro FROM registros WHERE guia = '"+guia+"' ";
                    st = conn.createStatement();
                    rs = st.executeQuery(stsql);
                    if(rs.next()){
                        System.out.println(rs.getString(1));
                        String sql1 = "SELECT COUNT(*) FROM lotes INNER JOIN registros on registros.id_registro = lotes.id_registro WHERE registros.guia = '"+guia+"' and  lotes.validado=0";
                        st = conn.createStatement();
                        rs = st.executeQuery(sql1);
                        rs.next();
                        cantidadItem = rs.getInt(1);
                        tvLote.setText("El registro contiene " +cantidadItem+ " números de serie para validar\n");
                        System.out.println("cantidad item "+cantidadItem);
                    }else{
                        tvNoRegistro.setText("No existe el registro "+guia);
                        tvLote.setText("");
                        cantidadItem = 0;
                    }


                    conn.close();
                } catch (SQLException se) {
                    System.out.println(" Error: " + se.toString());
                }

            }
        };


        return sqlThread;
    }


    private void edtDinamico(int n){

        final EditText[] editTexts = new  EditText[n];
        edtList.clear();
        for (int i = 1; i <= n; i++) {
            // create a new textview
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
            //Remove 2nd(index:1) TextView from the parent LinearLayout
            //Log.d("MyApp", "Entra al for"+i);
            layoutItems.removeView(edtList.get(i));

        }
    }

    public static  Connection getConnection(){
        Connection conn = null;
        try {


            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.8:3306/movon_tracking", "dadne", "soporte");


        }catch (Exception e) {

            System.out.println("oops! No se puede conectar. Error: " +e);
        }
        return  conn;
    }
}