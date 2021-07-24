package com.example.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends AppCompatActivity {
    ListView followingList;
    ArrayList<String> userArrayList=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ConstraintLayout mylayout;
    ArrayList<String> followingUsersList=new ArrayList<>();
//    SharedPreferences sharedPreferences;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.tweet:
                AlertDialog.Builder builder=new AlertDialog.Builder(FollowingActivity.this);
                builder.setTitle("Tweet");
                builder.setMessage("Enter your tweet : ");
                EditText editText=new EditText(this);
                editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                builder.setView(editText);
                builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseObject parseObject=new ParseObject("TwitterTweets");
                        parseObject.put("username",ParseUser.getCurrentUser().getUsername());
                        parseObject.put("tweet",editText.getText().toString());
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Toast.makeText(FollowingActivity.this, "Tweet Posted!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(FollowingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel",null);
                builder.show();
                break;
            case R.id.feed:
                int length=followingList.getCount();
                SparseBooleanArray checked=followingList.getCheckedItemPositions();
                followingUsersList.clear();
                for(int i=0;i<length;i++){
                    if(checked.get(i)){
                        followingUsersList.add(userArrayList.get(i));
                    }
                }
                Intent intent0=new Intent(getApplicationContext(),FeedActivity.class);
                intent0.putExtra("followinglist",followingUsersList);
                startActivity(intent0);
                break;
            case R.id.logout:
                ParseUser.logOut();
                Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        setTitle("Follow Users and View Feed");
//        sharedPreferences=getSharedPreferences("com.example.twitterclone", Context.MODE_PRIVATE);
        followingList=findViewById(R.id.followingList);
        mylayout=findViewById(R.id.mylayout);
        followingList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked,userArrayList);
        followingList.setAdapter(arrayAdapter);
        followingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView=(CheckedTextView)view;
                if(checkedTextView.isChecked()){
                    Toast.makeText(FollowingActivity.this, "Following", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(FollowingActivity.this, "Unfollowing", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ParseQuery<ParseUser> query=ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for(ParseUser obj:objects){
                        userArrayList.add(obj.getUsername());
                    }
                    arrayAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(FollowingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}