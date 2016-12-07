package sunday.app.bairead.UI;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import sunday.app.bairead.MainActivity;
import sunday.app.bairead.R;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.Tool.NetworkTool;
import sunday.app.bairead.Tool.SearchLink;
import sunday.app.bairead.Tool.SearchManager;

/**
 * Created by sunday on 2016/12/2.
 */
public class SearchFragment extends Fragment {

    private ImageButton mButtonBack;
    private Button mButtonSearch;
    private EditText mEditText;
    private ListView mListView;

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
                if(NetworkTool.isNetworkConnect(getContext())){
                    String bookName = mEditText.getText().toString();
                    searchHistory.addHistory(bookName);
                    new SearchManager(SearchFragment.this).search(bookName);
                }else{
                    Toast.makeText(getContext(),"open the network",Toast.LENGTH_SHORT).show();
                }


            }
        });

        mEditText = (EditText) view.findViewById(R.id.search_fragment_edit_text);


        mListView = (ListView) view.findViewById(R.id.search_fragment_list_view);
        mListView.setAdapter(searchHistory.getAdapter());
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
    public void refreshSearchResult(ArrayList<SearchLink> list){
        if(list == null){

        }else {
            if (mListView.getAdapter() == searchHistory.getAdapter()) {
                mListView.setAdapter(mSearchLinkAdapter);
            }

            mSearchLinkAdapter.setData(list);

        }
    }


    class SearchLinkAdapter extends BaseAdapter{

        ArrayList<SearchLink> searchLinkArrayList = new ArrayList<>();

        public void setData(ArrayList<SearchLink> list){
            searchLinkArrayList.clear();
            searchLinkArrayList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return searchLinkArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return searchLinkArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                TextView textView = new TextView(getContext());
                textView.setText(searchLinkArrayList.get(position).getLink());
                convertView = textView;
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
