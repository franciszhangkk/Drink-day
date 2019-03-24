package comp5216.waterday;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FruitActivity extends AppCompatActivity {
    ListView listView;
    List<Map<String,Object>> datalist;
    SimpleAdapter simp_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);
        datalist = new ArrayList<Map<String,Object>>() ;
        simp_adapter = new SimpleAdapter(this,getdata(),R.layout.listview_fruit,new String[]{"pic","text"},new int[]{R.id.fruit_pic,R.id.fruit_infor});
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(simp_adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private List<Map<String,Object>> getdata(){
        String [] arr_fruit ={"3~5 ml / 0.1~0.2 oz","3~5 ml / 0.1~0.2 oz","80~100 ml / 2~3 oz","100~110 ml / 3~4 oz","100~110 ml / 3~4 oz","100~110 ml / 3~4 oz","100~110 ml / 3~4 oz"};
        int [] pic_fruit ={R.drawable.strawberry,R.drawable.cherry,R.drawable.kiwi,R.drawable.orange,R.drawable.pear,R.drawable.apple,R.drawable.banana};
        for (int i=0;i<7;i++){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("pic",pic_fruit[i]);
            map.put("text",arr_fruit[i]);
            datalist.add(map);
        }

        return  datalist;
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
