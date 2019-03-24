package comp5216.waterday;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SickActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner sickSpinner;
    private TextView TVneed;
    private EditText ETweight;
    private Map<String,Object> userMap;
    private ArrayAdapter<String> sickAdapter;
    private List<String> sickList;
    private String Sweight;
    private int userWeight;
    private int po = 1 ;
    private int need;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sick);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TVneed = (TextView) findViewById(R.id.TextView_waterNumber);
        ETweight =(EditText) findViewById(R.id.editText_weight1);
        ETweight.addTextChangedListener(new EditChangedListener());
        sickSpinner =(Spinner) findViewById(R.id.sick_spin);

        sickList = new ArrayList<String>();
        sickList.add("fever:<38°C");
        sickList.add("fever:<38°C-39°C");
        sickList.add("fever:>39°C");
        sickList.add("diarrhea");

        sickAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sickList);
        sickAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sickSpinner.setAdapter(sickAdapter);

        readUserFromFile();

        Sweight = (String) userMap.get("weight");
        userWeight = Integer.parseInt(Sweight);

        //spinner listener
        sickSpinner.setOnItemSelectedListener(this);
        updateTextView();

    }

    public void updateTextView (){
        int nowWeight;
        String SNWeight = ETweight.getText().toString();
        if (SNWeight.length()<1){
            nowWeight = 0;
        }else {
            double dweight = Double.parseDouble(SNWeight);
            dweight = Math.ceil(dweight);
            nowWeight = (int) dweight;
        }



        switch (po){
            case 0:
                need = nowWeight *15 ;
                break;
            case 1:
                need = nowWeight * 25;
                break;
            case 2:
                need = nowWeight * 40;
                break;
            case 3:
                if (userWeight>nowWeight){
                    if ((userWeight-nowWeight)>3){
                        need = userWeight * 100;
                    }else {
                        need = userWeight *50;
                    }
                }else {
                    need =userWeight*50;
                }
        }
        TVneed.setText(need+"ml");


    }


    public void Submit(View view) {
        Intent data = new Intent();
        data.putExtra("need",need);
        setResult(RESULT_OK, data);
        finish();

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


class EditChangedListener implements TextWatcher {


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateTextView();


    }

    @Override
    public void afterTextChanged(Editable s) {
        updateTextView();

        }

    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        po = position;
        updateTextView();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

