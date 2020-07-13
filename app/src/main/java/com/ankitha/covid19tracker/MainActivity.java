package com.ankitha.covid19tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity
        extends AppCompatActivity {

    // Create the object of TextView
    TextView tvCases, tvRecovered, tvActive, tvTotalDeaths, state, rec, tot, act, dea,no,none;
    TableRow tr,td;
    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link those objects with their respective id's
        // that we have given in .XML file
        tvCases
                = findViewById(R.id.tvCases);
        tvRecovered
                = findViewById(R.id.tvRecovered);
        tvActive
                = findViewById(R.id.tvActive);
        tvTotalDeaths
                = findViewById(R.id.tvTotalDeaths);
        no =findViewById(R.id.no);

        table = (TableLayout) findViewById(R.id.table);


        table.setColumnStretchable(0,true);
        table.setColumnStretchable(1,true);
        table.setColumnStretchable(2,true);
        table.setColumnStretchable(3,true);
        table.setColumnStretchable(4,true);

        // Creating a method fetchdata()
        fetchdata();

    }

    private void fetchdata()
    {

        // Create a String request
        String url = "https://api.covid19india.org/data.json";

        // using Volley Library

        final StringRequest request
                = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onResponse(String response)
                    {

                        // Handle the JSON object and
                        // handle it inside try and catch
                        try {

                            // Creating object of JSONObject
                            JSONObject obj
                                    = new JSONObject(
                                    response.toString());
                            JSONArray paramsArr = obj.getJSONArray("statewise");
                            JSONObject data = paramsArr.getJSONObject(0);


                            int recovered =Integer.parseInt(data.getString("recovered"));
                            int active =Integer.parseInt(data.getString("active"));
                            int death =Integer.parseInt(data.getString("deaths"));
                            int cases =Integer.parseInt(data.getString("confirmed"));


                            // Set the data in text view
                            // which are available in JSON format
                            // Note that the parameter inside
                            // the getString() must match
                            // with the name given in JSON format

                            tvCases.setText(String.format("%,d", cases));
                            tvRecovered.setText(
                                    String.format("%,d", recovered));
                            tvActive.setText(String.format("%,d", active));
                            tvTotalDeaths.setText(String.format("%,d", death));

                            fetchchart(recovered, active, death); //Fetch the Pie Chart

                            fetchtable(paramsArr); //Fetch the table for State info

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(
                                MainActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        RequestQueue requestQueue
                = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    @SuppressLint({"DefaultLocale", "ResourceType"})
    public void fetchtable(JSONArray obj) throws JSONException {

        for( int i = 1; i<obj.length() ;i++ ) {

            JSONObject data = obj.getJSONObject(i);

            int total= Integer.parseInt(data.getString("confirmed"));
            int recovered= Integer.parseInt(data.getString("recovered"));
            int active= Integer.parseInt(data.getString("active"));
            int death= Integer.parseInt(data.getString("deaths"));
            String name = data.getString("state");

            tr = new TableRow(this);
            td = new TableRow(this);

            // Here create the TextView dynamically

            state = new TextView(this);
            state.setText(name);
            if(name.equals("Dadra and Nagar Haveli and Daman and Diu")){
                state.setText("Daman and Diu");
            }
            if(name.equals("Andaman and Nicobar Islands")){
                state.setText("AN Islands");
            }
            state.setTextColor(Color.WHITE);
            state.setTextSize(16);
            state.setPadding(5,5,5,5);
            tr.addView(state);

            tot = new TextView(this);
            tot.setText(String.format("%,d", total));
            tot.setPadding(5,5,5,5);
            tot.setTextSize(16);
            tot.setTextColor(Color.rgb(248, 198, 57));
            tr.addView(tot);

            rec = new TextView(this);
            rec.setText(String.format("%,d", recovered));
            rec.setPadding(5,5,5,5);
            rec.setTextSize(16);
            rec.setTextColor(Color.rgb(251, 114, 104));
            tr.addView(rec);

            act = new TextView(this);
            act.setText(String.format("%,d", active));
            act.setPadding(5,5,5,5);
            act.setTextSize(16);
            act.setTextColor(Color.rgb(18, 182, 167));
            tr.addView(act);

            dea = new TextView(this);
            dea.setPadding(5,5,5,5);
            dea.setTextSize(16);
            dea.setText(String.format("%,d", death));
            dea.setTextColor(Color.rgb(128, 128, 128));
            tr.addView(dea);

            none= new TextView(this);
            none.setText("  ");
            td.addView(none);

        // finally add this to the table row
            table.addView(tr);
            table.addView(td);


        }
    }


    public void fetchchart(int rec, int act, int dea)
    {
        AnimatedPieView mAnimatedPieView = findViewById(R.id.piechart);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();

        config.startAngle(-150)// Starting angle offset
                .addData(new SimplePieInfo(rec, Color.rgb(251, 114, 104),"Recovered"))//Data (bean that implements the IPieInfo interface)
                .addData(new SimplePieInfo(dea, Color.rgb(147, 147, 147),"Deaths"))
                .addData(new SimplePieInfo(act, Color.rgb(18, 182, 167),"Active"))
                .duration(1500)
                .drawText(true)
                .textSize(35);
        config.selectListener(new OnPieSelectListener<IPieInfo>() {
                                  @Override
                                  public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isFloatUp) {
                                      Toast.makeText(MainActivity.this, pieInfo.getDesc() + ":  " + pieInfo.getValue(), Toast.LENGTH_SHORT).show();
                                  }
                              }
        );

        // The following two sentences can be replace directly 'mAnimatedPieView.start (config); '
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();

    }
}

