package com.app.zuluwaterways;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Callable;

//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    DatabaseHelper mDbHelper;
    Map<String, Drawable>  Symbols;
    //private FirebaseAuth mAuth;

    private final StringBuilder res = new StringBuilder();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final EditText editComment = findViewById(R.id.login_password);
        editComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;


                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editComment.getRight() - editComment.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                       // Log.d("clicked","Icon");
                       // Log.d("type",editComment.getInputType()+"");
                        editComment.setInputType(editComment.getInputType()==129?InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:129);
                        return true;
                    }
                }
                return false;
            }
        });
        editComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin(null);
                    v.clearFocus();
                    handled = true;
                }
                return handled;
            }
        });
        if(getIntent().hasExtra("needed")){
            findViewById(R.id.login_needed_text).setVisibility(View.VISIBLE);

        }
        //LoadPreferences();
        //mAuth = FirebaseAuth.getInstance();
        //GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
         //       .requestIdToken("1092550595258:android:160daa3b14af608121a883")
          //      .requestEmail()
          //      .build();
        //Log.d("name",gso.getAccount().name);
        //mAuth.signInAnonymously();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    public void doLogin(View view) {
       // Log.d("where am I?",view.getParent().toString());
       // Log.d(("am i here?"),((EditText)findViewById(R.id.login_email)).getText().toString());
        String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.login_password)).getText().toString();

        new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
            public String call() throws Exception {
                String result = res.toString();
                if (result.equals("")){

                    findViewById(R.id.login_response_text).setVisibility(View.VISIBLE);
                    return null;
                }
                JSONObject mainObject = new JSONObject(result);
               // Log.d("Result",mainObject.toString());
               // Log.d("Result",mainObject.getString("id"));
               // Log.d("Result",mainObject.getString("userId"));
               // Log.d("Result",mainObject.getJSONObject("user").getString("firstName"));
                String p = mainObject.has("user") && mainObject.getJSONObject("user").has("premuim")?
                        mainObject.getJSONObject("user").getString("premuim"):"";


                Intent intent = new Intent();
                intent.putExtra("token",mainObject.getString("id"));
                intent.putExtra("user",mainObject.getString("userId"));
                intent.putExtra("name",mainObject.getJSONObject("user").getString("firstName"));
                intent.putExtra("premuim",p);
                setResult(RESULT_OK, intent);
                finish();
                //Toast.makeText(this,"Download Compete",Toast.LENGTH_SHORT).show();
                return null;
            }
        }).execute("https://www.zuluwaterways.com/api/users/login?include=user&rememberMe=true","login",email,password);
        Log.d("@@@@@@@@@@@@@@",email+","+password);
    }

    public void doSignup(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zuluwaterways.com/register"));

        try {
            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            defaultBrowser.setData(browserIntent.getData());
            startActivity(defaultBrowser);
        }catch (Exception e){
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://www.zuluwaterways.com/register" ) );
            startActivity( browse );
        }

    }

    public void doReset(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zuluwaterways.com/forget-password"));

        try {
            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            defaultBrowser.setData(browserIntent.getData());
            startActivity(defaultBrowser);
        }catch (Exception e){
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://www.zuluwaterways.com/register" ) );
            startActivity( browse );
        }
    }


    public void goBack(View view) {
        onBackPressed();
    }
}
