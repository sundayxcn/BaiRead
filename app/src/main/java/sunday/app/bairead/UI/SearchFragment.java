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

import java.io.IOException;
import java.util.ArrayList;

import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.R;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.View.BookDetailView;
import sunday.app.bairead.View.SearchLinkItemView;

/**
 * Created by sunday on 2016/12/2.
 */
public class SearchFragment extends Fragment {

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
        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                new SearchManager(SearchFragment.this).downloadEnd();
                    String s = FileManager.getInstance().readFile("11.html");
                Spanned spanned = Html.fromHtml(s);
                TextView textView = new TextView(getActivity());
                textView.setText(spanned);
//                if(NetworkTool.isNetworkConnect(getContext())){
//                    String bookName = mEditText.getText().toString().trim();
//                    if(bookName.length() > 0) {
//                        searchHistory.addHistory(bookName);
//                        new SearchManager(SearchFragment.this).search(bookName);
//                    }
//                }else{
//                    Toast.makeText(getContext(),"open the network",Toast.LENGTH_SHORT).show();
//                }


            }
        });

        mEditText = (EditText) view.findViewById(R.id.search_fragment_edit_text);


        mListView = (ListView) view.findViewById(R.id.search_fragment_list_view);
        mListView.setAdapter(searchHistory.getAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view instanceof SearchLinkItemView){
                    SearchLinkItemView searchLinkItemView = (SearchLinkItemView) view;
                    bookDetailView.setDetail((BookDetail) searchLinkItemView.getTag());
                    bookDetailView.animatorShow(false);
                }else{

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

    /**
     * 点击搜索按钮后显示搜索结果
     * 点击按钮->OKHttp异步下载搜索结果->调用此方法
     * **/
    public void refreshSearchResult(final BookDetail bookDetail){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mListView.getAdapter() == searchHistory.getAdapter()) {
                    mListView.setAdapter(mSearchLinkAdapter);
                }
                mSearchLinkAdapter.addData(bookDetail);
            }
        });
    }



    class SearchLinkAdapter extends BaseAdapter{

        ArrayList<BookDetail> bookDetailList = new ArrayList<>();

        public void addData(BookDetail bookDetail){
            bookDetailList.add(bookDetail);
            notifyDataSetChanged();
        }

//        public void setData(ArrayList<BookDetail> list){
//            bookDetailList.clear();
//            bookDetailList.addAll(list);
//            notifyDataSetChanged();
//        }

        @Override
        public int getCount() {
            return bookDetailList.size();
        }

        @Override
        public Object getItem(int position) {
            return bookDetailList.get(position);
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
                searchLinkItemView.setInfo(bookDetailList.get(position));
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
            ArrayList<String> list = FileManager.getInstance().readFileByLine(FILE_NAME);
            for(String name :list){
                mHistoryList.add(0,name);
            }
        }

        public ArrayList<String> getHistory(){
            return mHistoryList;
        }

        public void addHistory(String bookName){
            try {
                FileManager.getInstance().writeFileByLine(FILE_NAME, bookName);
                mHistoryList.add(0,bookName);
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                searchHistoryAdapter.notifyDataSetChanged();
            }
        }


    }
}
