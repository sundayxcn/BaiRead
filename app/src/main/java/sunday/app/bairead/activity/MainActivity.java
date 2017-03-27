package sunday.app.bairead.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
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
import sunday.app.bairead.bookCase.BookListAdapter;
import sunday.app.bairead.bookCase.ComparatorManager;
import sunday.app.bairead.bookCase.FirstRunAsyncTask;
import sunday.app.bairead.database.BaiReadApplication;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.database.BookModel;
import sunday.app.bairead.download.BookChapterCache;
import sunday.app.bairead.presenter.BookcasePresenter;
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.utils.NewChapterShow;
import sunday.app.bairead.utils.PreferenceSetting;
import sunday.app.bairead.utils.TimeFormat;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, BookcasePresenter.IBookcasePresenterListener {


    public static final int OPERATOR_TOP = 0;
    public static final int OPERATOR_DETAIL = 1;
    public static final int OPERATOR_CACHE = 2;
    public static final int OPERATOR_DELETE = 3;
    public static final int OPERATOR_ALL = 4;

    public final String[] operatorStringArray = {"置顶", "书籍详情", "缓存全本", "删除本书", "批量操作"};
    Handler handler = new Handler();
    ComparatorManager comparatorManager = new ComparatorManager();
    private BookcasePresenter bookcasePresenter;
    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mBookCaseToolBar;
    private BookListAdapter booklistAdapter = new BookListAdapter(this);
    private OperatorListener operatorListener = new OperatorListener();
    private int click;
    private View.OnClickListener toolbarOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.book_case_tool_bar_top:
                    showConfirmDialog("确定将选中书籍置顶吗", new DialogListenerIm() {
                        @Override
                        public void onConfirm() {
                            super.onConfirm();
                            ArrayList<Long> bookIdList = booklistAdapter.getCheckList();
                            if (bookIdList != null) {
                                topBook(bookIdList);
                                onBackPressed();
                            }
                        }
                    });
                    break;
                case R.id.book_case_tool_bar_cache:
                    showConfirmDialog("确定缓存选中的书籍吗?建议在WIFI下缓存...", new DialogListenerIm() {
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
                    showConfirmDialog("确定删除选中的书籍吗?缓存也将一起删除且不能恢复...", new DialogListenerIm() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            PreferenceSetting.getInstance(this).setFirstRunFalse();
            firstRunWork();
        }


        setupView();
        bookcasePresenter = new BookcasePresenter(this, this);
        bookcasePresenter.init();

    }

    private void firstRunWork() {
        File[] files = FileManager.checkBookCache();
        if (files != null) {
            inflateBook(files);
        }
    }

    private void inflateBook(File[] files) {
        final int bookCount = files.length;
        if (bookCount > 0) {
            showConfirmDialog("检测到本地有缓存书籍，是否加载", "加载", "不加载", new DialogListenerIm() {
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
        swipeRefreshLayout.setColorSchemeColors(0xFFFF0000);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnect()) {
                    bookcasePresenter.checkNewChapter(booklistAdapter.getBookInfoList());
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showToastNetworkUnconnect();
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
        if (bookInfo.bookDetail.topCase) {
            operatorStringArray[0] = "取消置顶";
        } else {
            operatorStringArray[0] = "置顶";
        }


        showListDialog(bookName, operatorStringArray, operatorListener);
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
            showToast("再按一次退出应用");
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
            showConfirmDialog("是否删除所有的数据缓存", new DialogListenerIm() {
                @Override
                public void onConfirmAsync() {
                    super.onConfirmAsync();
                    FileManager.deleteAllCahce();
                }
            });
        } else if (id == R.id.nav_restore_config) {
            showConfirmDialog("恢复默认设置", new DialogListenerIm() {
                @Override
                public void onConfirmAsync() {
                    super.onConfirmAsync();
                    StringBuffer fileName = new StringBuffer("/data/data/");
                    fileName.append(getPackageName().toString())
                            .append("/shared_prefs/")
                            .append(getPackageName().toString())
                            .append("_preferences.xml");
                    FileManager.deleteFile(fileName.toString());
                    PreferenceSetting.getInstance(MainActivity.this).setFirstRunFalse();
                }
            });
        } else if (id == R.id.nav_suggest_report) {
            FeedbackAPI.openFeedbackActivity();
        } else if (id == R.id.nav_infalter_book) {
            File[] files = FileManager.checkBookCache();
            if (files == null) {
                showToast("本地无缓存");
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
    }

    @Override
    public void loadBookStart() {
        showProgressDialog();
    }

    @Override
    public void loadBookFinish(ArrayList<BookInfo> bookList) {
        booklistAdapter = new BookListAdapter(this);
        booklistAdapter.setBookInfoList(bookList);
        reOrderList();
        mListView.setAdapter(booklistAdapter);
        hideProgressDialog();
    }

    @Override
    public void onCheckNewChapter(BookInfo bookInfo) {
        if (bookInfo != null) {
            NewChapterShow.getInstance().addNewChapter(bookInfo.bookDetail.getId(), bookInfo.bookChapter.getChapterCount() - 1);
        }
        booklistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckFinish() {
        if (NewChapterShow.getInstance().isHaveNewChapter()) {
            showToast("更新完毕");
        } else {
            showToast("更新完毕,无新章节");
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
                    BookChapterCache.getInstance().downloadAllChpater(bookInfo);
                    break;
                case OPERATOR_DELETE:
                    booklistAdapter.getBookInfoList().remove(bookInfo);
                    booklistAdapter.notifyDataSetChanged();
                    bookcasePresenter.deleteBook(bookInfo);
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
