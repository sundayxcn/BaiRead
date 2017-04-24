package sunday.app.bairead.bookcase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.Unbinder;
import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseFragment;
import sunday.app.bairead.bookRead.BookReadContract;
import sunday.app.bairead.bookSearch.BookSearchActivity;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.utils.NetworkUtils;
import sunday.app.bairead.utils.PreferenceSetting;
import sunday.app.bairead.view.ListDialog;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class BookcaseFragment extends BaseFragment implements BookcaseContract.View {


    public static final int OPERATOR_TOP = 0;
    public static final int OPERATOR_DETAIL = OPERATOR_TOP + 1;
    public static final int OPERATOR_CACHE = OPERATOR_DETAIL + 1;
    public static final int OPERATOR_DELETE = OPERATOR_CACHE + 1;
    public static final int OPERATOR_ALL = OPERATOR_DELETE + 1;

    @BindView(R.id.book_case_tool_bar_top)
    ImageView bookCaseToolBarTop;
    @BindView(R.id.book_case_tool_bar_cache)
    ImageView bookCaseToolBarCache;
    @BindView(R.id.book_case_tool_bar_delete)
    ImageView bookCaseToolBarDelete;
    @BindView(R.id.book_case_tool_bar)
    LinearLayout bookCaseToolBar;
    @BindView(R.id.bookcase_list_view)
    ListView bookcaseListView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private BookcaseContract.Presenter mPresenter;
    private BookcaseListAdapter bookcaseListAdapter;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookcaseListAdapter = new BookcaseListAdapter(getActivity());
        mPresenter.start();
        if(PreferenceSetting.getInstance(getActivity()).isFirstRun()) {
            firstRunWork();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bookcase_fragment, container, false);
        unbinder = ButterKnife.bind(this, root);
        int color = ContextCompat.getColor(getActivity(), R.color.colorRed);
        swipeRefreshLayout.setColorSchemeColors(color);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (NetworkUtils.isNetworkConnect(getActivity())) {
                if (bookcaseListAdapter.getBookInfoList() == null || bookcaseListAdapter.getBookInfoList().size() == 0) {
                    swipeRefreshLayout.setRefreshing(false);
                    showToast(R.string.book_case_no_book_tips);
                } else {
                    mPresenter.updateBooks();
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
                showToast(R.string.network_connect_failed);
            }
        });
        bookcaseListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipeRefreshLayout.setEnabled(true);
                }else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        //解决阅读完后回到书架界面 当前章节显示未更新
        if (bookcaseListAdapter != null) {
            if (isToolBarShow()) {
                hideBookCaseToolBar();
            }
            bookcaseListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected boolean onBackPressed() {
        if (isToolBarShow()) {
            hideBookCaseToolBar();
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void showLoading() {
        showProgressDialog();
    }

    @Override
    public void showLoadingError() {

    }

    @Override
    public void showBooks(List<BookInfo> list) {
        bookcaseListAdapter.setBookInfoList(list);
        bookcaseListAdapter.notifyDataSetChanged();
        bookcaseListView.setAdapter(bookcaseListAdapter);
        hideProgressDialog();
    }

    @Override
    public void showNoBooks() {
        hideProgressDialog();
    }

    @Override
    public void showUpdateBook(List<BookInfo> list) {
        if (list != null) {
            showToast(R.string.update_finish);
        } else {
            showToast(R.string.update_finish_no_new);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setPresenter(BookcaseContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.book_case_tool_bar_top,R.id.book_case_tool_bar_cache,R.id.book_case_tool_bar_delete})
    public void onClick(View v) {
        int id = v.getId();
        final ArrayList<Long> bookIdList = bookcaseListAdapter.getCheckList();
        if(bookIdList != null && bookIdList.size() > 0) {
            if (id == R.id.book_case_tool_bar_top) {
                //mPresenter.topBooks(bookIdList);
            } else if (id == R.id.book_case_tool_bar_cache) {
                showConfirmDialog(R.string.cache_book_tips, (dialog, which) -> mPresenter.cacheBooks(bookIdList));
            } else if (id == R.id.book_case_tool_bar_delete) {
                showConfirmDialog(R.string.delete_book_tips, (dialog, which) -> mPresenter.deleteBooks(bookIdList));
            }
        }
    }


    public boolean isToolBarShow() {
        return bookCaseToolBar.getVisibility() == View.VISIBLE;
    }



    @OnItemClick(R.id.bookcase_list_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isToolBarShow()) {
            BookcaseListAdapter.ViewHolder viewHolder = (BookcaseListAdapter.ViewHolder) view.getTag();
            viewHolder.changeCheckBox();
        } else {
            BookInfo bookInfo = (BookInfo) bookcaseListAdapter.getItem(position);
            //mPresenter.readBook(bookInfo);
            ActivityUtils.readBook(getActivity(),bookInfo.bookDetail.getId());
        }
    }

    @OnItemLongClick(R.id.bookcase_list_view)
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isToolBarShow()) {
            long bookId = (long) bookcaseListAdapter.getItem(position);
            BookInfo bookInfo = mPresenter.getBook(bookId);
            showCaseOperatorDialog(bookInfo);
        }
        return true;
    }

    public void showCaseOperatorDialog(BookInfo bookInfo) {
        String bookName = bookInfo.bookDetail.getName();
        operatorListener.setBookInfo(bookInfo);
        String[] operator;
        if (bookInfo.bookDetail.topCase) {
            operator = getResources().getStringArray(R.array.dialog_list_operator_top_cancel);
        } else {
            operator = getResources().getStringArray(R.array.dialog_list_operator_top);
        }
        showListDialog(bookName, operator, operatorListener);
    }

    private ListDialog mListDialog;
    public void showListDialog(String title,String[] texts, AdapterView.OnItemClickListener onItemClickListener){
        mListDialog = new ListDialog(getActivity());
        mListDialog.show(title,texts,onItemClickListener);
    }

    public void hideListDialog(){
        if(mListDialog != null){
            mListDialog.dismiss();
        }
    }
    private OperatorListener operatorListener = new OperatorListener();
    class OperatorListener implements AdapterView.OnItemClickListener {
        private BookInfo bookInfo;

        private void setBookInfo(BookInfo bookInfo) {
            this.bookInfo = bookInfo;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case OPERATOR_TOP:
                    Map<Long,Boolean> map = new HashMap();
                    boolean topCase = bookInfo.bookDetail.isTopCase();
                    map.put(bookInfo.bookDetail.getId(),topCase);
                    mPresenter.topBooks(map);
                    break;
                case OPERATOR_DETAIL:
                    BookSearchActivity.goBookDetail(getActivity(), bookInfo);
                    break;
                case OPERATOR_CACHE:
                    showConfirmDialog(R.string.cache_one_book_tips, (dialog, which) -> mPresenter.cacheBook(bookInfo.bookDetail.getId()));
                    break;
                case OPERATOR_DELETE:
                    showConfirmDialog(R.string.delete_one_book_tips, (dialog, which) -> mPresenter.deleteBook(bookInfo.bookDetail.getId()));
                    break;
                case OPERATOR_ALL:
                    showBookCaseToolBar();
                    break;
                default:
                    break;
            }

            hideCaseOperatorDialog();

        }
    }

    public void showBookCaseToolBar() {
        bookCaseToolBar.setVisibility(View.VISIBLE);
        bookcaseListAdapter.setEdit(true);
        bookcaseListAdapter.notifyDataSetChanged();
    }

    public void hideBookCaseToolBar() {
        bookCaseToolBar.setVisibility(View.GONE);
        bookcaseListAdapter.clear();
        bookcaseListAdapter.setEdit(false);
        bookcaseListAdapter.notifyDataSetChanged();
    }

    public void hideCaseOperatorDialog() {
        hideListDialog();
    }

    private void firstRunWork() {
        PreferenceSetting.getInstance(getActivity()).setFirstRunFalse();
        inflateBook();
    }

    void inflateBook() {
        File[] files = FileManager.getInstance(getActivity()).checkBookCache();
        final int bookCount = files.length;
        if (bookCount > 0) {
            showConfirmDialog(
                    R.string.cache_book_title,
                    R.string.cache_book_confirm,
                    R.string.cache_book_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new FirstRunAsyncTask(new File(FileManager.PATH), mPresenter) {
                                @Override
                                protected void onProgressUpdate(String... values) {
                                    super.onProgressUpdate(values);
                                    showProgressDialog(values[0]);
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    mPresenter.loadBooks(true);
//                                    if (bookcaseListAdapter != null) {
//                                        bookcaseListAdapter.notifyDataSetChanged();
//                                    }
                                    hideProgressDialog();
                                }
                            }.execute();
                        }
                    });
        }
    }
}
