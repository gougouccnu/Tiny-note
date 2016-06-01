package com.ggccnu.tinynote;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class HorizontalListViewAdapter extends BaseAdapter{
    //private int[] mIconIDs;
    //private String[] mTitles;
    private List<NotesItem> mNotesItemList = new ArrayList<NotesItem>();
    private Context mContext;
    private LayoutInflater mInflater;
    //Bitmap iconBitmap;
    private int selectIndex = -1;
/*
    public HorizontalListViewAdapter(Context context, String[] titles){
        this.mContext = context;
        //this.mIconIDs = ids;
        this.mTitles = titles;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }
*/

    public HorizontalListViewAdapter(Context context, List<NotesItem> notesItemList){
        this.mContext = context;
        //this.mIconIDs = ids;
        this.mNotesItemList = notesItemList;
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);

    }
    @Override
    public int getCount() {
        return mNotesItemList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.note_item, null);
            //holder.mImage=(ImageView)convertView.findViewById(R.id.img_list_item);
            holder.mTitle=(TextViewVertical)convertView.findViewById(R.id.note_item);

            //Typeface customFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/KangXi.ttf");
            //holder.mTitle = (TextViewVertical) findViewById(R.id.title_year);
            //holder.mTitle.setTypeface(customFont);

            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        if(position == selectIndex){
            convertView.setSelected(true);
        }else{
            convertView.setSelected(false);
        }

        holder.mTitle.setText(mNotesItemList.get(position).getTitle());
        // modify fonts here makes activity load slow
        //Typeface customFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/KangXi.ttf");
        //holder.mTitle.setTypeface(customFont);

        return convertView;
    }
    public class changeFont extends AsyncTask<String, Integer, String> {
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = mInflater.inflate(R.layout.note_item, null);
            //TextViewVertical tx =(TextViewVertical)view.findViewById(R.id.note_item);
            //Typeface customFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/KangXi.ttf");
            //tx.setTypeface(customFont);
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            String myString = params[0];

            // Do something that takes a long time, for example:
            for (int i = 0; i <= 100; i++) {

                // Do things

                // Call this to update your progress
                publishProgress(i);
            }

            return "this string is passed to onPostExecute";
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            // Do things like update the progress bar
        }
        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Typeface customFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/KangXi.ttf");
            //holder.mTitle.setTypeface(customFont);
            //Typeface customFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/KangXi.ttf");
            //holder.mTitle.setTypeface(customFont);// Do things like hide the progress bar or change a TextView
        }
    }
    private static class ViewHolder {
        private TextViewVertical mTitle ;
        //private ImageView mImage;
    }
    /***********
    private Bitmap getPropThumnail(int id){
        Drawable d = mContext.getResources().getDrawable(id);
        Bitmap b = BitmapUtil.drawableToBitmap(d);
//		Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
        int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
        int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);

        Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);

        return thumBitmap;
    }************/
    public void setSelectIndex(int i){
        selectIndex = i;
    }
}