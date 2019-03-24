package comp5216.waterday;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by zekunzhang on 2017/10/23.
 */

public class TimeService extends Service {

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        DataChangeReceiver receiver=new DataChangeReceiver();
        registerReceiver(receiver,new IntentFilter(Intent.ACTION_TIME_TICK));

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }

}