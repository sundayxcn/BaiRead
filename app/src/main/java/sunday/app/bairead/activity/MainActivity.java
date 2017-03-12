package sunday.app.bairead.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import sunday.app.bairead.database.BookChapter;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.R;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseDetail;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.presenter.BookDetailPresenter;
import sunday.app.bairead.tool.FileManager;
import sunday.app.bairead.tool.NetworkTool;
import sunday.app.bairead.presenter.BookcasePresenter;
import sunday.app.bairead.tool.NewChapterShow;
import sunday.app.bairead.tool.PreferenceSetting;
import sunday.app.bairead.tool.TimeFormat;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, BookcasePresenter.IBookcasePresenterListener {

    private NetworkTool networkTool = new NetworkTool(this);
    private BookcasePresenter bookcasePresenter;
    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BookListAdapter booklistAdapter = new BookListAdapter();

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


        if(PreferenceSetting.getInstance(this).isFirstRun()){
            PreferenceSetting.getInstance(this).setFirstRunFalse();
            firstRunWork();
        }


        setupView();
        bookcasePresenter = new BookcasePresenter(this, this);
        bookcasePresenter.init();
        registerReceiver();
        
    }


    private void firstRunWork(){
        final File baseDir = new File(FileManager.PATH);
        if(baseDir.exists()){
            final int bookCount = baseDir.listFiles().length;
            if(bookCount > 0) {
                showConfirmDialog("检测到本地有缓存书籍，是否加载", "加载", "不加载", new DialogListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirmed() {
                        new FirstRunAsyncTask(baseDir).execute();
                    }
                });
            }
        }

    }

    private void setupView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(0xFFFF0000);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookcasePresenter.checkNewChapter(booklistAdapter.getBookInfoList());
            }
        });
        mListView = (ListView) findViewById(R.id.xlist_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookInfo bookInfo = (BookInfo) booklistAdapter.getItem(position);
                BookcasePresenter.readBook(getBaseContext(), bookInfo);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int fPosition = position;
                showConfirmDialog("是否从书架中删除","确定","取消",new DialogListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirmed() {
                        BookInfo bookInfo = (BookInfo) booklistAdapter.getItem(fPosition);
                        booklistAdapter.getBookInfoList().remove(bookInfo);
                        booklistAdapter.notifyDataSetChanged();

                        bookcasePresenter.deleteBook(bookInfo);
                    }

                });
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //解决阅读完后回到书架界面 当前章节显示未更新
        if(booklistAdapter != null){
            booklistAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search) {
            Intent intent = new Intent();
            intent.setClass(this, BookSearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_clear_cache) {

        } else if (id == R.id.nav_restore_config) {
            FeedbackAPI.openFeedbackActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    public void registerReceiver() {
        networkTool.addReceiver();
    }

    public void unRegisterReceiver() {
        networkTool.removeReceiver();
    }

    @Override
    public void loadBookStart() {
        showProgressDialog();
    }

    @Override
    public void loadBookFinish(ArrayList<BookInfo> bookList) {
        booklistAdapter = new BookListAdapter();
        booklistAdapter.setBookInfoList(bookList);
        mListView.setAdapter(booklistAdapter);
        hideProgressDialog();
    }

    @Override
    public void onCheckNewChapter(BookInfo bookInfo) {
        if(bookInfo != null) {
            NewChapterShow.getInstance().addNewChapter(bookInfo.bookDetail.getId(), bookInfo.bookChapter.getChapterCount() - 1);
        }
        booklistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckFinish() {
        if(NewChapterShow.getInstance().isHaveNewChapter()){
            showToast("更新完毕");
        }else{
            showToast("更新完毕,无新章节");
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCheckStart() {
        NewChapterShow.getInstance().clearNewChapterList();
    }

    class ViewHolder {
        TextView nameTView;
        TextView chapterLatestTView;
        TextView chapterIndexTView;
        TextView updateImageTView;
        TextView updateTimeTView;
        private long bookId;

        ViewHolder(ViewGroup parent) {
            nameTView = (TextView) parent.findViewById(R.id.xlist_item_name);
            chapterLatestTView = (TextView) parent.findViewById(R.id.xlist_item_chapter_latest);
            chapterIndexTView = (TextView) parent.findViewById(R.id.xlist_item_chapter_index);
            updateImageTView = (TextView) parent.findViewById(R.id.xlist_item_chapter_update);
            updateTimeTView = (TextView) parent.findViewById(R.id.xlist_item_update_time);
        }

        public void setValue(BookInfo bookInfo) {
            String name = bookInfo.bookDetail.getName();
            String chapterLatest = bookInfo.bookDetail.getChapterLatest();
            int chapterIndex = 0;//bookInfo.bookChapter.getChapterIndex() + 1;
            int chapterCount = 0;//bookInfo.bookChapter.getChapterCount();
            String chapterText = String.valueOf(chapterIndex) + "/" + String.valueOf(chapterCount);
            nameTView.setText(name);
            chapterLatestTView.setText(chapterLatest);
            chapterIndexTView.setText(chapterText);

            String timeString = TimeFormat.getTimeString(bookInfo.bookDetail.getUpdateTime());
            updateTimeTView.setText(timeString);
            boolean newChapter = NewChapterShow.getInstance().isHaveNewChapter(bookInfo.bookDetail.getId());
            updateImageTView.setVisibility(newChapter ? View.VISIBLE : View.INVISIBLE);
            bookId = bookInfo.bookDetail.getId();
        }

        public long getBookId() {
            return bookId;
        }
    }

    public class BookListAdapter extends BaseAdapter {

        private ArrayList<BookInfo> bookInfos;

        public ArrayList<BookInfo> getBookInfoList() {
            return bookInfos;
        }

        public void setBookInfoList(ArrayList<BookInfo> list) {
            bookInfos = list;
        }

        @Override
        public int getCount() {
            return bookInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return bookInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.xlist_item, null);
                ViewHolder viewHolder = new ViewHolder((ViewGroup) convertView);
                convertView.setTag(viewHolder);
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.setValue(bookInfos.get(position));
            return convertView;
        }
    }


    private class FirstRunAsyncTask extends AsyncTask<Void,String,Void>{

        private File baseDir;

        FirstRunAsyncTask(File fileDir){
            baseDir = fileDir;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            BookDetailPresenter bookDetailPresenter = new BookDetailPresenter(getBaseContext(), null);

            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if(pathname.isDirectory() && !pathname.getName().contains("cache")) {
                        return true;
                    }else{
                        return false;
                    }

                }
            };
            File[] fileDirs = baseDir.listFiles(fileFilter);
            int bookCount = fileDirs.length;
            int i = 1;
            for (File fileDir : fileDirs) {
                String fileName = fileDir.getAbsolutePath() + "/" + BookChapter.FileName;
                File file = new File(fileName);
                if (file.exists()) {
                    BookInfo bookInfo = new BookInfo();
                    bookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).from(file).parse();
                    bookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).from(file).parse();
                    bookDetailPresenter.addToBookCase(bookInfo);
                    StringBuffer stringBuffer = new StringBuffer("加载第");
                    stringBuffer
                            .append(i)
                            .append('/')
                            .append(bookCount)
                            .append("本书");
                    publishProgress(stringBuffer.toString());
                    i++;
                }
            }
            return null;
        }

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
    }

}
