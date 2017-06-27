package sunday.app.bairead.bookRead;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseFragment;
import sunday.app.bairead.bookRead.adapter.ChapterAdapter;
import sunday.app.bairead.bookRead.adapter.ReadAdapter;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.Chapter;
import sunday.app.bairead.utils.PreferenceKey;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public class BookReadChapterFragment extends BaseFragment implements BookReadContract.ChapterView {

    @BindView(R.id.book_read_setting_panel_list_title)
    TextView mListTitle;
    @BindView(R.id.book_read_setting_panel_list_button)
    Button mListButton;
    @BindView(R.id.book_read_setting_panel_list)
    ListView mList;

    private BookReadContract.ChapterPresenter mPresenter;
    private ReadAdapter mReadAdapter;
    private Unbinder unbinder;

    private boolean isOrderDefault;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("sunday", "BookReadChapterFragment-onCreateView");
        View root = inflater.inflate(R.layout.book_read_fragment, container, false);
        unbinder = ButterKnife.bind(this, root);
        mPresenter.start();
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnItemClick(R.id.book_read_setting_panel_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int chapterIndex = isOrderDefault ? position : mReadAdapter.getCount() - position - 1;
        getFragmentManager().popBackStack();
        Intent intent = new Intent(BookReadActivity.ACTION_CHAPTER_INDEX);
        intent.putExtra(BookReadActivity.ACTION_CHAPTER_EXTRA, chapterIndex);
        getActivity().sendBroadcast(intent);
    }

    @OnClick(R.id.book_read_setting_panel_list_button)
    public void onClick(View view) {
        mPresenter.changeOrder();
    }

    @Override
    public void setPresenter(BookReadContract.ChapterPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showOrder(boolean defaultOrder) {
        if (isOrderDefault != defaultOrder && mReadAdapter != null) {
            isOrderDefault = defaultOrder;
            Collections.reverse(mReadAdapter.getList());
            mReadAdapter.notifyDataSetChanged();
        }
        mListButton.setText(getButtonText());
    }

    public String getButtonText() {
        if (isOrderDefault) {
            return getResources().getString(R.string.order_default);
        } else {
            return getResources().getString(R.string.order_revert);
        }
    }

    @Override
    public void showChapter(BookInfo bookInfo) {
        String title = bookInfo.bookDetail.getName();
        List list = new ArrayList();
        list.addAll(bookInfo.bookChapter.getChapterList());
        mReadAdapter = new ChapterAdapter(getActivity(), list, title);
        if(!isOrderDefault){
            Collections.reverse(mReadAdapter.getList());
        }
        mList.setAdapter(mReadAdapter);
        mListTitle.setText(title);
    }
}
