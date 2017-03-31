package sunday.app.bairead.bookCase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sunday.app.bairead.R;
import sunday.app.bairead.activity.BaseActivity;
import sunday.app.bairead.activity.BookSearchActivity;
import sunday.app.bairead.activity.DisclaimerActivity;
import sunday.app.bairead.bookRead.BookChapterCacheNew;
import sunday.app.bairead.database.BaiReadApplication;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.database.BookModel;
import sunday.app.bairead.download.BookChapterCache;
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.utils.NewChapterShow;
import sunday.app.bairead.utils.PreferenceSetting;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, BookcasePresenter.IBookcasePresenterListener {


    public static final int OPERATOR_TOP = 0;
    public static final int OPERATOR_DETAIL = OPERATOR_TOP + 1;
    public static final int OPERATOR_CACHE = OPERATOR_DETAIL + 1;
    public static final int OPERATOR_DELETE = OPERATOR_CACHE + 1;
    public static final int OPERATOR_ALL = OPERATOR_DELETE + 1;

    Handler handler = new Handler();
    ComparatorManager comparatorManager = new ComparatorManager();
    private BookcasePresenter bookcasePresenter;
    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mBookCaseToolBar;
    private BookListAdapter booklistAdapter;
    private OperatorListener operatorListener = new OperatorListener();
    private int click;
    private View.OnClickListener toolbarOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.book_case_tool_bar_top:
                    showConfirmDialog(R.string.top_book_tips, new DialogListenerIm() {
                        @Override
                        public void onConfirm() {
                            super.onConfirm();
                            ArrayList<Long> bookIdList = booklistAdapter.getCheckList();
                            if (bookIdList != null) {
                                topBook(bookIdList);
                            }
                            onBackPressed();
                        }
                    });
                    break;
                case R.id.book_case_tool_bar_cache:
                    showConfirmDialog(R.string.cache_book_tips, new DialogListenerIm() {
                        @Override
                        public void onConfirm() {
                            super.onConfirm();
                            ArrayList<Long> bookIdList = booklistAdapter.getCheckList();
                            BaiReadApplication application = (BaiReadApplication) getApplication();
                            BookModel bookModel = application.getBookModel();
                            for (long id : bookIdList) {
                                BookInfo bookInfo = bookModel.getBookInfo(id);
                                BookChapterCache.getInstance().downloadAllChpater(bookInfo);
                            }
                            onBackPressed();
                        }
                    });
                    break;
                case R.id.book_case_tool_bar_delete:
                    showConfirmDialog(R.string.delete_book_tips, new DialogListenerIm() {
                        @Override
                        public void onConfirm() {
                            super.onConfirm();
                            ArrayList<Long> bookIdList = booklistAdapter.getCheckList();
                            BaiReadApplication application = (BaiReadApplication) getApplication();
                            BookModel bookModel = application.getBookModel();
                            for (long id : bookIdList) {
                                BookInfo bookInfo = bookModel.getBookInfo(id);
                                booklistAdapter.getBookInfoList().remove(bookInfo);
                                bookcasePresenter.deleteBook(bookInfo);
                            }
                            onBackPressed();
                        }
                    });
                    break;
                default:
            }

        }
    };


    private void createDir(){

        //缓存文件夹
        File file = getCacheDir();
        if (!file.exists()) {
            file.mkdirs();
        }

        //搜索临时文件夹
        file = new File(FileManager.TEMP_DIR);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDir();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (PreferenceSetting.getInstance(this).isFirstRun()) {
            firstRunWork();
        }
        setupView();

        bookcasePresenter = new BookcasePresenter(this, this);
        bookcasePresenter.init();

    }

    private void firstRunWork() {
        PreferenceSetting.getInstance(this).setFirstRunFalse();
        File[] files = FileManager.checkBookCache();
        if (files != null) {
            inflateBook(files);
        }
    }

    private void inflateBook(File[] files) {
        final int bookCount = files.length;
        if (bookCount > 0) {
            showConfirmDialog(R.string.cache_book_title, R.string.cache_book_confirm, R.string.cache_book_cancel, new DialogListenerIm() {
                @Override
                public void onConfirm() {
                    new FirstRunAsyncTask(new File(FileManager.PATH), getApplicationContext()) {
                        @Override
                        protected void onProgressUpdate(String... values) {
                            super.onProgressUpdate(values);
                            showProgressDialog(values[0]);
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            if (booklistAdapter != null) {
                                booklistAdapter.notifyDataSetChanged();
                            }
                            hideProgressDialog();
                        }
                    }.execute();
                }
            });
        }
    }

    private void setupView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        int color = ContextCompat.getColor(this,R.color.colorRed);
        swipeRefreshLayout.setColorSchemeColors(color);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnect()) {
                    if(booklistAdapter.getBookInfoList() == null ||booklistAdapter.getBookInfoList().size() == 0){
                        swipeRefreshLayout.setRefreshing(false);
                        showToast(R.string.book_case_no_book_tips);
                    }else {
                        bookcasePresenter.checkNewChapter(booklistAdapter.getBookInfoList());
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showToastNetworkUnConnect();
                }
            }
        });

        mBookCaseToolBar = (LinearLayout) findViewById(R.id.book_case_tool_bar);
        int count = mBookCaseToolBar.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = mBookCaseToolBar.getChildAt(i);
            v.setOnClickListener(toolbarOnclick);
        }

        mListView = (ListView) findViewById(R.id.xlist_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isToolBarShow()) {
                    BookListAdapter.ViewHolder viewHolder = (BookListAdapter.ViewHolder) view.getTag();
                    viewHolder.changeCheckBox();
                } else {
                    BookInfo bookInfo = (BookInfo) booklistAdapter.getItem(position);
                    BookcasePresenter.readBook(getBaseContext(), bookInfo);
                }
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isToolBarShow()) {
                    BookInfo bookInfo = (BookInfo) booklistAdapter.getItem(position);
                    showCaseOperatorDialog(bookInfo);
                }
                return true;
            }
        });

    }

    public void showBookCaseToolBar() {
        mBookCaseToolBar.setVisibility(View.VISIBLE);
        booklistAdapter.setEdit(true);
        booklistAdapter.notifyDataSetChanged();

    }

    public boolean isToolBarShow() {
        return mBookCaseToolBar.getVisibility() == View.VISIBLE;
    }

    public void hideBookCaseToolBar() {
        mBookCaseToolBar.setVisibility(View.GONE);
        booklistAdapter.clear();
        booklistAdapter.setEdit(false);
        booklistAdapter.notifyDataSetChanged();
    }

    public void showCaseOperatorDialog(BookInfo bookInfo) {
        String bookName = bookInfo.bookDetail.getName();
        operatorListener.setBookInfo(bookInfo);
        String[] operator;
        if(bookInfo.bookDetail.topCase){
            operator = getResources().getStringArray(R.array.dialog_list_operator_top_cancel);
        }else{
            operator = getResources().getStringArray(R.array.dialog_list_operator_top);
        }
        showListDialog(bookName, operator, operatorListener);
    }

    public void hideCaseOperatorDialog() {
        hideListDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //解决阅读完后回到书架界面 当前章节显示未更新
        if (booklistAdapter != null) {
            if (isToolBarShow()) {
                hideBookCaseToolBar();
            }
            booklistAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isToolBarShow()) {
            hideBookCaseToolBar();
            //super.onBackPressed();
        } else {
            doubleClickBack();
        }
    }

    public void doubleClickBack() {
        if (click >= 1) {
            super.onBackPressed();
        } else {
            click++;
            showToast(R.string.double_click_cancel);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    click = 0;
                }
            }, 3000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent();
            intent.setClass(this, BookSearchActivity.class);
            startActivity(intent);
            return true;
        } else {
            int order = comparatorManager.getOrder(id);
            reOrderList(order);
        }

        return super.onOptionsItemSelected(item);
    }

    public void reOrderList() {
        int order = PreferenceSetting.getInstance(this).getIntValue(PreferenceSetting.KEY_CASE_LIST_ORDER);
        reOrderList(order);
    }

    /**
     * @param order 只能输入order,定义在PreferenceSetting中
     */
    public void reOrderList(int order) {
        ArrayList<BookInfo> bookInfoArrayList = booklistAdapter.getBookInfoList();
        Comparator<BookInfo> comparator = comparatorManager.getComparator(order);
        Collections.sort(bookInfoArrayList, comparator);
        PreferenceSetting.getInstance(this).putIntValue(PreferenceSetting.KEY_CASE_LIST_ORDER, order);
        booklistAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_clear_cache) {
            showConfirmDialog(R.string.delete_all_book, new DialogListenerIm() {
                @Override
                public void onConfirmAsync() {
                    super.onConfirmAsync();
                    FileManager.deleteAllCahce();
                }
            });
        } else if (id == R.id.nav_restore_config) {
            showConfirmDialog(R.string.restore_config, new DialogListenerIm() {
                @Override
                public void onConfirmAsync() {
                    super.onConfirmAsync();
                    PreferenceSetting.getInstance(MainActivity.this).clear();
                    PreferenceSetting.getInstance(MainActivity.this).setFirstRunFalse();
                }
            });
        } else if (id == R.id.nav_suggest_report) {
            FeedbackAPI.openFeedbackActivity();
        } else if (id == R.id.nav_inflater_book) {
            File[] files = FileManager.checkBookCache();
            if (files == null) {
                showToast(R.string.no_cache);
            } else {
                inflateBook(files);
            }
        } else if (id == R.id.nav_version) {
            Intent intent = new Intent();
            intent.setClass(this, DisclaimerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BookChapterCacheNew.getInstance().closeCache();
    }

    @Override
    public void loadBookStart() {
        showProgressDialog();
    }

    @Override
    public void loadBookFinish(ArrayList<BookInfo> bookList) {
        if(bookList != null) {
            booklistAdapter = new BookListAdapter(this);
            booklistAdapter.setBookInfoList(bookList);
            reOrderList();
            mListView.setAdapter(booklistAdapter);
        }
        hideProgressDialog();
    }

    @Override
    public void onCheckNewChapter(BookInfo bookInfo) {
        if (bookInfo != null) {
            NewChapterShow.getInstance().addNewChapter(bookInfo.bookDetail.getId(), bookInfo.bookChapter.getChapterCount() - 1);
            booklistAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCheckFinish() {
        if (NewChapterShow.getInstance().isHaveNewChapter()) {
            showToast(R.string.update_finish);
        } else {
            showToast(R.string.update_finish_no_new);
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCheckStart() {
        NewChapterShow.getInstance().clearNewChapterList();
    }

    public void topBook(BookInfo bookInfo) {
        bookInfo.bookDetail.setTopCase(true);
        reOrderList();
        booklistAdapter.notifyDataSetChanged();
    }

    public void topBook(long bookId) {
        BaiReadApplication application = (BaiReadApplication) getApplication();
        BookModel bookModel = application.getBookModel();
        BookInfo bookInfo = bookModel.getBookInfo(bookId);
        topBook(bookInfo);
    }

    public void topBook(ArrayList<Long> bookIdList) {
        BaiReadApplication application = (BaiReadApplication) getApplication();
        BookModel bookModel = application.getBookModel();
        for (long id : bookIdList) {
            BookInfo bookInfo = bookModel.getBookInfo(id);
            bookInfo.bookDetail.setTopCase(true);
        }
        reOrderList();
        booklistAdapter.notifyDataSetChanged();
    }

    class OperatorListener implements AdapterView.OnItemClickListener {
        private BookInfo bookInfo;

        private void setBookInfo(BookInfo bookInfo) {
            this.bookInfo = bookInfo;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case OPERATOR_TOP:
                    boolean topCase = bookInfo.bookDetail.isTopCase();
                    bookInfo.bookDetail.setTopCase(!topCase);
                    reOrderList();
                    bookcasePresenter.updateBook(bookInfo);
                    break;
                case OPERATOR_DETAIL:
                    BookSearchActivity.goBookDetail(getBaseContext(), bookInfo);
                    break;
                case OPERATOR_CACHE:
                    showConfirmDialog(R.string.cache_one_book_tips,new DialogListenerIm(){
                        @Override
                        public void onConfirm() {
                            super.onConfirm();
                            BookChapterCache.getInstance().downloadAllChpater(bookInfo);
                        }
                    });

                    break;
                case OPERATOR_DELETE:
                    showConfirmDialog(R.string.delete_one_book_tips,new DialogListenerIm(){
                        @Override
                        public void onConfirm() {
                            super.onConfirm();
                            booklistAdapter.getBookInfoList().remove(bookInfo);
                            booklistAdapter.notifyDataSetChanged();
                            bookcasePresenter.deleteBook(bookInfo);
                        }
                    });
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


}
