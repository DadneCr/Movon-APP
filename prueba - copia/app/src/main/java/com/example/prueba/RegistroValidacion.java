package com.example.prueba;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AlertDialog;
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

public class RegistroValidacion extends AppCompatActivity {


    Button btnBuscar, btnScanner, btnValidar, btnValidados, btnCerrar;
    TextView  tvCodigo, tvLote, tvLoteValidado, tvRegistro, tvNoResgistro, tvLoteNoValido;
    EditText edtItem, edtCodigoBarras;
    RequestQueue requestQueue;
    private Object StringRequest;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_resgistro);

        btnScanner = findViewById(R.id.btnEscanear);
        edtCodigoBarras = findViewById(R.id.edtCodigoBarras);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        tvLote = findViewById(R.id.tvLote);
        edtItem = findViewById(R.id.edItem);
        btnValidar = (Button) findViewById(R.id.btnValidar);
        tvLoteValidado= findViewById(R.id.tvLoteValido);
        tvRegistro = findViewById(R.id.tvRegistro);
        tvNoResgistro = findViewById(R.id.tvNoResgistro);
        tvLoteNoValido = findViewById(R.id.tvLoteNoValido);
        btnCerrar = (Button) findViewById(R.id.btnCerrar);


        btnScanner.setOnClickListener(mOnClickListener);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent (v.getContext(), MainActivity.class);
                startActivityForResult(intent, 0);*/
                buscarProducto("http://192.168.0.16/Movon-APP/buscarRegistro.php?guia="+edtItem.getText()+"");
                tvLoteValidado.setText(" ");
                itemValidado("http://192.168.0.16/Movon-APP/consultarLotesValidados.php?guia="+edtItem.getText()+"");
            }
        });

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validarLote("http://192.168.0.16/Movon-APP/validarLotes.php");
               buscarProducto("http://192.168.0.16/Movon-APP/buscarRegistro.php?guia="+edtItem.getText()+"");
                validarIncorrecto("http://192.168.0.16/Movon-APP/comparacionLote.php?guia="+edtItem.getText()+"&lote="+edtCodigoBarras.getText()+"");
                tvLoteValidado.setText(" ");
                itemValidado("http://192.168.0.16/Movon-APP/consultarLotesValidados.php?guia="+edtItem.getText()+"");
                registroValidado("http://192.168.0.16/Movon-APP/registrosValidados.php?guia="+edtItem.getText()+"");
            }
        });

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    //ESCANER
    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data );
        if (result.getContents() != null){
            edtCodigoBarras.setText(result.getContents());
        }else {
            edtCodigoBarras.setText("Error al escanear codigo");
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnEscanear:
                    new IntentIntegrator(RegistroValidacion.this).initiateScan();
                    break;
            }
        }
    };

    /* SELECT DE REGISTROS BD **/

        private  void buscarProducto (String link){
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(link, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject jsonObject = null;
                    for (int i = 0; i < response.length(); i++) {
                        try {

                            jsonObject = response.getJSONObject(i);
                            ///VALIDAR NÚERO DE GUIA
                                String validacion = jsonObject.getString("COUNT(*)");
                            if(validacion.equals("0")){
                                tvNoResgistro.setText("No existe el registro o ya fue validado " + edtItem.getText());
                                tvRegistro.setText("");
                                tvLote.setText("");
                                //Log.d("MyApp","Entra al if");
                            }else {
                                tvNoResgistro.setText("");
                                tvLote.setText("El registro contiene " + jsonObject.getString("COUNT(*)") + " números de serie para validar\n");
                                tvRegistro.setText("Registro " + edtItem.getText());
                                //Log.d("MyApp",validacion);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"ERROR NO EXISTE", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(getApplicationContext(), "Validando", Toast.LENGTH_SHORT).show();
                Log.d("MyApp",response);
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
                parametros.put("lotes", edtCodigoBarras.getText().toString());
                return parametros;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


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
            }
        }

        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    /**  MOSTAR ITEM INVALIDOS  **/

    private  void validarIncorrecto (String link){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(link, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {
                    try {

                        jsonObject = response.getJSONObject(i);
                        String validacion = jsonObject.getString("COUNT(*)");
                        if(validacion.equals("0")){
                            tvLoteNoValido.setText("El número de serie "+edtCodigoBarras.getText()+" es incorrecto");
                        }else {
                            tvLoteNoValido.setText("");

                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error validar incorrecto", Toast.LENGTH_SHORT).show();
            }
        }

        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private  void registroValidado(String link){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(link, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {
                    try {

                        jsonObject = response.getJSONObject(i);
                        String validacion = jsonObject.getString("COUNT(*)");
                        if(validacion.equals("0")){
                            //Toast.makeText(getApplicationContext(),"error validar incorrecto", Toast.LENGTH_SHORT).show();
                        }else {
                            tvLoteValidado.setText("");
                            tvCodigo.setText("");
                            tvLote.setText("");
                            edtItem.setText("");
                            tvNoResgistro.setText("");
                            tvRegistro.setText("");
                            Toast.makeText(getApplicationContext(),"Registro validado", Toast.LENGTH_SHORT).show();
                            /*AlertDialog.Builder builder = new AlertDialog.Builder(RegistroValidacion.this);
                            builder.setTitle("Registro validado");*/

                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getApplicationContext(),"error validar incorrecto", Toast.LENGTH_SHORT).show();
            }
        }

        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
