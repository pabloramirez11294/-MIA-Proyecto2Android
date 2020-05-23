package com.example.proyecto2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Carrito extends AppCompatActivity {
    private String token;
    private RequestQueue queue;
    private EditText textCarrito;
    private TextView txtComprar;
    private int[] codigos;
    private int[] cantidades;
    private Double[] subtotales;
    private Button btnComprar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        textCarrito=findViewById(R.id.txtListado);
        btnComprar=findViewById(R.id.btnComprar);
        txtComprar=findViewById(R.id.txtComprar);
        token = getIntent().getExtras().getString("TOKEN");
        queue= Volley.newRequestQueue(this);
        getCarrito(token);

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double total=0.0;
                for (int i=0;i<subtotales.length;i++){
                    total+=subtotales[i];
                }
                try {
                    Comprar(token,codigos,total,subtotales,cantidades);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getCarrito(String txt){
        final String url = "http://192.168.43.36:3000/productos/getCarrito/"+txt;


        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());
                        try{
                            // Loop through the array elements
                            codigos=new int[response.length()];
                            subtotales=new Double[response.length()];
                            cantidades=new int[response.length()];
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                Object o =response.get(i);
                                JSONArray otro = response.getJSONArray(i);


                                Log.d("Objec ", otro.toString());
                                textCarrito.setText(textCarrito.getText().toString() + otro.toString()+"\n");
                                cantidades[i]=Integer.parseInt(otro.get(4).toString());
                                codigos[i]=Integer.parseInt(otro.get(0).toString());
                                subtotales[i]=Double.parseDouble(otro.get(5).toString());
                                //nombres.add(i,otro.get(1)+" Precio: "+otro.get(4));
                                //codigos.add(i,otro.get(0));
                            }



                        }catch (JSONException e){
                            Log.d("Error.Response", e.toString());
                        }

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

    private void Comprar(String id_comprador,int  codigos[],double total,Double subtotales[],int cantidades[]) throws JSONException {
        JSONArray cods=new JSONArray(codigos);
        JSONArray subtotalesJ=new JSONArray(subtotales);
        JSONArray cantidadesJ=new JSONArray(cantidades);
        Log.i("datos ", id_comprador+" " + cods+ " "+ total);
        String url ="http://192.168.43.36:3000/productos/comprar";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id_comprador", id_comprador);
        jsonBody.put("codigos", cods);
        jsonBody.put("subtotales", subtotalesJ);
        jsonBody.put("cantidades", cantidadesJ);
        final String mRequestBody = jsonBody.toString();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("datos ", response.toString());
                    txtComprar.setText("Compara exitosa");
                    textCarrito.setText("");
                    getCarrito(token);
                } catch (Exception e) {
                    Log.i("Error ", e.toString());
                    txtComprar.setText("Error al comprar.");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_RESPONSE", error.toString());
                txtComprar.setText("No se pudo comprar.");
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
}
