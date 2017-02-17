package com.ggccnu.tinynote.view;

import android.app.Application;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.db.NoteDbInstance;
import com.ggccnu.tinynote.model.BmobNote;
import com.ggccnu.tinynote.model.Note;
import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.xiaomi.mistatistic.sdk.URLStatsRecorder;
import com.xiaomi.mistatistic.sdk.controller.HttpEventFilter;
import com.xiaomi.mistatistic.sdk.data.HttpEvent;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by lishaowei on 2017/1/31.
 */

public class SplashActivity extends Application {

    private String appID = "2882303761517476332";
    private String appKey = "5721747680332";

    private NoteDbInstance mNoteDbInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        MistatInit();

        Bmob.initialize(this, "28f7f6c4714b277721a96a64c7bf8204");
        BmobUpdateAgent.update(this);

        SqlDatabaseUpgrade();
        SyncNoteTask();
    }

    private void SyncNoteTask() {
        final List<Note> localNoteList = mNoteDbInstance.QueryAllNote();

        BmobQuery<BmobNote> query = new BmobQuery<BmobNote>();
        //query.addWhereExists("id");
        query.findObjects(getApplicationContext(), new FindListener<BmobNote>() {
            @Override
            public void onSuccess(List<BmobNote> list) {
                int localNoteCnt = localNoteList.size();
                int bmobNoteCnt = list.size();
                if (localNoteCnt > 0 && bmobNoteCnt == 0) {
                    uploadNote2Bmob(localNoteList);
                }
                if (localNoteCnt == 0 && bmobNoteCnt > 0) {
                    saveBmobNote2Local(list);
                }
                if (localNoteCnt != 0 && bmobNoteCnt != 0) {
                    if (localNoteCnt > bmobNoteCnt) {
                        uploadSomeNote2Bmob(localNoteList, list);
                    }else if (localNoteCnt < bmobNoteCnt) {
                        deleteBmobNote(list, localNoteList);
                    } else {
                        LogUtils.i("local and bmob note are same");
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.d("bmob query note id error: \n" + s);
            }
        });

    }

    /**
     *
     * @param bmobNoteList 云上笔记多
     * @param localNoteList 本地笔记少
     */
    private void saveSomeNote2Local(List<BmobNote> bmobNoteList, List<Note> localNoteList) {
        List<BmobNote> needSave2LocalBmobNoteList = new ArrayList<>();

        // 遍历云上的笔记，看是否能在本地找到
        for (int bmobNoteIndex = 0; bmobNoteIndex < bmobNoteList.size(); bmobNoteIndex++) {
            int bmobNoteId = bmobNoteList.get(bmobNoteIndex).getCmpId();
            Boolean hasLocalSaved = false;
            for (int localNoteIndex = 0; localNoteIndex < localNoteList.size(); localNoteIndex++) {
                if (bmobNoteId == localNoteList.get(localNoteIndex).getCmpId()) {
                    hasLocalSaved = true;
                    break;
                }
            }
            // 本地没有发现这条笔记
            if (!hasLocalSaved) {
                needSave2LocalBmobNoteList.add(bmobNoteList.get(bmobNoteIndex));
            }
        }
        saveBmobNote2Local(needSave2LocalBmobNoteList);
    }

    /**
     *
     * @param bmobNoteList 云上笔记多
     * @param localNoteList 本地笔记少
     */
    private void deleteBmobNote(List<BmobNote> bmobNoteList, List<Note> localNoteList) {
        List<BmobObject> needDeleteBmobNoteList = new ArrayList<>();

        // 遍历云上的笔记，看是否能在本地找到
        for (int bmobNoteIndex = 0; bmobNoteIndex < bmobNoteList.size(); bmobNoteIndex++) {
            int bmobNoteId = bmobNoteList.get(bmobNoteIndex).getCmpId();
            Boolean hasLocalSaved = false;
            for (int localNoteIndex = 0; localNoteIndex < localNoteList.size(); localNoteIndex++) {
                if (bmobNoteId == localNoteList.get(localNoteIndex).getCmpId()) {
                    hasLocalSaved = true;
                    break;
                }
            }
            // 本地没有发现这条笔记
            if (!hasLocalSaved) {
                needDeleteBmobNoteList.add(bmobNoteList.get(bmobNoteIndex));
            }
        }
        new BmobObject().deleteBatch(getApplicationContext(), needDeleteBmobNoteList, new DeleteListener() {
            @Override
            public void onSuccess() {
                LogUtils.i("sync note,delete bmob note success");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.e("sync note,delete bmob note failed:\n" + s);
            }
        });
    }

    private void uploadSomeNote2Bmob(List<Note> localNoteList, List<BmobNote> bmobNoteList) {
        // find diff id of note
        List<Note> needUploadLocalNoteList = searchNeedUploadLocalNote(localNoteList, bmobNoteList);
        uploadNote2Bmob(needUploadLocalNoteList);
    }

    /**
     *
     * @param localNoteList 笔记数量多
     * @param bmobNoteList  笔记数量少
     * @return 笔记差异
     */
    private List<Note> searchNeedUploadLocalNote(List<Note> localNoteList, List<BmobNote> bmobNoteList) {
        List<Note> needUploadLocalNoteList = new ArrayList<>();
        // 遍历本地的所有笔记，看是否能在云上找到
        for (int localNoteIndex = 0; localNoteIndex < localNoteList.size(); localNoteIndex++) {
            int localNoteId = localNoteList.get(localNoteIndex).getCmpId();
            Boolean hasModifieded = false;

            for (int bmobNoteIndex = 0; bmobNoteIndex < bmobNoteList.size(); bmobNoteIndex++) {
                // 本地的笔记在云上找到了
                if (localNoteId == bmobNoteList.get(bmobNoteIndex).getCmpId()) {
                    hasModifieded = true;
                    break;
                }
            }
            // 遍历云上的所有笔记，都没有发现本地的这条笔记
            if (!hasModifieded) {
                needUploadLocalNoteList.add(localNoteList.get(localNoteIndex));
            }
        }
        return needUploadLocalNoteList;
    }

    private void saveBmobNote2Local(List<BmobNote> list) {
        for (int i = 0; i < list.size(); i++) {
            BmobNote bmobNote = list.get(i);
            Note note = new Note();
            note.setCmpId(bmobNote.getCmpId());
            note.setYear(bmobNote.getYear());
            note.setMonth(bmobNote.getMonth());
            note.setDate(bmobNote.getDate());
            note.setTitle(bmobNote.getTitle());
            note.setContent(bmobNote.getContent());
            note.setLoacation(bmobNote.getLoacation());
            note.setHasModified(1);
            mNoteDbInstance.InsertNote(note);
        }
    }

    private void uploadNote2Bmob(List<Note> localNoteList) {
        List<BmobObject> bmobNoteList = new ArrayList<>();
        for (int i = 0; i < localNoteList.size() ; i++) {
            Note note = localNoteList.get(i);
            bmobNoteList.add(new BmobNote(note.getContent(), note.getDate(),
                    note.getHasModified(), note.getCmpId(), note.getLoacation(),
                    note.getMonth(), note.getTitle(), note.getYear()));
        }
        new BmobObject().insertBatch(getApplicationContext(), bmobNoteList, new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtils.i("upload all local note to bmob success");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.e("upload all local note to bmob failure");
            }
        });
    }

    private void SqlDatabaseUpgrade() {
        mNoteDbInstance = NoteDbInstance.getInstance(this);
        //mNoteDbInstance.addCmpId2NoteDb();
    }

    private void MistatInit() {
        // regular stats.
        MiStatInterface.initialize(this.getApplicationContext(), appID, appKey,
                "mi");
        MiStatInterface.setUploadPolicy(
                MiStatInterface.UPLOAD_POLICY_REALTIME, 0);
        MiStatInterface.enableLog();

        // enable exception catcher.
        MiStatInterface.enableExceptionCatcher(true);

        // enable network monitor
        URLStatsRecorder.enableAutoRecord();
        URLStatsRecorder.setEventFilter(new HttpEventFilter() {

            @Override
            public HttpEvent onEvent(HttpEvent event) {
                try {
                    LogUtils.d("MI_STAT", event.getUrl() + " result =" + event.toJSON());
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtils.e("MI_STAT", "event null");
                }
                // returns null if you want to drop this event.
                // you can modify it here too.
                return event;
            }
        });

        LogUtils.d("MI_STAT", MiStatInterface.getDeviceID(this) + " is the device.");
    }
}
