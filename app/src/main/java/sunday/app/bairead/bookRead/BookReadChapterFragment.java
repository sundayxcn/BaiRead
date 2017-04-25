package sunday.app.bairead.bookRead;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseFragment;
import sunday.app.bairead.bookRead.adapter.ChapterAdapter;
import sunday.app.bairead.bookRead.adapter.ReadAdapter;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.utils.PreferenceKey;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public class BookReadChapterFragment extends BaseFragment implements BookReadContract.ViewMenu {

    @BindView(R.id.book_read_setting_panel_list_title)
    TextView mListTitle;
    @BindView(R.id.book_read_setting_panel_list_button)
    Button mListButton;
    @BindView(R.id.book_read_setting_panel_list)
    ListView mList;

    private PreferenceSetting mPreferenceSetting;
    private BookReadContract.Presenter mPresenter;
    private ReadAdapter mReadAdapter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.book_read_fragment, container, false);
        unbinder = ButterKnife.bind(this, root);
        init();
        return root;
    }


    public void init() {
        String title = mPresenter.getBookInfo().bookDetail.getName();
        mListTitle.setText(title);
        mListButton.setText(getButtonText());
        ArrayList<BookChapter.Chapter> list = (ArrayList<BookChapter.Chapter>) mPresenter.
                getBookInfo().
                bookChapter.
                getChapterList().
                clone();
        if (!isOrderDefault()) {
            Collections.reverse(list);
        }
        mReadAdapter = new ChapterAdapter(getActivity(), list);
        mList.setAdapter(mReadAdapter);

    }


    @Override
    public void setPresenter(BookReadContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setPreferenceSetting(PreferenceSetting preferenceSetting) {
        mPreferenceSetting = preferenceSetting;
    }

    public String getButtonText() {
        if (isOrderDefault()) {
            return getResources().getString(R.string.order_default);
        } else {
            return getResources().getString(R.string.order_revert);
        }
    }


    public boolean isOrderDefault() {
        int order = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_CHAPTER_ORDER, PreferenceKey.CHAPTER_ORDER_DEFAULT);
        return order == PreferenceKey.CHAPTER_ORDER_DEFAULT;
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

//    @Override
//    public void setChapterIndex() {
//        int chapterIndex = 0;
//        mPresenter.setChapterIndex(chapterIndex);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnItemClick(R.id.book_read_setting_panel_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int chapterIndex = isOrderDefault() ? position : mReadAdapter.getCount() - position - 1;
        getFragmentManager().popBackStack();
        mPresenter.setChapterIndex(chapterIndex);
    }

    @OnClick(R.id.book_read_setting_panel_list_button)
    public void onClick(View view) {
        int order = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_CHAPTER_ORDER, PreferenceKey.CHAPTER_ORDER_DEFAULT);
        if (order == PreferenceKey.CHAPTER_ORDER_DEFAULT) {
            mPreferenceSetting.putIntValue(PreferenceSetting.KEY_CHAPTER_ORDER, PreferenceKey.CHAPTER_ORDER_REVERSE);
        } else {
            mPreferenceSetting.putIntValue(PreferenceSetting.KEY_CHAPTER_ORDER, PreferenceKey.CHAPTER_ORDER_DEFAULT);
        }
        Collections.reverse(mReadAdapter.getList());
        mReadAdapter.notifyDataSetChanged();
        mListButton.setText(getButtonText());
    }


    @Override
    protected boolean onBackPressed() {
        return super.onBackPressed();
    }

}
