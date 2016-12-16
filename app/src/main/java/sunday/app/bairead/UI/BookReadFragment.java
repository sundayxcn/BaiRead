package sunday.app.bairead.UI;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.Download.BookCacheManager;
import sunday.app.bairead.Download.OKhttpManager;
import sunday.app.bairead.R;
import sunday.app.bairead.View.BookTextView;

/**
 * Created by sunday on 2016/12/8.
 */

public class BookReadFragment extends Fragment implements BookCacheManager.ChapterListener {

    private BookTextView mBookTextTview;

    private BookInfo bookInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_read_fragment, container, false);
        mBookTextTview = (BookTextView) view.findViewById(R.id.book_read_fragment_book_text);
        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setBookInfo(BookInfo bookInfo){
        this.bookInfo = bookInfo;
    }

    @Override
    public void onResume() {
        super.onResume();
        BookCacheManager.getInstance().getChapterText(bookInfo,this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void show(Activity activity){
        FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack("bookRead");
        fragmentTransaction.add(R.id.drawer_layout,this).show(this).commit();
    }

    public void hide(){
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.remove(BookReadFragment.this);
        fragmentTransaction.commit();
        fragmentManager.popBackStack();
    }

    @Override
    public void end(Spanned text) {
        mBookTextTview.setText(text);
    }
}
