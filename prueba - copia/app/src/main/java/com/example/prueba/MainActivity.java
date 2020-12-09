package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.AccessController;

public class MainActivity extends AppCompatActivity {
    Button btnIngresar;
    EditText edtCorreo, edtPassword;
    TextView tvError;
    RequestQueue requestQueue;
    private Object StringRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnIngresar = findViewById(R.id.btnIngresar);
        edtCorreo = findViewById(R.id.edtCorreo);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvErrorLogin);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent (v.getContext(), RegistroValidacion.class);
                startActivityForResult(intent, 0);*/
                login("http://192.168.0.16/Movon-APP/login.php?correo="+edtCorreo.getText()+"&password="+edtPassword.getText()+"");
            }
        });
    }

    public void login(String link){
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
                            Toast.makeText(getApplicationContext(),"ERROR USUARIO O CONTRSEÑA DIFERENTE", Toast.LENGTH_SHORT).show();
                            tvError.setText("ERROR USUARIO O CONTRSEÑA");
                       }else {
                            tvError.setText(" ");
                            Intent intent = new Intent (getApplicationContext(), RegistroValidacion.class);
                            startActivityForResult(intent, 0);
                            Toast.makeText(getApplicationContext(),"Correcto", Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ERROR CONSULTA", Toast.LENGTH_SHORT).show();
            }
        }

        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


}