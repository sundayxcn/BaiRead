package sunday.app.bairead.bookRead;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.Unbinder;
import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseActivity;
import sunday.app.bairead.base.BaseFragment;
import sunday.app.bairead.bookRead.adapter.MarkAdapter;
import sunday.app.bairead.bookRead.adapter.ReadAdapter;
import sunday.app.bairead.bookRead.cache.BookChapterCacheNew;
import sunday.app.bairead.data.setting.BookMarkInfo;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public class BookReadMarkFragment extends BaseFragment implements BookReadContract.ViewMenu {

    @BindView(R.id.book_read_setting_panel_list_title)
    TextView mListTitle;
    @BindView(R.id.book_read_setting_panel_list_button)
    Button mListButton;
    @BindView(R.id.book_read_setting_panel_list)
    ListView mList;

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

    private void init(){
        mPresenter.loadBookMark(mList);
    }


    @Override
    public void setPresenter(BookReadContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnItemClick(R.id.book_read_setting_panel_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BookMarkInfo bookMarkInfo = (BookMarkInfo) mList.getAdapter().getItem(position);
        mPresenter.setChapterIndex(bookMarkInfo.chapterIndex);
    }

    @OnItemLongClick(R.id.book_read_setting_panel_list)
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final BookMarkInfo bookMarkInfo = (BookMarkInfo) mList.getAdapter().getItem(position);
        showConfirmDialog(R.string.mark_delete, (dialog, which) -> {
            mReadAdapter = (ReadAdapter) mList.getAdapter();
            mReadAdapter.removeItem(bookMarkInfo);
            mPresenter.deleteBookMark(bookMarkInfo);
        });
        return true;
    }

    @OnClick(R.id.book_read_setting_panel_list_button)
    public void onClick(View v){
        showConfirmDialog(R.string.mark_clear, (dialog, which) -> {
            mReadAdapter = (ReadAdapter) mList.getAdapter();
            mReadAdapter.clear();
            mPresenter.clearBookMark();
        });
    }
}
