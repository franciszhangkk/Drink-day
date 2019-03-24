package comp5216.waterday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LogActivity extends Activity implements View.OnClickListener {
    private static String TAG = "FacebookLoginDemo";

    private Button mFbLoginBtn,goBtn;
    private String facebook_id;
    private String facebook_name;
    private String imageUrl;
    private CallbackManager callbackManager;
    private ImageView back;
    private Map<String, Object> userMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initViews();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        goBtn = (Button) findViewById(R.id.go_btn);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "comp5216.waterday",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        if (isLoggedIn()){
            goBtn.setVisibility(View.VISIBLE);
        }

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LogActivity.this, "facebook_account_oauth_Success", Toast.LENGTH_SHORT);
                AccessToken accessToken = loginResult.getAccessToken();
                boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
                Profile profile = Profile.getCurrentProfile();
                if (enableButtons && profile != null) {

                    facebook_id = profile.getId();
                    facebook_name = profile.getName();
                }
                userMap = new HashMap<String, Object>();

                userMap.put("id",facebook_id);
                userMap.put("name",facebook_name);
                userMap.put("homesize",300);
                userMap.put("outsize",500);
                userMap.put("gender","male");
                userMap.put("height",175);
                userMap.put("weight",65);
                saveItemsToFile();



                Intent intent = new Intent(LogActivity.this,DrinkActivity.class);
                startActivity(intent);


                Log.e(TAG, "token: " + loginResult.getAccessToken().getToken());
                //TODO：got the token，Notify server，and do something
            }

            @Override
            public void onCancel() {
                Toast.makeText(LogActivity.this, "facebook_account_oauth_Cancel", Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LogActivity.this, "facebook_account_oauth_Error", Toast.LENGTH_SHORT);
                Log.e(TAG, "e: " + e);
            }
        });
    }

    private void initViews() {
        mFbLoginBtn = (Button) findViewById(R.id.login_button);
        mFbLoginBtn.setOnClickListener(this);
        back =(ImageView) findViewById(R.id.imageView);
        back.setImageResource(R.drawable.drinkup);

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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) {
            if (isLoggedIn()){
                goBtn.setVisibility(View.INVISIBLE);
            }
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken == null || accessToken.isExpired()) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
            }

        }

    }

    public void gotomain(View view) {
        Intent intent = new Intent(LogActivity.this,DrinkActivity.class);
        startActivity(intent);
    }
}
