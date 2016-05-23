package com.mycompany.tinynote;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private NoteDb mNoteDb;
    private Note mNote = new Note();
    private Button buttonWriteDone;
    private EditText noteTitle;
    private EditText noteContent;
    private TextView noteLocation;
    // Note year/month/location got from system
    private String title;
    private String content;
    private String year, month, day;
    //地理位置查询网址
    private String address;
    //位置经纬度
    private LocationManager mLocationManager;
    private String provider;
    private List<String> mProviderList;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_note);

        mNoteDb = NoteDb.getInstance(this);
        buttonWriteDone = (Button) findViewById(R.id.write_done);
        buttonWriteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get mNote title/content/...
                noteTitle = (EditText) findViewById(R.id.note_title);
                noteContent = (EditText) findViewById(R.id.note_content);
                title = noteTitle.getText().toString();
                content = noteContent.getText().toString();

                Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int d = c.get(Calendar.DAY_OF_MONTH);
                year = DateConvertor.formatYear(y);
                month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CHINA);
                day = DateConvertor.formatDay(d);
                Log.d("WriteNoteActivity", "current date is year: " + year +
                        "month: " + month + "day: " + day);
                // 保存日记到数据库
                mNote.setYear(year);
                mNote.setMonth(month);
                mNote.setTitle(title);
                mNote.setContent(content);
                mNote.setLoacation(noteLocation.getText().toString());
                mNote.setDate(year + month + day);
                mNoteDb.InsertNote(mNote);
                // 回到主活动
                Intent intent = new Intent(WriteNoteActivity.this, MainActivity.class);
                intent.putExtra("extra_noteYear", year);
                intent.putExtra("extra_noteMonth", month);
                startActivity(intent);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mProviderList = mLocationManager.getProviders(true);
            if (mProviderList.contains(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
            } else if (mProviderList.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(this, "No mLocation provider to use", Toast.LENGTH_LONG).show();
            }
            mLocation = mLocationManager.getLastKnownLocation(provider);
            mLocationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
            // get mNote location
            noteLocation = (TextView) findViewById(R.id.note_location);
            if (mLocation != null) {
                showLocation(mLocation);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(locationListener);
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
                Toast.makeText(WriteNoteActivity.this, "获取笔记位置失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("WriteNoteActivity", "PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION granted!");
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mProviderList = mLocationManager.getProviders(true);
            if (mProviderList.contains(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
            } else if (mProviderList.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(this, "No location provider to use", Toast.LENGTH_LONG).show();
            }
            mLocation = mLocationManager.getLastKnownLocation(provider);
            mLocationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
            // get mNote location
            noteLocation = (TextView) findViewById(R.id.note_location);
            if (mLocation != null) {
                showLocation(mLocation);
            }
        } else {
            // permission denied TODO
            Log.d("WriteNoteActivity", "PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION denied!");
        }
        return;
    }
}
