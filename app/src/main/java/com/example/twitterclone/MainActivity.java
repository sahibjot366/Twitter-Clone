package com.example.twitterclone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {
    AutoCompleteTextView username;
    EditText password;
    ImageView imageView;
    RelativeLayout relativeLayout;
    SharedPreferences sharedPreferences;
    ArrayList<String> loginUserAutoComplete=new ArrayList<>();
    ArrayAdapter<String> loginuserarrayadapter;
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.relativeLayout_Main || v.getId()==R.id.imageViewLoginSignup){
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(v.getId()==R.id.passwordText){
            if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
                logInOrSignup(v);
            }
        }
        else if(v.getId()==R.id.usernameText){
            if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
                password.findFocus();
            }
        }

        return false;
    }

    public void UsernameScreen(){
        Intent intent=new Intent(getApplicationContext(),FollowingActivity.class);
        startActivity(intent);
    }
    public void logInOrSignup(View view){
        String User=username.getText().toString();
        String Password=password.getText().toString();
        ParseQuery<ParseUser> query=ParseUser.getQuery();
        query.whereEqualTo("username",User);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null){
                    if(objects.size()==0){
                        ParseUser user=new ParseUser();
                        user.setUsername(User);
                        user.setPassword(Password);
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Toast.makeText(MainActivity.this, "Signed up succesfully", Toast.LENGTH_SHORT).show();
                                    UsernameScreen();
                                }else
                                    Toast.makeText(MainActivity.this, "Error in sign up", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        ParseUser.logInInBackground(User, Password, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if(e==null){
                                    Toast.makeText(MainActivity.this, "Logged in succesfully", Toast.LENGTH_SHORT).show();
                                    UsernameScreen();
                                }
                                else
                                    Toast.makeText(MainActivity.this, "Error in log in", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (!loginUserAutoComplete.contains(User)) {
                        loginUserAutoComplete.add(User);
                        loginuserarrayadapter.notifyDataSetChanged();
                        try {
                            sharedPreferences.edit().putString("loginusers",ObjectSerializer.serialize(loginUserAutoComplete)).apply();
                        } catch (Exception ioException) {
                            ioException.printStackTrace();
                        }
                    }

                }
                else
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Twitter Login/Signup");
        getSupportActionBar().hide();
        username=findViewById(R.id.usernameText);
        password=findViewById(R.id.passwordText);
        imageView=findViewById(R.id.imageViewLoginSignup);
        relativeLayout=findViewById(R.id.relativeLayout_Main);
        sharedPreferences=getSharedPreferences("com.example.twitterclone", Context.MODE_PRIVATE);
        try {
            loginUserAutoComplete=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("loginusers",ObjectSerializer.serialize(new ArrayList<String>())));
            loginuserarrayadapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,loginUserAutoComplete);
            username.setAdapter(loginuserarrayadapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        username.setOnKeyListener(this);
        imageView.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        password.setOnKeyListener(this);
        if(ParseUser.getCurrentUser()!=null)
            UsernameScreen();

    }
}