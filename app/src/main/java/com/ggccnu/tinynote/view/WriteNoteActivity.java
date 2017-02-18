package com.ggccnu.tinynote.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.R;
import com.ggccnu.tinynote.db.NoteDbInstance;
import com.ggccnu.tinynote.model.BmobNote;
import com.ggccnu.tinynote.model.Note;
import com.ggccnu.tinynote.util.BaiduLocationDecode;
import com.ggccnu.tinynote.util.DateConvertor;
import com.ggccnu.tinynote.util.HttpCallbackListener;
import com.ggccnu.tinynote.util.HttpUtil;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lishaowei on 15/10/4.
 */
public class WriteNoteActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final String LAST_UNSAVED_NOTE = "lastUnsavedNote";
    private NoteDbInstance mNoteDbInstance;
    private Note mNote = new Note();
    private EditText etTitle;
    private EditText etContent;
    private EditText etLocation;
    // Note year/month/location got from system
    private String year, month, day;
    private String title, content;
    //位置经纬度
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_note);

        etTitle = (EditText) findViewById(R.id.note_title);
        etContent = (EditText) findViewById(R.id.note_content);
        restoreLastUnsavedNote();

        mNoteDbInstance = NoteDbInstance.getInstance(this);
        etLocation = (EditText) findViewById(R.id.note_location);
        Button btnWriteDone = (Button) findViewById(R.id.write_done);
        btnWriteDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get mNote title/content/...
                title = etTitle.getText().toString();
                content = etContent.getText().toString();

                Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int d = c.get(Calendar.DAY_OF_MONTH);
                year = DateConvertor.formatYear(y);
                month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CHINA);
                day = DateConvertor.formatDay(d);

                // 笔记标题和内容为空
                if (TextUtils.isEmpty(content.trim()) && TextUtils.isEmpty(title.trim())) {
                    finish();
                } else {
                    saveNoteToDB();
                    // 刷新新笔记的用于比较的ID
                    mNoteDbInstance.AddCmpId2NewestNote();
                    mNote.setCmpId(mNoteDbInstance.getCmpIdOfNewestNote());
                    uploadNote2Bmob(mNote);
                    saveLastUnsavedNoteToSharedPreferences("", "");
                    // 回到主活动
                    Intent intent = new Intent(WriteNoteActivity.this, NoteTitleActivity.class);
                    intent.putExtra("extra_noteYear", year);
                    intent.putExtra("extra_noteMonth", month);
                    startActivity(intent);
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            getLocationFromLocationManager();
        }
    }

    private void restoreLastUnsavedNote() {
        title = restoreStringFromSharedPreferences("title");
        content = restoreStringFromSharedPreferences("content");
        if (!title.equals("") || !content.equals("")) {
            etTitle.setText(title);
            etTitle.setSelection(title.length());
            etContent.setText(content);
            etContent.setSelection(content.length());
        }
    }

    private String restoreStringFromSharedPreferences(String key) {
        SharedPreferences pref = getSharedPreferences(LAST_UNSAVED_NOTE, MODE_PRIVATE);
        return pref.getString(key, "");
    }

    private void saveNoteToDB() {
        LogUtils.d("WriteNoteActivity", "current date is year: " + year +
                "month: " + month + "day: " + day);
        // 保存日记到数据库
        mNote.setYear(year);
        mNote.setMonth(month);
        if (TextUtils.isEmpty(title.trim())) {
            mNote.setTitle(day);
        } else {
            mNote.setTitle(title);
        }
        if (TextUtils.isEmpty(content.trim())) {
            mNote.setContent(" ");
        } else {
            mNote.setContent(content);
        }
        mNote.setLocation(etLocation.getText().toString());
        mNote.setDate(year + month + day);
        mNoteDbInstance.InsertNote(mNote);
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
            setLocationFromHttp(location);
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

    private void setLocationFromHttp(Location location) {
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=a3U3IGBFNBRL48WszyW1WvFdw8Og7ilk&callback=renderReverse&location="
                + location.getLatitude() + "," + location.getLongitude() + "&output=json&pois=0";
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                final String mLocation = BaiduLocationDecode.locationFromResponse(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!"".equals(mLocation)) {
                            etLocation.setText(mLocation);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                MiStatInterface.recordCountEvent("http request GPS error", "");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // grantResults长度可能为0
        if (grantResults.length != 0) {
            if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogUtils.d("WriteNoteActivity", "PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION granted!");

                Location location = getLocationFromLocationManager();
                if (location != null) {
                    setLocationFromHttp(location);
                }
            } else {
                // permission denied TODO
                LogUtils.d("WriteNoteActivity", "PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION denied!");
            }
        }
    }

    private Location getLocationFromLocationManager() {
        Location location;
        String locationProvider;

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> mProviderList = mLocationManager.getProviders(true);

        if (mProviderList.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (mProviderList.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_LONG).show();
            locationProvider = null;
        }
        if (locationProvider != null) {
            location = mLocationManager.getLastKnownLocation(locationProvider);
            mLocationManager.requestLocationUpdates(locationProvider, 5000, 1, locationListener);

            return location;
        } // TODO: colletct gps failed
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(this, "write note page");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        if (!TextUtils.isEmpty(content.trim()) || !TextUtils.isEmpty(title.trim())) {
            saveLastUnsavedNoteToSharedPreferences(title, content);
        }
    }

    private void saveLastUnsavedNoteToSharedPreferences(String title, String content) {
        SharedPreferences.Editor editor = getSharedPreferences(LAST_UNSAVED_NOTE, MODE_PRIVATE).edit();
        editor.putString("title", title);
        editor.putString("content", content);
        editor.apply();
    }

    private void uploadNote2Bmob(Note note) {
        BmobNote bmobNote = localNote2BmobNote(note);
        bmobNote.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtils.i("new note uploaded to bmob success");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.e("new note uploaded to bmob failure: \n" + s);
            }
        });

    }

    private BmobNote localNote2BmobNote(Note note) {
        BmobNote bmobNote = new BmobNote();
        bmobNote.setYear(note.getYear());
        bmobNote.setMonth(note.getMonth());
        bmobNote.setDate(note.getDate());
        bmobNote.setLocation(note.getLocation());
        bmobNote.setTitle(note.getTitle());
        bmobNote.setContent(note.getContent());
        bmobNote.setHasModified(0);
        bmobNote.setCmpId(note.getCmpId());
        return bmobNote;
    }
}
