package com.mycompany.tinynote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.tinynote.db.NoteDb;
import com.mycompany.tinynote.model.Note;
import com.mycompany.tinynote.util.DateConvertor;
import com.mycompany.tinynote.util.HttpCallbackListener;
import com.mycompany.tinynote.util.HttpUtil;
import com.mycompany.tinynote.util.Utility;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by lishaowei on 15/10/4.
 */
public class WriteNoteActivity extends Activity {

    //private MyDatabaseHelper dbHelper;
    private NoteDb noteDb;
    private Note note = new Note();
    private Button buttonWriteDone;
    // 笔记title
    private EditText noteTitle;
    // note content
    private EditText noteContent;
    // note location
    private TextView noteLocation;
    // note year/month/location got from system
    private String title;
    private String content;
    private String year,month,day;
    //地理位置查询网址
    private String address;
    //位置经纬度
    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_note);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_LONG).show();
        }
        if (provider != null) {
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
            // get note location
            noteLocation = (TextView) findViewById(R.id.note_location);
            if (location != null) {
                showLocation(location);
            }
        }

        noteDb = NoteDb.getInstance(this);
        buttonWriteDone = (Button) findViewById(R.id.write_done);
        buttonWriteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get note title/content/...
                noteTitle = (EditText) findViewById(R.id.note_title);
                noteContent = (EditText) findViewById(R.id.note_content);
                title = noteTitle.getText().toString();
                content = noteContent.getText().toString();

                Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);

                //year = c.getDisplayName(Calendar.YEAR, Calendar.LONG, Locale.CHINA);
                year = DateConvertor.formatYear(y);
                month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CHINA);
                //day = c.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.CHINA);
                day = DateConvertor.formatDay(d);
                Log.d("WriteNoteActivity", "current date is year: " + year +
                        "month: " + month + "day: " + day);
                // 保存日记到数据库
                note.setYear(year);
                note.setMonth(month);
                note.setTitle(title);
                note.setContent(content);
                note.setLoacation("湖北武汉市");
                note.setDate(year + month + day);
                noteDb.InsertNote(note);
                // 回到主活动
                Intent intent = new Intent(WriteNoteActivity.this, MainActivity.class);
                intent.putExtra("extra_noteYear", year);
                intent.putExtra("extra_noteMonth", month);
                startActivity(intent);
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void showLocation(Location location) {

        address = "http://api.map.baidu.com/geocoder/v2/?ak=61f8bd72d68aef3a7b66537761d29d82&callback=renderReverse&location="
                + location.getLatitude() + "," + location.getLongitude() + "&output=json&pois=0";
        // 静态方法，可不用new对象，直接引用
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                final String mlocation = Utility.handleLocationResponse(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noteLocation.setText(mlocation);
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

}
