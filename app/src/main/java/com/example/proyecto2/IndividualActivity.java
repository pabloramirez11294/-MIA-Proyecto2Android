package com.example.proyecto2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.ImageView;

import java.io.UnsupportedEncodingException;

public class IndividualActivity extends AppCompatActivity {
    private RequestQueue queue;
    private TextView txtNombre;
    private  TextView txtPrecio;
    private  TextView txtCantidad;
    private EditText editCantida;
    private Button cantidad;
    private TextView  textInfo;
    String cod;
    String token;
    ImageView imageV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        editCantida =(EditText)findViewById(R.id.editCantidad);
        imageV= (ImageView)findViewById(R.id.imageView);
        txtNombre = (TextView)findViewById(R.id.txtNombre);
        txtPrecio = (TextView)findViewById(R.id.txtPrecio);
        txtCantidad = (TextView)findViewById(R.id.txtCantidad);
        textInfo = (TextView)findViewById(R.id.textInfo);
        queue= Volley.newRequestQueue(this);
        cod = getIntent().getExtras().getString("COD");
        token = getIntent().getExtras().getString("TOKEN");
        buscar(cod);

        cantidad=(Button) findViewById(R.id.cmdCarrito);
        cantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    agregarCarrito(Integer.parseInt(editCantida.getText().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void buscar(String txt){
        final String url = "http://192.168.0.5:3000/productos/getProducto/"+txt;


        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());
                        try{
                            // Loop through the array elements
                            JSONArray otro=null;
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                //JSONObject o =response.getJSONObject(i);
                                 otro = response.getJSONArray(i);

                                Log.d("Objec ", otro.toString());
                            }
                            txtNombre.setText(otro.get(1).toString());
                            txtCantidad.setText(otro.get(6).toString());
                            txtPrecio.setText(otro.get(4).toString());
                            Picasso.get().load("http://192.168.0.5:3000/"+otro.get(2).toString()).into(imageV);

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

    private void agregarCarrito(int cantidad) throws JSONException {
        String url ="http://192.168.0.5:3000/productos/agregarCarrito";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id_u", token);
        jsonBody.put("codigo", cod);
        jsonBody.put("cantidad",cantidad);
        final String mRequestBody = jsonBody.toString();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    textInfo.setText("Se añadio al carrito exitosamente.");
                } catch (Exception e) {
                    Log.e("LOG_RESPONSE",  e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_RESPONSE", error.toString());
                textInfo.setText(error.toString());
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
