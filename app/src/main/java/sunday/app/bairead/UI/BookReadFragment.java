package sunday.app.bairead.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import sunday.app.bairead.DataBase.BookDetail;

/**
 * Created by sunday on 2016/12/8.
 */

public class BookReadFragment extends Fragment {

    private BookDetail mBookDetail;
    private ArrayList<HashMap> mChapterList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setBookDetail(BookDetail bookDetail){
        String chapterUrl = bookDetail.chapterUrl;
    }

    public void read(){

    }

}
