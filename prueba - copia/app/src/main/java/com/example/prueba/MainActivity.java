package com.example.prueba;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;

    import com.android.volley.AuthFailureError;
    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonArrayRequest;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.google.zxing.integration.android.IntentIntegrator;
    import com.google.zxing.integration.android.IntentResult;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.HashMap;
    import java.util.Map;

public class MainActivity extends AppCompatActivity {


    Button btnBuscar, btnScanner, btnValidar, btnValidados;
    TextView  tvCodigo, tvLote, tvLoteValidado, tvRegistro;
    EditText edtItem;
    RequestQueue requestQueue;
    private Object StringRequest;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);

        btnScanner = findViewById(R.id.btnEscanear);
        tvCodigo = findViewById(R.id.tvCodigoBarras);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        tvLote = findViewById(R.id.tvLote);
        edtItem = findViewById(R.id.edItem);
        btnValidar = (Button) findViewById(R.id.btnValidar);
        tvLoteValidado= findViewById(R.id.tvLoteValido);
        tvRegistro = findViewById(R.id.tvRegistro);


        btnScanner.setOnClickListener(mOnClickListener);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProducto("http://192.168.0.13/Movon-APP/buscarRegistro.php?guia="+edtItem.getText()+"");

                tvLoteValidado.setText(" ");
                itemValidado("http://192.168.0.13/Movon-APP/consultarLotesValidados.php?guia="+edtItem.getText()+"");
            }
        });

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLote("http://192.168.0.13/Movon-APP/validarLotes.php");
                buscarProducto("http://192.168.0.13/Movon-APP/buscarRegistro.php?guia="+edtItem.getText()+"");

            }
        });

        /*btnValidados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLoteValidado.setText(" ");
                itemValidado("http://192.168.0.13/Movon-APP/consultarLotesValidados.php?guia="+edtItem.getText()+"");
            }
        });*/
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data );
        if (result.getContents() != null){
            tvCodigo.setText(result.getContents());
        }else {
            tvCodigo.setText("Error al escanear codigo");
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

    /* SELECT A LA BD DE MYSQL**/

        private  void buscarProducto (String link){
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(link, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject jsonObject = null;
                    for (int i = 0; i < response.length(); i++) {
                        try {

                            jsonObject = response.getJSONObject(i);
                            if(jsonObject.getString("COUNT(*)") !=  "0"){

                                tvLote.setText("El registro contiene  " + jsonObject.getString("COUNT(*)") + " números de serie para validar\n");
                                tvRegistro.setText("Registro " + edtItem.getText());

                            }else {
                                tvLote.setText("Lotes validados");

                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"ERROR no existe", Toast.LENGTH_SHORT).show();

                            tvRegistro.setText("No existe el registro: " + edtItem.getText());

                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"ERROR no existe", Toast.LENGTH_SHORT).show();
                    tvRegistro.setText("No existe el registro: " + edtItem.getText());
                }
            }

            );
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonArrayRequest);

        }


    /* UPDATE A LA BD DE MYSQL**/
    private void validarLote(String link){
        StringRequest stringRequest;

        StringRequest = stringRequest = new  StringRequest(Request.Method.POST, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "Operación exitosa", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("guia", edtItem.getText().toString());
                parametros.put("lotes", tvCodigo.getText().toString());

                return parametros;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        tvLoteValidado.setText(" ");
        itemValidado("http://192.168.0.13/Movon-APP/consultarLotesValidados.php?guia="+edtItem.getText()+"");
    }

    private  void itemValidado (String link){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(link, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {
                    try {

                        jsonObject = response.getJSONObject(i);

                        tvLoteValidado.setText(tvLoteValidado.getText()+"\n"+jsonObject.getString("num_serie")+ "  ");
                        //tvLoteValidado.setText(jsonObject.getString("num_serie"));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ERROR", Toast.LENGTH_SHORT).show();
                tvRegistro.setText("No existe el registro: " + edtItem.getText());

            }
        }

        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


}
