package com.abbasandfriends.injurymonitoringsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import sendable.data.Acceleration;
import sendable.Sendable;

/**
 * Creates a Graph of acceleration magnitudes received from
 * the player.
 * Passed a <Sendable> list of data from MainAppActivity, iterates the list to
 * create a chart of Time vs Acceleration Magnitude.
 * Uses an Open Source library to create the graphs - MPAndroidChart
 */
public class GraphActivity extends android.app.Activity {
    List<Sendable> graphActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        graphActivity = null;

        if(MainAppActivity.data != null){
            graphActivity = MainAppActivity.data;
        }

        if (graphActivity != null) {
            BarChart chart = (BarChart) findViewById(R.id.chart);

            BarData data = new BarData(getXAxisValues(), getDataSet());
            chart.setData(data);
            chart.setDescription("Impact Monitoring");
            chart.animateXY(2000, 2000);
            chart.invalidate();
        }
        else{
            Toast.makeText(this, "Please request data", Toast.LENGTH_LONG).show();
        }

    }
    /**
     * Returns an ArrayList for the magnitude of accelerations received from database.
     * Used as y axis values for graph.
     */
    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        for (int i = 0; i < graphActivity.size(); i++)
        {
            if (graphActivity.get(i) instanceof Acceleration) {
                Acceleration a = (Acceleration) graphActivity.get(i);
                float accel = (float)a.getAccelMag();
                BarEntry v1e1 = new BarEntry(accel, i);
                valueSet1.add(v1e1);
            }
        }
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Change in Acceleration");

        //Default color so the graph won't crash
        barDataSet1.setColor(Color.rgb(155, 0, 0));


        //Changes the colour of the graph according to the player
        for (int i = 0; i < graphActivity.size(); i++){
            int colorRatio = graphActivity.get(i).getUID()*graphActivity.get(i).getUID();
            barDataSet1.setColor(Color.rgb(255/colorRatio, 0, 2*colorRatio));
        }

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;


    }
    /**
     * Creates an ArrayList of the dates with relation to the Acceleration graphed.
     * Used as the x axis values.
     */
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();

        for (int i = 0; i < graphActivity.size(); i++)
        {
            if (graphActivity.get(i) instanceof Acceleration){
                Acceleration a = (Acceleration) graphActivity.get(i);
                String dateAccel = a.getDate().toString();
                xAxis.add(dateAccel);
            }
        }
        return xAxis;
    }
}
