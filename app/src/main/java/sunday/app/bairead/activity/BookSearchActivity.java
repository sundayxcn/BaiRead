package sunday.app.bairead.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.R;
import sunday.app.bairead.utils.Temp;
import sunday.app.bairead.presenter.BookSearchPresenter;
import sunday.app.bairead.view.MaterialProgressView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by sunday on 2017/3/6.
 */

public class BookSearchActivity extends BaseActivity implements BookSearchPresenter.IBookSearchListener{


    private BookSearchPresenter bookSearchPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_search_activity);
        setupView();
        bookSearchPresenter = new BookSearchPresenter(this);
    }

    private ImageButton mBackButton;
    private EditText mBookTextEditText;
    private ListView historyListView;
    private TextView historyTextView;
    private Button historyDeleteButton;
    private ListView bookListView;
    private HistoryAdapter historyAdapter;
    private BookAdapter bookAdapter;
    private TextView mToastView;

    private RelativeLayout mHotSearchPanel;
    private RelativeLayout mHistoryPanel;
    private RelativeLayout mResultPanel;
    private TextView mResultTextView;
    private MaterialProgressView materialProgressView;

    public static void goBookDetail(Context context,BookInfo bookInfo){
        Temp.getInstance().setBookInfo(bookInfo);
        Intent intent = new Intent();
        intent.setClass(context, BookDetailActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private class HistoryAdapter extends BaseAdapter{

        private ArrayList<String> list;


        public HistoryAdapter(ArrayList<String> historyList){
            this.list = historyList;
        }

        public void addItem(String name){
            list.add(0,name);
            notifyDataSetChanged();
        }

        public void clear(){
            list.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.history_list_item,null,false);
                convertView.setMinimumHeight(200);
            }

            ((TextView)convertView).setText(list.get(position));
            return convertView;
        }
    }


    class ViewHolder {
        private TextView nameTView;
        private TextView authorTView;
        private TextView sourceTView;
        private TextView chapterLatestTView;
        private TextView chapterTimeTView;

        ViewHolder(View parent) {
            nameTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_name);
            authorTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_author);
            sourceTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_source);
            chapterLatestTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_chapter_latest);
            chapterTimeTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_chapter_time);
        }

        void setValue(BookInfo searchResult){
            nameTView.setText(searchResult.bookDetail.getName());
            authorTView.setText(searchResult.bookDetail.getAuthor());
            //sourceTView.setText(sourceTView.getText()+info.get);
            chapterLatestTView.setText(searchResult.bookDetail.getChapterLatest());
            chapterTimeTView.setText(searchResult.bookDetail.getUpdateTime());
        }

    }

    private class BookAdapter extends BaseAdapter{
        private ArrayList<BookInfo> list;

        public void initList(){
            list = new ArrayList<>();
        }

        public void addItem(BookInfo bookInfo){
            list.add(bookInfo);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public BookInfo getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.search_list_item,null,false);
                ViewHolder viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.setValue(list.get(position));
            return convertView;
        }
    }

    private void setupView(){

        materialProgressView = (MaterialProgressView) findViewById(R.id.book_search_activity_material_progress);
        mBackButton = (ImageButton) findViewById(R.id.book_search_button_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBookTextEditText = (EditText) findViewById(R.id.book_search_edit_text);
        Button ButtonSearch = (Button) findViewById(R.id.book_search_button_search);
        ButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mBookTextEditText.getText().toString();
                if(name.length()!=0) {
                    //mBookTextEditText.setText("");
                    mBookTextEditText.clearFocus();
                    bookSearchPresenter.addSearchHistory(getBaseContext(), name);
                    //bookSearchPresenter.searchBook(BookSearchActivity.this,name);
                    bookSearchPresenter.searchBookDebug(name);
                }
            }
        });


        mHotSearchPanel = (RelativeLayout) findViewById(R.id.book_search_activity_hot_search_panel);

        mHistoryPanel = (RelativeLayout) findViewById(R.id.book_search_activity_history_panel);


        historyTextView = (TextView) mHistoryPanel.findViewById(R.id.book_search_history);
        historyDeleteButton = (Button) mHistoryPanel.findViewById(R.id.book_search_history_delete_button);
        historyDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyAdapter.clear();
                bookSearchPresenter.clearHistory(getBaseContext());
            }
        });
        historyListView = (ListView) mHistoryPanel.findViewById(R.id.book_search_history_list_view);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) historyAdapter.getItem(position);
                mBookTextEditText.setText(name);
                mBookTextEditText.clearFocus();
                bookSearchPresenter.searchBookDebug(name);
            }
        });



        mResultPanel = (RelativeLayout) findViewById(R.id.book_search_activity_result_panel);
        //mResultTextView = (TextView) mResultPanel.findViewById(R.id.book_search_activity_result_panel_text);
        bookListView = (ListView) mResultPanel.findViewById(R.id.book_search_book_list_view);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //BookDownLoad.SearchResult searchResult = bookAdapter.getItem(position);
                BookInfo bookInfo = bookAdapter.getItem(position);
                goBookDetail(BookSearchActivity.this,bookInfo);
//                bookSearchPresenter.updateBookDetail(bookInfo,BookSearchActivity.this);
            }
        });

    }


    public void showMaterialProgress(){
        materialProgressView.setVisibility(View.VISIBLE);
        materialProgressView.start();
    }

    public void hideMaterialProgress(){
        materialProgressView.setVisibility(View.GONE);
        materialProgressView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookSearchPresenter.readSearchHistory(this);
    }


    @Override
    public void onBackPressed() {
        if(materialProgressView.getVisibility() ==View.VISIBLE){
            hideMaterialProgress();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void historyAddFinish(String name) {
        historyAdapter.addItem(name);

    }

    @Override
    public void historyLoadFinish(ArrayList<String> historyList) {
        historyAdapter = new HistoryAdapter(historyList);
        historyListView.setAdapter(historyAdapter);
    }

    @Override
    public void bookSearchStart() {
        //showProgressDialog();
        showMaterialProgress();
        showHistoryPanel(false);
        showResultPanel(true);
        //if(bookAdapter == null){
            bookAdapter = new BookAdapter();
            bookAdapter.initList();
            bookListView.setAdapter(bookAdapter);
        //}
    }

    @Override
    public void bookSearching(BookInfo bookInfo) {

        bookAdapter.addItem(bookInfo);
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    public void bookSearchFinish() {
        hideMaterialProgress();
        if(bookAdapter.getCount() == 0){
            showTipsDialog("这位小哥，为了节省流量，目前只支持精确搜索，请确定你没有写错书名");
        }
        //mResultTextView.setText("搜索完成");
    }


    @Override
    public void bookSearchError() {
        //hideProgressDialog();
        showToastNetworkUnconnect();
    }

    public void showHistoryPanel(boolean show){
        int visibility = show ? View.VISIBLE : View.GONE;
//        mToastView.setVisibility(visibility);
//        historyListView.setVisibility(visibility);
//        historyTextView.setVisibility(visibility);
//        historyDeleteButton.setVisibility(visibility);
        mHistoryPanel.setVisibility(visibility);

    }

    public void showResultPanel(boolean show){
        int visibility = show ? View.VISIBLE : View.GONE;
        mResultPanel.setVisibility(visibility);
    }


}