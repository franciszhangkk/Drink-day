package comp5216.waterday;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class DrinkActivity extends AppCompatActivity {
    private CircleProgressBar circleProgressBar;
    private int totalProgress ;
    private int currentProgress;
    private List<Map<String,Object>> datalist;
    private int progress ;
    private int oldtotal;
    private int homesize,outsize,sipsize;
    private BoomMenuButton bmb;
    private BoomMenuButton leftBmb;
    private TextView needToDrink,tital;
    public final int SPORT_REQUEST_CODE = 666;
    public final int INFOR_REQUEST_CODE = 667;
    public final int SICK_REQUEST_CODE = 668;
    private Map<String, Object> userMap;
    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        Intent service=new Intent(this, TimeService.class);
        startService(service);
        setdata();

        readUserFromFile();

        //set drink size boomMenu
        needToDrink = (TextView) findViewById(R.id.textView_number);
        tital = (TextView) findViewById(R.id.textView_need);
        homesize = Integer.parseInt((String) userMap.get("homesize"));
        outsize = Integer.parseInt((String) userMap.get("outsize"));
        sipsize = 30;
        setdrink();

        //set action bar
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View actionBar = mInflater.inflate(R.layout.action_bar, null);
        TextView mTitleTextView = (TextView) actionBar.findViewById(R.id.title_text);
        mTitleTextView.setText(R.string.app_name);
        mActionBar.setCustomView(actionBar);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0,0);

        leftBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_left_bmb);
        setMenu();


        //set progressBar
        circleProgressBar = (CircleProgressBar) findViewById(R.id.circleProgressbar);
        int weight = Integer.parseInt((String) userMap.get("weight"));
        totalProgress = weight * 40;

        //set current progress
        getCurrentProgress();


        needToDrink.setText((totalProgress-currentProgress)+" ml water");

        circleProgressBar.setProgress(currentProgress);
        circleProgressBar.setTotalProgress(totalProgress);

        new Thread(new ProgressRunable()).start();

        contextOfApplication = getApplicationContext();


    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    public void getCurrentProgress(){

        readListFromFile();
        int listposition = datalist.size()-1;
        Map<String,Object> m = datalist.get(listposition);
        String s = (String)m.get("amount");
        int i = Integer.parseInt(s);
        currentProgress =i;
        s= (String)m.get("progress");
        i = Integer.parseInt(s);
        progress = i;

    }
    public void saveCurrentProgress(){
        int listposition = datalist.size()-1;
        Map<String,Object> m = datalist.get(listposition);
        m.put("amount",currentProgress);
        m.put("progress",currentProgress);
        datalist.set(listposition,m);
        saveListToFile();


    }

    public void setMenu(){
        leftBmb.setButtonEnum(ButtonEnum.Ham);
        leftBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_5);
        leftBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_5);

        HamButton.Builder menu1 = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent = new Intent(DrinkActivity.this,HistoryActivity.class);
                        startActivity(intent);

                    }
                })
                .containsSubText(true)
                .shadowEffect(true)
                .normalImageRes(R.drawable.history01)
                .normalText("Drink History")
                .subNormalText("Track your drink history in the past 7 days.")
                .normalColor(getColor(R.color.menu1));
        leftBmb.addBuilder(menu1);

        HamButton.Builder menu2 = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Intent intent = new Intent(DrinkActivity.this,FruitActivity.class);
                        startActivity(intent);

                    }
                })
                .containsSubText(true)
                .shadowEffect(true)
                .normalImageRes(R.drawable.strawberry)
                .normalText("Fruit")
                .subNormalText("The water content of normal fruit.")
                .normalColor(getColor(R.color.menu2));
        leftBmb.addBuilder(menu2);

        HamButton.Builder menu4 = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Intent intent =  new Intent(DrinkActivity.this,SportsActivity.class);
                        startActivityForResult(intent, SPORT_REQUEST_CODE);

                    }
                })
                .containsSubText(true)
                .shadowEffect(true)
                .normalImageRes(R.drawable.sport01)
                .normalText("Sport")
                .subNormalText("Water and the amount of exercise.")
                .normalColor(getColor(R.color.menu4));
        leftBmb.addBuilder(menu4);

        HamButton.Builder menu5 = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Intent intent =new Intent(DrinkActivity.this,InforInputActivity.class);
                        startActivityForResult(intent, INFOR_REQUEST_CODE);

                    }
                })
                .containsSubText(true)
                .shadowEffect(true)
                .normalImageRes(R.drawable.personal03)
                .normalText("Personal Information")
                .subNormalText("Modify your personal information.")
                .normalColor(getColor(R.color.menu5));
        leftBmb.addBuilder(menu5);

        HamButton.Builder menu6 = new HamButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Intent intent = new Intent(DrinkActivity.this,SickActivity.class);
                        startActivityForResult(intent,SICK_REQUEST_CODE);

                    }
                })
                .containsSubText(true)
                .shadowEffect(true)
                .normalImageRes(R.drawable.sick05)
                .normalText("Sick")
                .subNormalText("You need drink more when you are sick.")
                .normalColor(getColor(R.color.menu6));
        leftBmb.addBuilder(menu6);

    }

    public void drink(int water){
        getCurrentProgress();
        int temp = currentProgress;
        progress = temp;
        currentProgress = currentProgress + water;
        new Thread(new ProgressRunable()).start();
        if ((totalProgress-currentProgress)>0){
            needToDrink.setText((totalProgress-currentProgress)+" ml water");
        }else{
            tital.setText("Good!");
            needToDrink.setText("Target Achieve");
        }
        saveCurrentProgress();



    }


    public void setdrink(){
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        bmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
        bmb.setNormalColor(Color.WHITE);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_3);
        TextOutsideCircleButton.Builder builderhome = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        drink(homesize);

                    }
                })
                .normalImageRes(R.drawable.homecup)
                .normalText(homesize+"ml");
        bmb.addBuilder(builderhome);
        TextOutsideCircleButton.Builder builderout = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        drink(outsize);
                    }
                })
                .normalImageRes(R.drawable.outcup)
                .normalText(outsize+"ml");
        bmb.addBuilder(builderout);
        TextOutsideCircleButton.Builder buildersip = new TextOutsideCircleButton.Builder()
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        drink(sipsize);

                    }
                })
                .normalImageRes(R.drawable.drop)
                .normalText(sipsize+"ml")
                .normalColor(Color.WHITE);

        bmb.addBuilder(buildersip);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPORT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int need = data.getIntExtra("need",0);
                oldtotal =totalProgress;
                totalProgress = totalProgress + need;
                circleProgressBar.setTotalProgress(totalProgress);
                needToDrink.setText((totalProgress-currentProgress)+" ml water");
