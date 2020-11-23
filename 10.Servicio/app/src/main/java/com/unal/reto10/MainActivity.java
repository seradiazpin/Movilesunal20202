package com.unal.reto10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner fechas_spinner;
    private ArrayList<String> fechass;
    private ArrayAdapter<String> fechas_adapter;
    private Spinner horas_spinner;
    private ArrayList<String> horass;
    private ArrayAdapter<String> horas_adapter;
    private ListView list;
    private ArrayList<String> vehicolo;
    private ArrayAdapter<String> cod_adapter;
    private Context context = this;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fechass = new ArrayList<>();
        horass = new ArrayList<>();
        vehicolo = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        String url = "https://www.datos.gov.co/resource/23nq-t3bk.json?$select=distinct%20dia_entrada&$order=dia_entrada%20ASC";
        fechas_spinner = (Spinner) findViewById(R.id.fecha);
        horas_spinner = (Spinner) findViewById(R.id.hora);
        list = findViewById(R.id.list);
        JsonArrayRequest departamentos = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject tmp = null;
                            try {
                                tmp = response.getJSONObject(i);
                                fechass.add(tmp.getString("dia_entrada"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        fechas_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, fechass);
                        fechas_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        fechas_spinner.setAdapter(fechas_adapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        fechas_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                horass.clear();
                String tmp = (String) parent.getItemAtPosition(pos);
                String url = "https://www.datos.gov.co/resource/23nq-t3bk.json?$select=distinct%20hora&dia_entrada="+ tmp + "&$order=hora%20ASC";
                JsonArrayRequest municipios = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject tmp = null;
                                    try {
                                        tmp = response.getJSONObject(i);
                                        horass.add(tmp.getString("hora"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                horas_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, horass);
                                horas_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                horas_spinner.setAdapter(horas_adapter);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                queue.add(municipios);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        horas_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                vehicolo.clear();
                final String tmp = (String) parent.getItemAtPosition(pos);
                String url = "https://www.datos.gov.co/resource/23nq-t3bk.json?hora=" + tmp;
                JsonArrayRequest codes = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject tmp = null;
                                    try {
                                        tmp = response.getJSONObject(i);
                                        String tmp2 = "Comparendo: " + tmp.getString("comparendo") + "\n";
                                        tmp2 += "ID Funcionario: " + tmp.getString("funcionario_autoriza") + "\n";
                                        tmp2 += "Direccion: " + tmp.getString("dierrcion_comparendo") + "\n";
                                        tmp2 += "# Entrada: " + tmp.getString("numero_entrada") + "\n";
                                        tmp2 += "Clase: " + tmp.getString("clase") + "\n";
                                        tmp2 += "Dia Entrada: " + tmp.getString("dia_entrada") + "\n";
                                        tmp2 += "Hora Entrada: " + tmp.getString("hora") + "\n";
                                        tmp2 += "Id Concepto: " + tmp.getString("concepto") + "\n";
                                        tmp2 += "Id Comparendo: " + tmp.getString("comparendo") + "\n";
                                        tmp2 += "Placa: " + tmp.getString("placa") + "\n";
                                        vehicolo.add(tmp2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                cod_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, vehicolo);
                                list.setAdapter(cod_adapter);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("REQ", "bad");
                            }
                        });
                queue.add(codes);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        queue.add(departamentos);

    }
}
