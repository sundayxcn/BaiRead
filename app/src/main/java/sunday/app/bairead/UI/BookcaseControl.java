package sunday.app.bairead.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;
import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.Download.OKhttpManager;
import sunday.app.bairead.Parse.ParseChapter;
import sunday.app.bairead.Parse.ParseDetail;
import sunday.app.bairead.Parse.ParseXml;
import sunday.app.bairead.R;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.View.BookcaseView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by sunday on 2016/12/15.
 */

public class BookcaseControl implements BookModel.CallBack {
    private MainActivity activity;

    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private XListAdapter mAdapter;
    private Handler mHandler = new Handler();
    private ArrayList<BookInfo> mBookInfoList = new ArrayList<>();

    private AlertDialog mDeleteBookDialog;


    private int bookCount;

    synchronized void updateBookBegin() {
        bookCount = mBookInfoList.size();
    }

    synchronized void updateBookReduce(final String bookTitle) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "已更新" + bookTitle, Toast.LENGTH_SHORT).show();
                bookCount--;
                if (bookCount == 0) {
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public BookcaseControl(MainActivity context) {
        activity = context;

        swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout);
        //swipeRefreshLayout.set
        swipeRefreshLayout.setColorSchemeColors(0xFFFF0000);
        //swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkAllNewChapter();
                updateBookBegin();
            }
        });
        //swipeRefreshLayout.setProgressBackgroundColorSchemeColor(activity.getResources().);
        mListView = (ListView) activity.findViewById(R.id.xlist_view);
        //mListView.setPullLoadEnable(false);
        mAdapter = new XListAdapter();
        mListView.setAdapter(mAdapter);
        // mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBookId = mBookInfoList.get(position).bookDetail.getId();
                readBook(activity, mBookId);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //final BookInfo bookInfo = mBookInfoList.get(position);
                if (mDeleteBookDialog == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("是否从书架中移除")
                            .setNegativeButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //the position has problem
                                    BookInfo bookInfo = mBookInfoList.get(position);
                                    BaiReadApplication application = (BaiReadApplication) activity.getApplication();
                                    BookModel bookModel = application.getBookModel();
                                    bookModel.deleteBook(bookInfo);
                                    mDeleteBookDialog.dismiss();
                                }
                            })
                            .setPositiveButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDeleteBookDialog.dismiss();
                                }
                            });
                    mDeleteBookDialog = builder.create();
                }
                mDeleteBookDialog.show();
                return true;
            }
        });
    }

    private long mBookId;

    public static void readBook(Context context, long bookId) {
        Intent intent = new Intent();
        intent.setClass(context, BookReadActivity.class);
        intent.putExtra(BookReadActivity.EXTRAS_BOOK_ID, bookId);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void loadFinish(ArrayList<BookInfo> list) {
        mBookInfoList.clear();
        mBookInfoList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addBookDataFinish(BookInfo bookInfo, boolean success) {
        if (success) {
            mBookInfoList.add(bookInfo);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteBookDataFinish(BookInfo bookInfo, boolean success) {
        if (success) {
            mBookInfoList.remove(bookInfo);
            mAdapter.notifyDataSetChanged();

            //删除本地缓存
            FileManager.deleteFolder(FileManager.PATH + "/" + bookInfo.bookDetail.getName());
        }
    }

    public class XListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBookInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                BookcaseView bookcaseView = (BookcaseView) LayoutInflater.from(activity).inflate(R.layout.xlist_item, null);
                bookcaseView.setData(mBookInfoList.get(position));
                convertView = bookcaseView;
            }
            return convertView;
        }
    }


    private void checkNewChapter(final BookInfo bookInfo) {
        String url = bookInfo.bookChapter.getChapterLink();
        OKhttpManager.getInstance().connectUrl(url, new OKhttpManager.ConnectListener() {
            @Override
            public void start(String url) {

            }

            @Override
            public void onFailure(Call call, IOException e) {
                updateBookReduce("网络超时");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.e("sunday","checkNewChapter-");
                //Response cacheResponse = response.cacheResponse();
                //Response netWorkResponse = response.networkResponse();
                if (response != null && response.body() != null) {
                    final String chapterFile = FileManager.PATH + "/" + bookInfo.bookDetail.getName() + "/" + BookChapter.FileName;
                    FileManager.writeByte(chapterFile, response.body().bytes());
                    response.body().close();
                    BookDetail bookDetail = ParseXml.createParse(ParseDetail.class).parse(chapterFile);
                    BookChapter bookChapter = ParseXml.createParse(ParseChapter.class).parse(chapterFile);
                    //bookInfo.bookChapter
                    if (bookInfo.bookChapter.getChapterCount() != bookChapter.getChapterCount()) {
                        bookInfo.bookChapter.setChapterList(bookChapter.getChapterList());
                        bookInfo.bookDetail.setUpdateTime(bookDetail.getUpdateTime());
                        bookInfo.bookDetail.setChapterLatest(bookInfo.bookChapter.getLastChapter().getTitle());
                        //newChapterList.add(bookInfo);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                BookcaseView bookcaseView = indexOf(bookInfo.bookDetail.getId());
                                if (bookcaseView != null) {
                                    bookcaseView.setData(bookInfo);
                                    bookcaseView.setUpdate();
                                }
                            }
                        });
                    }
                }
                updateBookReduce(bookInfo.bookDetail.getName());
            }
        });
    }


    //private ArrayList<BookInfo> newChapterList = new ArrayList<>();

    public void checkAllNewChapter() {
        for (BookInfo bookInfo : mBookInfoList) {
            checkNewChapter(bookInfo);
        }
    }


    public void refreshIndex() {
        BookcaseView bookcaseView = indexOf(mBookId);
        BaiReadApplication application = (BaiReadApplication) activity.getApplication();
        BookModel bookModel = application.getBookModel();
        BookInfo bookInfo = bookModel.getBookInfo(mBookId);
        if (bookcaseView != null) {
            bookcaseView.setData(bookInfo);
            bookcaseView.hideUpdate();
        }
    }


    public BookcaseView indexOf(long bookId) {
        int count = mListView.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = mListView.getChildAt(i);
            if (view instanceof BookcaseView) {
                long id = (long) view.getTag();
                if (id == bookId) {
                    return (BookcaseView) view;
                }
            }
        }
        return null;
    }


}
