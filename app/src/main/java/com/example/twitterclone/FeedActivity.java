package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> arrayList=new ArrayList<>();
    ArrayList<String> tweetsList=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitle(ParseUser.getCurrentUser().getUsername()+"'s Feed");
        listView=findViewById(R.id.feedList);
        Intent intent=getIntent();
        arrayList=intent.getStringArrayListExtra("followinglist");
        tweetsList.clear();
        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,tweetsList);
        listView.setAdapter(arrayAdapter);
        ParseQuery<ParseObject> parseQuery=ParseQuery.getQuery("TwitterTweets");
        parseQuery.whereContainedIn("username",arrayList);
        parseQuery.addDescendingOrder("createdAt");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    for(ParseObject obj:objects){
                        tweetsList.add(obj.getString("tweet")+"\n Posted By: "+obj.getString("username"));
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}