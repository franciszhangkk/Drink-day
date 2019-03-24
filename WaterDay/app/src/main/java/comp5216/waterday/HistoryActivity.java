package comp5216.waterday;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    BarChart barChart;
    List<Map<String,Object>> datalist;
//    ImageView imv1,imv2,imv3;
    TextView days,totalmount,avgamount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        imv1 = (ImageView) findViewById(R.id.imageView3);
//        imv2 = (ImageView) findViewById(R.id.imageView4);
//        imv3 = (ImageView) findViewById(R.id.imageView5);
//        imv1.setImageDrawable(getDrawable());
        days = (TextView) findViewById(R.id.textdays);
        totalmount = (TextView) findViewById(R.id.texttotal);
        avgamount = (TextView)findViewById(R.id.textaverage);

        barChart = (BarChart) findViewById(R.id.barchart);
        setdata();



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setdata(){
        String [] datelist = new  String[]{"10.15","10.16","10.17","10.18","10.19","10.20","10.21","10.22","10.23"};
        int [] drinklist = new int []{1000,2000,1500,500,2400,1200,1600,1800,0};
        datalist = new ArrayList<Map<String,Object>>() ;
        readListFromFile();
        if (datalist.size()<9){
            for (int i=0; i<9;i++){
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("amount",drinklist[i]);
                map.put("date",datelist[i]);
                map.put("progress",0);
                datalist.add(map);
            }
        }
        saveListToFile();
        readListFromFile();

        for (Map<String,Object> m :datalist){
            String s = (String)m.get("amount");
            int i = Integer.parseInt(s);
            m.put("amount",i);

        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = (datalist.size() - 7); i<datalist.size(); i++){
            int amount = (int) datalist.get(i). get("amount");

            BarEntry be = new BarEntry(amount,(i+7-datalist.size()));
            entries.add(be);

        }


        ArrayList<String> labels = new ArrayList<String>();
        for (int j = (datalist.size() - 7) ; j < datalist.size();j++ ){
            String date = (String) datalist.get(j).get("date");
            labels.add(date);
        }

        BarDataSet bardataset = new BarDataSet(entries, "Drink Amount");

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateY(2000);


        int totaldays = datalist.size();
        int totalAmountValue = 0;
        for(Map<String,Object> m:datalist){
            int value = (int) m.get("amount");
            totalAmountValue = totalAmountValue + value;
        }
        double totalAmountUse = totalAmountValue/1000;
        String s = totalAmountUse+"L";
        int avgValue = (int) Math.floor(totalAmountValue /totaldays);

        days.setText(Integer.toString(totaldays));
        totalmount.setText(s);
        avgamount.setText(Integer.toString(avgValue)+"ml");

    }
    private void readListFromFile(){


        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        SharedPreferences sp = this.getSharedPreferences("history", Context.MODE_PRIVATE);
        String result = sp.getString("list", "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Map<String, Object> itemMap = new HashMap<String, Object>();
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        datalist = datas;

    }
    private void saveListToFile(){
        JSONArray mJsonArray = new JSONArray();
        for (int i = 0; i < datalist.size(); i++) {
            Map<String, Object> itemMap = datalist.get(i);
            Iterator<Map.Entry<String, Object>> iterator = itemMap.entrySet().iterator();

            JSONObject object = new JSONObject();

            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();

                try {
                    object.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mJsonArray.put(object);
        }

        SharedPreferences sp = this.getSharedPreferences("history", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("list", mJsonArray.toString());
        editor.commit();
    }
}
