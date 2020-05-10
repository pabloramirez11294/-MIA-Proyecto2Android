package com.example.proyecto2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private EditText correo;
    private EditText clave;
    private TextView info;
    private Button login;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue= Volley.newRequestQueue(this);
        correo=(EditText) findViewById(R.id.txtCorreo);
        clave=(EditText) findViewById(R.id.txtClave);
        info=(TextView) findViewById(R.id.txtInfo);
        login=(Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //metGet();
                    obtenerDatos(correo.getText().toString(),clave.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void validacion(String user,String pass){
        if(user.equals("admin") && pass.equals("123")){
            Intent intent = new Intent(MainActivity.this,InicioActivity.class);
            startActivity(intent);
        }else{
            info.setText("Vuelva a intenarlo");
        }
    }

    private void obtenerDatos(String correo,String clave) throws JSONException {
        Log.i("correo y clave ", correo+" " + clave);
        String url ="http://192.168.0.5:3000/register/login";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("correo", correo);
        jsonBody.put("clave", clave);
        final String mRequestBody = jsonBody.toString();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token=response.getJSONObject("dataU").get("accessToken").toString();
                    String clase=response.getJSONObject("dataU").get("clase").toString();
                    Intent intent = new Intent(MainActivity.this,InicioActivity.class);
                    intent.putExtra("TOKEN",token);
                    intent.putExtra("CLASE",clase);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_RESPONSE", error.toString());
                info.setText("Credenciales incorrectas.");
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };
        queue.add(request);
    }

    private void metGet(){
        final String url = "http://192.168.0.3:3000/register/listar";


        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(getRequest);
    }
}
