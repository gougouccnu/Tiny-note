package com.ggccnu.tinynote.util;

import android.content.Context;

import com.blankj.utilcode.utils.LogUtils;
import com.ggccnu.tinynote.db.NoteDbInstance;
import com.ggccnu.tinynote.model.BmobNote;
import com.ggccnu.tinynote.model.Note;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lishaowei on 2017/2/19.
 */

public class SyncNoteUtil {

    public static void SyncNoteTask(final Context context, final BmobUser user) {

        final NoteDbInstance mNoteDbInstance = NoteDbInstance.getInstance(context);

        final List<Note> localNoteList = mNoteDbInstance.QueryAllNote();

        BmobQuery<BmobNote> query = new BmobQuery<BmobNote>();
        query.addWhereEqualTo("user", user);
        query.findObjects(context, new FindListener<BmobNote>() {
            @Override
            public void onSuccess(List<BmobNote> list) {
                int localNoteCnt = localNoteList.size();
                int bmobNoteCnt = list.size();
                if (localNoteCnt > 0 && bmobNoteCnt == 0) {
                    uploadNote2Bmob(context, user, localNoteList);
                }
                if (localNoteCnt == 2 && bmobNoteCnt > 2) { // 默认有2条笔记
                    saveBmobNote2Local(list, mNoteDbInstance);
                } else if (localNoteCnt != 0 && bmobNoteCnt != 0) {
                    if (localNoteCnt > bmobNoteCnt) {
                        uploadSomeNote2Bmob(context, user,localNoteList, list);
                    }else if (localNoteCnt < bmobNoteCnt) {
                        deleteBmobNote(context, list, localNoteList);
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
    static void saveSomeNote2Local(List<BmobNote> bmobNoteList, List<Note> localNoteList) {
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
        //saveBmobNote2Local(needSave2LocalBmobNoteList, mNoteDbInstance);
    }

    /**
     *
     * @param bmobNoteList 云上笔记多
     * @param localNoteList 本地笔记少
     */
    private static void deleteBmobNote(Context context, List<BmobNote> bmobNoteList, List<Note> localNoteList) {
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
        new BmobObject().deleteBatch(context, needDeleteBmobNoteList, new DeleteListener() {
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

    private static void uploadSomeNote2Bmob(Context context, BmobUser user, List<Note> localNoteList, List<BmobNote> bmobNoteList) {
        // find diff id of note
        List<Note> needUploadLocalNoteList = searchNeedUploadLocalNote(localNoteList, bmobNoteList);
        uploadNote2Bmob(context, user, needUploadLocalNoteList);
    }

    /**
     *
     * @param localNoteList 笔记数量多
     * @param bmobNoteList  笔记数量少
     * @return 笔记差异
     */
    private static List<Note> searchNeedUploadLocalNote(List<Note> localNoteList, List<BmobNote> bmobNoteList) {
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

    private static void saveBmobNote2Local(List<BmobNote> list, NoteDbInstance mNoteDbInstance) {
        for (int i = 0; i < list.size(); i++) {
            BmobNote bmobNote = list.get(i);
            Note note = new Note();
            note.setCmpId(bmobNote.getCmpId());
            note.setYear(bmobNote.getYear());
            note.setMonth(bmobNote.getMonth());
            note.setDate(bmobNote.getDate());
            note.setTitle(bmobNote.getTitle());
            note.setContent(bmobNote.getContent());
            note.setLocation(bmobNote.getLocation());
            note.setHasModified(1);
            mNoteDbInstance.InsertNote(note);
        }
    }

    private static void uploadNote2Bmob(Context context, BmobUser user, List<Note> localNoteList) {
        List<BmobObject> bmobNoteList = new ArrayList<>();
        for (int i = 0; i < localNoteList.size() ; i++) {
            Note note = localNoteList.get(i);
            bmobNoteList.add(new BmobNote(user, note.getContent(), note.getDate(),
                    note.getHasModified(), note.getCmpId(), note.getLocation(),
                    note.getMonth(), note.getTitle(), note.getYear()));
        }
        new BmobObject().insertBatch(context, bmobNoteList, new SaveListener() {
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
}