//                new Thread(new TextRunable1()).start();
                String toast = "You need drink "+ need+"ml water after sports";
                Toast t=Toast.makeText(DrinkActivity.this,toast,Toast.LENGTH_SHORT);
                showMyToast(t, 4500);


            }
        }
        if (requestCode == SICK_REQUEST_CODE){
            if(resultCode== RESULT_OK){
                int need = data.getIntExtra("need",0);
                oldtotal =totalProgress;
                totalProgress = totalProgress + need;
                circleProgressBar.setTotalProgress(totalProgress);
//                new Thread(new TextRunable2()).start();
                needToDrink.setText((totalProgress-currentProgress)+" ml water");
                String toast = "You need drink "+ need+"ml water. Hope you will get better soon.";
                Toast t=Toast.makeText(DrinkActivity.this,toast,Toast.LENGTH_SHORT);
                showMyToast(t, 4500);
            }
        }
        if (requestCode == INFOR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                readUserFromFile();
                int weight = Integer.parseInt((String) userMap.get("weight"));
                totalProgress = weight * 40;
                homesize = Integer.parseInt((String) userMap.get("homesize"));
                outsize = Integer.parseInt((String) userMap.get("outsize"));
                bmb.removeBuilder(0);
                bmb.removeBuilder(0);
                bmb.removeBuilder(0);
                setdrink();




                if((totalProgress - currentProgress)>0){
                    needToDrink.setText((totalProgress-currentProgress)+" ml water");
                    circleProgressBar.setTotalProgress(totalProgress);
                    circleProgressBar.setProgress(currentProgress);
                    progress = currentProgress;
                }else{
                    needToDrink.setText("You have achieve the target today");
                    circleProgressBar.setTotalProgress(totalProgress);
                    circleProgressBar.setProgress(totalProgress);
                    progress=totalProgress;
                }

            }
        }

    }

    class ProgressRunable implements Runnable {
        @Override
        public void run() {
            while (progress < currentProgress) {
                progress += 50;
                circleProgressBar.setProgress(progress);
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class TextRunable1 implements Runnable {
        @Override
        public void run() {
            while (oldtotal < totalProgress) {
                oldtotal += 1;
                needToDrink.setText((oldtotal-currentProgress)+" ml water");
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class TextRunable2 implements Runnable {
        @Override
        public void run() {
            while (oldtotal < totalProgress) {
                oldtotal += 10;
                needToDrink.setText((oldtotal-currentProgress)+" ml water");
                try {
                    Thread.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }

    private void readUserFromFile(){
        Map<String, Object> itemMap = new HashMap<String, Object>() ;
        SharedPreferences sp = this.getSharedPreferences("Drink", Context.MODE_PRIVATE);
        String result = sp.getString("UserMap", "");
        try {
            JSONObject object = new JSONObject(result);
            itemMap = new HashMap<String, Object>();
            JSONArray names = object.names();
            if (names != null) {
                for (int j = 0; j < names.length(); j++) {
                    String name = names.getString(j);
                    String value = object.getString(name);
                    itemMap.put(name, value);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        userMap = itemMap;

    }
    private void saveItemsToFile(){

        Iterator<Map.Entry<String, Object>> iterator = userMap.entrySet().iterator();

        JSONObject object = new JSONObject();

        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();

            try {
                object.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        SharedPreferences sp = this.getSharedPreferences("Drink", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("UserMap", object.toString());
        editor.commit();
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

        }
}
