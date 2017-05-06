package sunday.app.bairead.bookSearch;

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
import java.util.List;

import sunday.app.bairead.base.BaseActivity;
import sunday.app.bairead.bookDetail.BookDetailActivity;
import sunday.app.bairead.bookSearch.adapter.BookAdapter;
import sunday.app.bairead.bookSearch.adapter.HistoryAdapter;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.R;
import sunday.app.bairead.download.BookDownService;
import sunday.app.bairead.parse.ParseBaiduSearch;
import sunday.app.bairead.parse.ParseBookDetail;
import sunday.app.bairead.utils.Temp;
import sunday.app.bairead.view.MaterialProgressView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by sunday on 2017/3/6.
 */

public class BookSearchActivity extends BaseActivity implements BookSearchContract.View {


    private BookSearchContract.Present mBookSearchPresenter;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_search_activity);
        setupView();
        mBookSearchPresenter = new BookSearchPresenter(
                this,
                new BookDownService(),
                new ParseBaiduSearch(),
                new ParseBookDetail());
        mBookSearchPresenter.start();
    }

    @Override
    public void setPresenter(BookSearchContract.Present presenter) {

    }

    @Override
    public void showLoading() {
        showMaterialProgress();
    }

    @Override
    public void hideLoading() {
        hideMaterialProgress();
    }

    @Override
    public void showHistory(List<String> titleList) {
        historyAdapter = new HistoryAdapter(getLayoutInflater(), new ArrayList<>());
        historyListView.setAdapter(historyAdapter);
        showHistoryPanel();
    }

    @Override
    public void showResult(BookInfo bookInfo) {
        if (bookAdapter == null) {
            bookAdapter = new BookAdapter(getLayoutInflater(), new ArrayList<>());
            bookListView.setAdapter(bookAdapter);
        }
        showResultPanel();
        bookAdapter.addItem(bookInfo);
    }

    @Override
    public void clearSearch() {
        try {
            bookAdapter.clearData();
        } catch (NullPointerException e) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBookSearchPresenter.stop();
    }

    private void setupView() {

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
                if (name.length() != 0) {
                    mBookTextEditText.clearFocus();
                    mBookSearchPresenter.addHistory(name);
                    mBookSearchPresenter.search(name);
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
                mBookSearchPresenter.clearHistory();
            }
        });
        historyListView = (ListView) mHistoryPanel.findViewById(R.id.book_search_history_list_view);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) historyAdapter.getItem(position);
                mBookTextEditText.setText(name);
                mBookTextEditText.clearFocus();
                mBookSearchPresenter.search(name);
            }
        });


        mResultPanel = (RelativeLayout) findViewById(R.id.book_search_activity_result_panel);
        //mResultTextView = (TextView) mResultPanel.findViewById(R.id.book_search_activity_result_panel_text);
        bookListView = (ListView) mResultPanel.findViewById(R.id.book_search_book_list_view);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookInfo bookInfo = bookAdapter.getItem(position);
                mBookSearchPresenter.goBookDetail(getApplicationContext(), bookInfo);
            }
        });

    }


    public void showMaterialProgress() {
        materialProgressView.setVisibility(View.VISIBLE);
        materialProgressView.start();
    }

    public void hideMaterialProgress() {
        materialProgressView.setVisibility(View.GONE);
        materialProgressView.stop();
    }

    @Override
    public void onBackPressed() {
        if (materialProgressView.getVisibility() == View.VISIBLE) {
            hideMaterialProgress();
        } else {
            super.onBackPressed();
        }
    }

    public void showHistoryPanel() {
        mHistoryPanel.setVisibility(View.VISIBLE);
        mResultPanel.setVisibility(View.INVISIBLE);
    }

    public void showResultPanel() {
        mHistoryPanel.setVisibility(View.INVISIBLE);
        mResultPanel.setVisibility(View.VISIBLE);
    }


}