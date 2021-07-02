package com.example.clima;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.GestureDescription;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    EditText ciudad;
    TextView resultado,climaac;
    private final String url ="http://api.openweathermap.org/data/2.5/forecast";
    private final String appid ="5244eab018d02d88b9e9f662bf642b7c";
    DecimalFormat df = new DecimalFormat("#.##");
    boolean hoy = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ciudad = findViewById(R.id.ciudad);
        resultado = findViewById(R.id.resultados);
        climaac = findViewById(R.id.climaac);
    }

    public void obtenerClima(View view) {
        String tempUrl = "";
        String city = ciudad.getText().toString().trim();
        if (city.equals("")) {
            resultado.setText("El campo ciudad no puede estar vacio");
        } else {
            tempUrl = url + "?q=" + city + "&appid=" + appid;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    String output = "", fechaws = "", fechaac = "";
                    double temp = 0;
                    int horaIgual = 0;
                    hoy = true;

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("list");

                        fechaac = getDate().trim();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject item1 = jsonArray.getJSONObject(i);
                            fechaws = item1.getString("dt_txt").toString().substring(0, 10);

                            if (horaIgual == 0) {

                                JSONObject jsonObjectWeather = jsonArray.getJSONObject(i);
                                JSONObject main = jsonObjectWeather.getJSONObject("main");
                                temp = main.getInt("temp") - 273;

                                if (hoy == false) {
                                    output += "Para la fecha:" + fechaws + " es "
                                            + "Temp: " + df.format(temp) + " ºC" + "\n\n";
                                } else {
                                    climaac.setText("La Temperatura actual es: " + df.format(temp) + " ºC");
                                    hoy = false;
                                }

                                horaIgual = 1;

                            } else {

                                if (!fechaws.trim().equals(fechaac)) {
                                    fechaac = convertiraDate(fechaac);
                                    horaIgual = 0;

                                }

                            }

                        }

                        resultado.setTextColor(Color.rgb(0, 0, 0));
                        resultado.setText(output);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }

            });
            //devuelve los resultados
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

        //obtiene la fecha de cada dia
    private String getDate(){
        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        return date;
    }

    // convierte la fecha de string a date le suma 1 dia y vuelve a convertir a date
        private String convertiraDate(String fecha){
            Date date = null;
            Calendar calendar = null;
            String dateNew = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = format.parse(fecha);
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_YEAR, 1);// Configuramos la fecha que se recibe
                dateNew = format.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return dateNew;

        }
}