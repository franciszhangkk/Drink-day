package comp5216.waterday;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InforInputActivity extends AppCompatActivity {
    private TextView helloUser;
    private Spinner genderSpinner;
    private List<String> genderList;
    private ArrayAdapter<String> genderAdapter;
    private EditText ETage,ETheight,ETweight,ETout,EThome;
    private String name;
    private Map<String,Object> userMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        helloUser = (TextView)findViewById(R.id.hello_user);
        genderSpinner = (Spinner) findViewById(R.id.gender_spin);
        ETage = (EditText) findViewById(R.id.editText_age);
        ETheight = (EditText) findViewById(R.id.editText_height);
        ETweight = (EditText) findViewById(R.id.editText_weight);
        EThome = (EditText) findViewById(R.id.editText_homesize);
        ETout = (EditText) findViewById(R.id.editText_outsize);
        //set data source of spinner
        genderList = new ArrayList<String>();
        genderList.add("Male");
        genderList.add("Female");
        //adapter 设置样式
        genderAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        readUserFromFile();

        name = (String) userMap.get("name");
        if(name==null){
            helloUser.setText("Hello！Tailor your drinking plan");
        }else{
            helloUser.setText("Hello "+name);
        }



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





    public void Submit(View view) {
        String weight = ETweight.getText().toString();
        double Dweight = Double.parseDouble(weight);
        Dweight = Math.ceil(Dweight);

        String height = ETheight.getText().toString();
        double Dheight = Double.parseDouble(weight);
        Dheight = Math.ceil(Dheight);

        String age = ETage.getText().toString();
        double Dage = Double.parseDouble(age);
        Dage = Math.ceil(Dage);

        String homesize = EThome.getText().toString();
        double Dhome = Double.parseDouble(homesize);
        Dhome = Math.ceil(Dhome);

        String outsize = ETout.getText().toString();
        double Dout = Double.parseDouble(outsize);
        Dout = Math.ceil(Dout);

        String gender = genderSpinner.getSelectedItem().toString();

        userMap.put("homesize",Dhome);
        userMap.put("outsize",Dout);
        userMap.put("gender",gender);
        userMap.put("height",Dheight);
        userMap.put("weight",Dweight);
        userMap.put("age",Dage);
        saveItemsToFile();

        Intent intent = new Intent(InforInputActivity.this,DrinkActivity.class);
        setResult(RESULT_OK, intent);
        finish();


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
}
