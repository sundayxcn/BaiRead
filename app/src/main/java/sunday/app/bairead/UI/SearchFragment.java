package sunday.app.bairead.UI;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.Download.SearchManager;
import sunday.app.bairead.R;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.Tool.NetworkTool;
import sunday.app.bairead.View.BookDetailView;
import sunday.app.bairead.View.SearchLinkItemView;

/**
 * Created by sunday on 2016/12/2.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {

    private ImageButton mButtonBack;
    private Button mButtonSearch;
    private EditText mEditText;
    private ListView mListView;
    private BookDetailView bookDetailView;

    private SearchHistory searchHistory = new SearchHistory();
    private SearchLinkAdapter mSearchLinkAdapter = new SearchLinkAdapter();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_fragment, container, false);

        mButtonBack = (ImageButton) view.findViewById(R.id.search_fragment_button_back);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });


        mButtonSearch = (Button) view.findViewById(R.id.search_fragment_button_search);
        mButtonSearch.setOnClickListener(this);

        mEditText = (EditText) view.findViewById(R.id.search_fragment_edit_text);


        mListView = (ListView) view.findViewById(R.id.search_fragment_list_view);
        mListView.setAdapter(searchHistory.getAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view instanceof SearchLinkItemView){
                    SearchLinkItemView searchLinkItemView = (SearchLinkItemView) view;
                    bookDetailView.setInfo((BookInfo) searchLinkItemView.getTag());
                    bookDetailView.animatorShow(false);
                }else{
                    TextView textView = (TextView) view;
                    CharSequence text = textView.getText();
                    mEditText.setText(text);
                    onClick(null);
                }
            }
        });


        bookDetailView = (BookDetailView) view.findViewById(R.id.search_fragment_top_book_detail);


        return view;
    }


    public void show(Activity activity){
        FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack("search");
        fragmentTransaction.add(R.id.drawer_layout,this).show(this).commit();
    }

    public void hide(){
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.remove(SearchFragment.this);
        fragmentTransaction.commit();
        fragmentManager.popBackStack();
    }

    public void back(){
        if(bookDetailView.getVisibility() == View.VISIBLE){
            bookDetailView.animatorHide(false);
        }else{
            hide();
        }
    }


    /**
     * 点击搜索按钮后显示搜索结果
     * 点击按钮->OKHttp异步下载搜索结果->调用此方法
     * **/
    public void refreshSearchResult(final BookInfo bookInfo){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mListView.getAdapter() == searchHistory.getAdapter()) {
                    mListView.setAdapter(mSearchLinkAdapter);
                }
                mSearchLinkAdapter.addData(bookInfo);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(NetworkTool.isNetworkConnect(getContext())){
            String bookName = mEditText.getText().toString().trim();
            if(bookName.length() > 0) {
                searchHistory.addHistory(bookName);

                new SearchManager(SearchFragment.this).debugDetail(bookName);
                //new SearchManager(SearchFragment.this).searchTopWeb(bookName);
            }
        }else{
            Toast.makeText(getContext(),"open the network",Toast.LENGTH_SHORT).show();
        }
    }


    class SearchLinkAdapter extends BaseAdapter{

        ArrayList<BookInfo> bookInfoList = new ArrayList<>();

        public void addData(BookInfo bookInfo){
            bookInfoList.add(bookInfo);
            notifyDataSetChanged();
        }

//        public void setData(ArrayList<BookDetail> list){
//            bookDetailList.clear();
//            bookDetailList.addAll(list);
//            notifyDataSetChanged();
//        }

        @Override
        public int getCount() {
            return bookInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return bookInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                SearchLinkItemView searchLinkItemView = (SearchLinkItemView) layoutInflater.inflate(R.layout.search_fragment_list_item,null);
                searchLinkItemView.setInfo(bookInfoList.get(position));
                convertView = searchLinkItemView;
            }
            return convertView;
        }
    }

    class SearchHistoryAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return searchHistory.getHistory().size();
        }

        @Override
        public Object getItem(int position) {
            return searchHistory.getHistory().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                TextView textView = new TextView(getContext());
                textView.setText(searchHistory.getHistory().get(position));
                convertView = textView;
            }
            return convertView;
        }
    }


    /**
     * Created by sunday on 2016/12/6.
     * 用于对搜索历史的管理，每次进入读写根目录的searchHistory文件
     */
      class SearchHistory {
        public static final String FILE_NAME = "searchHistory.txt";
        private ArrayList<String> mHistoryList = new ArrayList<>();

        private SearchHistoryAdapter searchHistoryAdapter = new SearchHistoryAdapter();

        public SearchHistoryAdapter getAdapter(){
            return searchHistoryAdapter;
        }

        SearchHistory(){
            ArrayList<String> list = FileManager.readFileByLine(FILE_NAME);
            for(String name :list){
                mHistoryList.add(0,name);
            }
        }

        public ArrayList<String> getHistory(){
            return mHistoryList;
        }

        public void addHistory(String bookName){
            try {
                if(mHistoryList.contains(bookName)){
                    mHistoryList.remove(bookName);
                    mHistoryList.add(0, bookName);
                }else {
                    FileManager.writeFileByLine(FILE_NAME, bookName);
                    mHistoryList.add(0, bookName);
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                searchHistoryAdapter.notifyDataSetChanged();
            }
        }


    }
}
