package com.example.proyecto2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InicioActivity extends AppCompatActivity {
    private String token;
    private RequestQueue queue;
    public ArrayList nombres;
    private ListView lv1;
    public ArrayList codigos;
    ArrayAdapter<String> adapter;
    private Button btnCarrito;
    private Button btnCerrarSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        lv1 = (ListView)findViewById(R.id.listProd);
        btnCarrito=findViewById(R.id.btnCarrito);
        btnCerrarSesion=findViewById(R.id.btnCerrarSesion);
        this.nombres=new ArrayList();
        this.codigos=new ArrayList();
        queue= Volley.newRequestQueue(this);
        token = getIntent().getExtras().getString("TOKEN");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nombres);
        buscar("a");
        btnCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioActivity.this,Carrito.class);
                intent.putExtra("TOKEN",token);
                startActivity(intent);
            }
        });
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void buscar(String txt){
        final String url = "http://192.168.43.36:3000/productos/buscarTodos";


        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());
                        try{
                            // Loop through the array elements
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                Object o =response.get(i);
                                JSONArray otro = response.getJSONArray(i);
                                // Display the formatted json data in text view
                                //mTextView.append(firstName +" " + lastName +"\nAge : " + age);
                                //mTextView.append("\n\n");
                              /*  Log.d("Objec ", o.toString());
                                nombres.add(i,o.toString());*/

                                Log.d("Objec ", otro.toString());
                                nombres.add(i,otro.get(1)+" Precio: "+otro.get(4));
                                codigos.add(i,otro.get(0));
                            }

                            Log.d("Nombresssssssssssss ", nombres.toString());
                            lv1.setAdapter(adapter);
                            lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    //tv1.setText("La edad de " + lv1.getItemAtPosition(i) + " es " + edades[i] + " años" );
                                    Log.d("codigo ", codigos.get(i).toString());
                                    Intent intent = new Intent(InicioActivity.this,IndividualActivity.class);
                                    intent.putExtra("COD",codigos.get(i).toString());
                                    intent.putExtra("TOKEN",token);
                                    startActivity(intent);
                                }
                            });

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
}
