package comp5216.waterday;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SportsActivity extends AppCompatActivity implements SensorEventListener, StepListener{
    private Button start;
    private Button end;
    private TextView textViewStep,textViewKCal;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private Chronometer timer;
    private static final String TEXT_NUM_STEPS = "";
    private int numSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        start = (Button) findViewById(R.id.startbutton);
        end = (Button) findViewById(R.id.stopbutton);
        textViewStep = (TextView) findViewById(R.id.numberSteps_label);
        textViewKCal = (TextView) findViewById(R.id.numberKcal_label);
        timer = (Chronometer) findViewById(R.id.timer);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);


    }

    public void startCount(View view) {
        numSteps = 0;
        sensorManager.registerListener(SportsActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        timer.start();

        start.setVisibility(View.INVISIBLE);
        end.setVisibility(View.VISIBLE);


    }

    public void stopCount(View view) {
        sensorManager.unregisterListener(SportsActivity.this);
        timer.stop();

        double Kcal = numSteps*0.1;
        double water = Kcal * 2;
        int need = (int) Math.ceil(water);

        Intent data = new Intent();
        data.putExtra("need",need);
        setResult(RESULT_OK, data);
        finish();


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        textViewStep.setText(TEXT_NUM_STEPS + numSteps);
        double a = numSteps*0.05;
        a=((int)(a*100))/100.0;
        textViewKCal.setText(a+"Kcal");
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
