package sunday.app.bairead.View;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.Download.BookChapterCache;
import sunday.app.bairead.R;
import sunday.app.bairead.Tool.PreferenceSetting;


/**
 * Created by sunday on 2017/2/23.
 */

public class BookReadSettingPanelView extends RelativeLayout{

    private int textSize;
    private int lineSize;
    private int chapterOrder;
    private ChapterPanel chapterPanel;

    private long bookId;

    public BookReadSettingPanelView(Context context) {
        super(context);
    }

    public BookReadSettingPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookReadSettingPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RelativeLayout settingTopPanel = (RelativeLayout) findViewById(R.id.book_read_setting_panel_top_panel);
        LinearLayout settingBottomPanel = (LinearLayout) findViewById(R.id.book_read_setting_panel_bottom_panel);
        setOnClick(settingTopPanel,buttonOnClickListener);
        setOnClick(settingBottomPanel,buttonOnClickListener);
    }

    private void setOnClick(ViewGroup viewGroup,OnClickListener onClickListener) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = viewGroup.getChildAt(i);
            v.setOnClickListener(onClickListener);
        }
        loadConfig();
    }

    public void setOnChangeListener(long bookId,OnChangeListener onChangeListener){
        this.bookId = bookId;
        this.onChangeListener = onChangeListener;
    }

    public interface OnChangeListener{
        void chapterChange(int chapterIndex);
        void textSizeChange(int textSize,int lineSize);
    }

    private OnChangeListener onChangeListener;

    private OnClickListener buttonOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.book_read_setting_panel_chapter_menu:
                    showChapterList();
                    //showSettingLong();
                    break;
                case R.id.book_read_setting_panel_book_mark:
                    //showBookTextSizePanel();
                    //break;
                case R.id.book_read_setting_panel_text_font:
                    showBookTextSizePanel();
                    break;
                case R.id.book_read_setting_panel_more:
                    //break;
                case R.id.book_read_setting_panel_source:
                    //break;
                    Toast.makeText(getContext(), "开发中", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    public void loadConfig(){
        PreferenceSetting preferenceSetting = PreferenceSetting.getInstance(getContext());

        textSize = preferenceSetting.getIntValue(PreferenceSetting.KEY_TEXT_SIZE,60);
        lineSize = preferenceSetting.getIntValue(PreferenceSetting.KEY_LINE_SIZE,20);
        chapterOrder = preferenceSetting.getIntValue(PreferenceSetting.KEY_CHAPTER_ORDER,0);
    }

    public void saveConfig(String key,int value){
        PreferenceSetting preferenceSetting = PreferenceSetting.getInstance(getContext());
        preferenceSetting.putIntValue(key,value);
    }


    private class ChapterAdapter extends BaseAdapter {

        //private BookInfo bookInfo;
        private ArrayList<BookChapter.Chapter> chapterArrayList;
        public void setBookInfo(BookInfo bookInfo){
            //this.bookInfo = bookInfo;
            chapterArrayList = (ArrayList<BookChapter.Chapter>) bookInfo.bookChapter.getChapterList().clone();
        }

        public ArrayList<BookChapter.Chapter> getChapterArrayList(){
            return chapterArrayList;
        }

        @Override
        public int getCount() {
            return chapterArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return chapterArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chapter_list_item,null,false);
            }
            TextView view = (TextView) convertView.findViewById(R.id.chapter_list_item_text);
            BookChapter.Chapter chapter = chapterArrayList.get(position);
            String text = chapter.getTitle();
            view.setText(text);
            TextView cacheTextview = (TextView) convertView.findViewById(R.id.chapter_list_item_cache_text);
            boolean isCache = BookChapterCache.getInstance().isChapterExists(position);
            cacheTextview.setVisibility(isCache ? VISIBLE : INVISIBLE);

            return convertView;
        }
    }

    private ChapterAdapter chapterAdapter;
    private void showChapterList() {
        chapterPanel = (ChapterPanel) LayoutInflater.from(getContext()).inflate(R.layout.book_read_setting_chapter_panel, null, false);
        BaiReadApplication application = (BaiReadApplication) getContext().getApplicationContext();
        BookModel bookModel = application.getBookModel();
        final BookInfo bookInfo = bookModel.getBookInfo(bookId);
        TextView bookNameView = (TextView) chapterPanel.findViewById(R.id.book_read_setting_panel_chapter_list_title);
        bookNameView.setText(bookInfo.bookDetail.getName());


        //if(chapterAdapter == null){
            chapterAdapter = new ChapterAdapter();
            chapterAdapter.setBookInfo(bookInfo);
        //}
        ListView chapterListView = (ListView) chapterPanel.findViewById(R.id.book_read_setting_panel_chapter_list);
        chapterListView.setAdapter(chapterAdapter);
        chapterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onChangeListener != null) {
                    if(chapterOrder == 1){
                        position = chapterAdapter.getCount() - position -1;
                    }
                    hide();
                    onChangeListener.chapterChange(position);
                }
            }
        });
        final Button chapterOrderButtonView = (Button) chapterPanel.findViewById(R.id.book_read_setting_panel_chapter_list_order_button);
        String orderString = chapterOrder == 0 ? "正序":"逆序";
        if(chapterOrder == 1){
            Collections.reverse(chapterAdapter.getChapterArrayList());
            chapterAdapter.notifyDataSetChanged();
        }
        chapterOrderButtonView.setText(orderString);
        chapterOrderButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chapterOrder = 1 - chapterOrder;
                saveConfig(PreferenceSetting.KEY_CHAPTER_ORDER,chapterOrder);
                String orderString = chapterOrder == 0 ? "正序":"逆序";
                chapterOrderButtonView.setText(orderString);
                Collections.reverse(chapterAdapter.getChapterArrayList());
                chapterAdapter.notifyDataSetChanged();
            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //layoutParams.setMargins(0, 0, 200, 0);
        addView(chapterPanel, layoutParams);
    }


    private void showBookTextSizePanel(){

    }



    public void show(){
        setVisibility(VISIBLE);
//        if(showType == ShowType.LONG){
//
//        }else{
//
//        }
    }

    public void hideAllChild(){
        int count = getChildCount();
        for(int i = 0;i<count;i++){
            View view = getChildAt(i);
            view.setVisibility(INVISIBLE);
        }
    }


    public void hide(){
        if(chapterPanel != null){
            removeView(chapterPanel);
            chapterPanel = null;
            chapterAdapter = null;
        }
        setVisibility(GONE);
    }

    public boolean isShow(){
        return getVisibility() == VISIBLE;
    }

}
