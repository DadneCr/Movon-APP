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


    Button btnBuscar, btnScanner;
    TextView  tvCodigo, tvIdRegistro, tvLote;
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

        btnScanner.setOnClickListener(mOnClickListener);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProducto("http://192.168.0.13/Movon-APP/buscarRegistro.php?guia="+tvCodigo.getText()+"");
            }
        });

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
                            tvLote.setText("Ingresa los "+jsonObject.getString("COUNT(*)")+" números de serie para validar\n");


                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"ERROR DE CONEXIÓN", Toast.LENGTH_SHORT).show();
                }
            }

            );
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonArrayRequest);
        }






}
