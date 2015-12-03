package com.abbasandfriends.injurymonitoringsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sendable.data.Acceleration;
import sendable.data.Position;
import sendable.DataType;
import sendable.Sendable;
import sendable.data.Service;

public class GraphActivity extends android.app.Activity {
    List<Sendable> graphActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        graphActivity = null;
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
        //graphActivity = MainAppActivity.data;
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        for (int i = 0; i < graphActivity.size(); i++)
        {
            if (graphActivity.get(i) instanceof Acceleration) {
                Acceleration a = (Acceleration) graphActivity.get(i);
                float accel = a.getAccelMag();
                BarEntry v1e1 = new BarEntry(accel, i);
                valueSet1.add(v1e1);
            }
        }
        //BarEntry v1e1 = new BarEntry(30.000f, 0);
        //valueSet1.add(v1e1);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Change in Acceleration");
        barDataSet1.setColor(Color.rgb(155, 0, 0));
        //BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Brand 2");
        //barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        //dataSets.add(barDataSet2);
        return dataSets;

    }

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