package sunday.app.bairead.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;
import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.Download.OKhttpManager;
import sunday.app.bairead.Parse.BookChapterParse;
import sunday.app.bairead.Parse.JsoupParse;
import sunday.app.bairead.R;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.View.BookcaseView;
import sunday.app.bairead.View.XListView;

/**
 * Created by sunday on 2016/12/15.
 */

public class BookcaseControl implements BookModel.CallBack,XListView.IXListViewListener{
    private MainActivity activity;

    private XListView mListView;
    private XListAdapter mAdapter;
    private static int refreshCnt = 0;
    private Handler mHandler = new Handler();
    private int start = 0;
    private ArrayList<BookInfo> mBookInfoList = new ArrayList<>();

    public BookcaseControl( MainActivity context){
        activity = context;
        mListView = (XListView) activity.findViewById(R.id.xlist_view);
        mListView.setPullLoadEnable(false);
        mAdapter = new XListAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long bookId = mBookInfoList.get(position - 1 ).bookDetail.getId();
                readBook(activity,bookId);
            }
        });

    }


    public static void readBook(Context context,long bookId){
        Intent intent = new Intent();
        intent.setClass(context,BookReadActivity.class);
        intent.putExtra(BookReadActivity.EXTRAS_BOOK_ID,bookId);
        context.startActivity(intent);
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("刚刚");
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void loadFinish(ArrayList<BookInfo> list) {
        mBookInfoList.clear();
        mBookInfoList.addAll(list);
        mAdapter.notifyDataSetChanged();
        checkAllNewChapter();

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


    private void checkNewChapter(final BookInfo bookInfo){
        String url = bookInfo.bookChapter.getChapterLink();
        OKhttpManager.getInstance().connectUrl(url, new OKhttpManager.ConnectListener() {
            @Override
            public void start(String url) {

            }

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.e("sunday","checkNewChapter-");
                //Response cacheResponse = response.cacheResponse();
                Response netWorkResponse = response.networkResponse();
                if(netWorkResponse != null){
                    final String chapterFile = FileManager.PATH +"/"+bookInfo.bookDetail.getName()+"/" + BookChapter.FileName;
                    FileManager.writeByte(chapterFile,netWorkResponse.body().bytes());
                    bookInfo.bookChapter = JsoupParse.from(chapterFile, new BookChapterParse());
                    //newChapterList.add(bookInfo);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            BookcaseView bookcaseView = indexOf(bookInfo.bookDetail.getId());
                            if(bookcaseView != null){
                                bookcaseView.setData(bookInfo);
                                bookcaseView.setUpdate();
                            }
                        }
                    });
                }
            }
        });
    }


    //private ArrayList<BookInfo> newChapterList = new ArrayList<>();

    public void checkAllNewChapter(){
        for(BookInfo bookInfo : mBookInfoList ) {
            checkNewChapter(bookInfo);
        }
    }





    public BookcaseView indexOf(long bookId){
        int count = mListView.getChildCount();
        for(int i = 0;i<count;i++){
            View view = mListView.getChildAt(i);
            if(view instanceof BookcaseView){
                long id = (long) view.getTag();
                if(id == bookId){
                    return (BookcaseView) view;
                }
            }
        }
        return  null;
    }


}
