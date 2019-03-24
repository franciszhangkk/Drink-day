package comp5216.waterday;

/**
 * Created by zekunzhang on 2017/10/23.
 */

import android.content.BroadcastReceiver;
import android.content.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DataChangeReceiver extends BroadcastReceiver{
    private List<Map<String,Object>> datalist;
    public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat sdf1 = new SimpleDateFormat("MM.dd");
    private Context applicationContext;

        @Override
    public void onReceive(Context context, Intent intent) {
            applicationContext = DrinkActivity.getContextOfApplication();

        Log.i("liujun", "时间发生变化。。。");
            Date date = new Date();
            String now = sdf.format(date);
            String newday = "00:00:00";
            if (now.equals(newday)){
                readListFromFile();
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("amount",0);
                map.put("date",sdf1.format(date));
                map.put("progress",0);
                datalist.add(map);
                saveListToFile();
            }


    }
    private void readListFromFile(){


        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("list", mJsonArray.toString());
        editor.commit();
    }


}
