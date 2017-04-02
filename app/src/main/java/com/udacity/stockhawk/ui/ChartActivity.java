package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.common.collect.Multiset;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.data;
import static android.R.attr.entries;
import static android.R.attr.label;
import static android.R.attr.y;
import static com.udacity.stockhawk.R.id.google;
import static com.udacity.stockhawk.R.id.symbol;

public class ChartActivity extends AppCompatActivity {
    public static final String EXTRA_SYMBOL = "extra:symbol";
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.chart)
    LineChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);
        String symbol = getIntent().getStringExtra(EXTRA_SYMBOL);
        plot(symbol);
    }
    public String plot(String symbol) {
        String history = getHistory(symbol);

        List<String[]> lines = getLines(history);

        List<Entry> entries = new ArrayList<>();
        final List<Long> xAxisValues = new ArrayList<>();
        int xAxisPosition = 0;
        for (String[] line : lines){
            xAxisValues.add(Long.valueOf(line[0]));
            xAxisPosition++;
            Entry entry = new Entry(
                    xAxisPosition,
                    Float.valueOf(line[1])
            );
            entries.add(entry);
        }
        LineData lineData = new LineData(new LineDataSet(entries, symbol));
        chart.setData(lineData);
        XAxis xAxis =  chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date(xAxisValues.get(xAxisValues.size()- (int)value - 1));
                return new SimpleDateFormat( "yyyy-MM-dd", Locale.ENGLISH)
                        .format(date);
            }
        });
        text.setText(symbol);
        return symbol;
    }
    @Nullable
    private List<String[]> getLines(String history){
        List<String[]> lines = new ArrayList<>();
        CSVReader reader = new CSVReader(new StringReader(history));
        try {
            lines.addAll(reader.readAll());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private String getHistory(String symbol) {
        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(symbol), null, null, null, null);
        String history = null;
        if(cursor != null){
            cursor.moveToFirst();
            history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
            cursor.close();
        }
        return history;
    }
}









