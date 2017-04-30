package com.tester.llistsongs;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;


class ListItems {
    String items;
    String desc;
    String duration;
    String path;
    String arr;

    public ListItems(String items, String desc, String duration, String path, String arr) {
        this.desc = desc;
        this.items = items;
        this.duration = duration;
        this.path = path;
        this.arr = arr;
        return ;
    }
}

public class MainActivity extends Activity {



    final String TAG = "ErrorString####";

    public ArrayList<ListItems> itemsArr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
        }



        //File home = new File("/storage/");

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"Permission granted ####");
        }else{
            Log.i(TAG,"Permission Denied ####");
        }


        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }



        itemsArr = new ArrayList<ListItems>();
        getItemsList();


        ProgressBar spinner;
        spinner = (ProgressBar)findViewById(R.id.progressBar);



        //ListItems Ls[] = {new ListItems("used for prog","Laptop"),new ListItems("time keeping","watch")};
        ListItems[] items = itemsArr.toArray(new ListItems[0]);

        final ListAdapter la = new customAdapter(this, items);

        final ListView Lv = (ListView) findViewById(R.id.myListView);

        //setting up a new thread
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Lv.setAdapter(la);
            }
        };

        Thread myThread = new Thread(r);
        myThread.start();

        spinner.setVisibility(View.GONE);
        spinner.setVisibility(View.INVISIBLE);

        Lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItems path = (ListItems) adapterView.getItemAtPosition(i);
                Toast.makeText(MainActivity.this,path.desc,Toast.LENGTH_LONG).show();
                MediaPlayer mp = new MediaPlayer();
                try {

                    Uri trackUri = ContentUris.withAppendedId(
                            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            Long.valueOf(path.path));

                    mp.setDataSource(getApplicationContext(),trackUri);
                    //mp.setDataSource(path.path);
                }catch (IOException x ){
                    Log.i(TAG,"Exception handled"+x.toString());
                }

                try {
                    mp.prepare();
                }catch (IOException x ){
                    Log.i(TAG,"Exception handled"+x.toString());
                }
                Toast.makeText(MainActivity.this, String.valueOf(i),Toast.LENGTH_SHORT).show();
                mp.start();

            }
        });


    }

    private void getItemsList(){


        ArrayList<ListItems> songList = new ArrayList<ListItems>();

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null,null,null,null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            int a = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int b = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int c = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int d = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE);
            int e = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION);
            int f = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int g = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            Log.i("ALBUM ID: ",String.valueOf(f));


            do{
                long id = musicCursor.getLong(b);
                String title = musicCursor.getString(a);
                String artist = musicCursor.getString(c);

                String ringtone = musicCursor.getString(d);
                String notific = musicCursor.getString(e);
                String duration =  milliToMinutes(musicCursor.getString(g));
                String albumPath = "";
                if(ringtone.contains("0") && notific.contains("0")){


                    //album art retriever
                    Uri myUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                    Cursor myCursor = getContentResolver().query(myUri,new String[] {MediaStore.Audio.Albums._ID,
                                    MediaStore.Audio.Albums.ALBUM_ART},
                            MediaStore.Audio.Albums._ID+ "=?",
                            new String[] {musicCursor.getString(f)},
                            null);
                    Log.i("query stat: ","queried");
                    if(myCursor!=null && myCursor.moveToFirst()){

                        int x = myCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                        //do {

                            albumPath = myCursor.getString(x);
                        //}while(myCursor.moveToNext());
                    }
                    myCursor.close();
                    //end of album art retriever


                    Log.i("ringtone type: ",ringtone.getClass().getName());
                    Log.i("Song List: ", String.valueOf(id) + " - " + title + " - " + artist);
                    Log.i("Is ringtone: ",ringtone);

                    songList.add(new ListItems(title,artist,duration,String.valueOf(id),albumPath));
                    Log.i("Songs count: ", String.valueOf(songList.size()));



                }
            }while(musicCursor.moveToNext());
        }
        musicCursor.close();
        musicUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        musicCursor = musicResolver.query(musicUri,null,null,null,null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            int a = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int b = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int c = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int d = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE);
            int e = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION);
            int f = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int g = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            Log.i("ALBUM ID: ",String.valueOf(f));

            do{
                long id = musicCursor.getLong(b);
                String title = musicCursor.getString(a);
                String artist = musicCursor.getString(c);
                String duration = milliToMinutes(musicCursor.getString(g));
                String ringtone = musicCursor.getString(d);
                String notific = musicCursor.getString(e);
                String albumPath = "";

                if(ringtone.contains("0")&&notific.contains("0")) {

                    //album art retriever
                    Uri myUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                    Cursor myCursor = getContentResolver().query(myUri,new String[] {MediaStore.Audio.Albums._ID,
                                    MediaStore.Audio.Albums.ALBUM_ART},
                            MediaStore.Audio.Albums._ID+ "=?",
                            new String[] {musicCursor.getString(f)},
                            null);
                    Log.i("query stat: ","queried");
                    if(myCursor!=null && myCursor.moveToFirst()){

                        int x = myCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                        albumPath = myCursor.getString(x);

                    }
                    myCursor.close();
                    //end of album art retriever



                    Log.i("Song List: ", String.valueOf(id) + " - " + title + " - " + artist);
                    songList.add(new ListItems(title,artist,duration,String.valueOf(id),albumPath));
                    Log.i("Songs count: ", String.valueOf(songList.size()));
                }
            }while(musicCursor.moveToNext());
        }
        musicCursor.close();
        itemsArr = songList;

    }

    private String milliToMinutes(String duration){
        long length = Long.parseLong(duration);
        length = length/1000;
        String seconds;
        String mins;

        if((length%60)<10){
            seconds = "0"+String.valueOf(length%60);
        }else{
            seconds = String.valueOf(length%60);
        }

        if((length/60)<10){
            mins = "0"+String.valueOf(length/60);
        }else{
            mins = String.valueOf(length/60);
        }

        return mins+":"+seconds;
    }
}
