package sunday.app.bairead.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.R;

/**
 * Created by sunday on 2016/12/9.
 */

public class BookcaseView extends RelativeLayout {
    private TextView mBookNameTview;
    private TextView mBookChapterLatestTview;
    private TextView mBookChapterIndexTview;
    private TextView mBookChapterUpdateTview;
    private TextView mBookUpdateTimeTview;


    public BookcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookcaseView(Context context) {
        super(context, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupView();
    }

    private void setupView(){
        mBookNameTview = (TextView) findViewById(R.id.xlist_item_name);
        mBookChapterLatestTview = (TextView) findViewById(R.id.xlist_item_chapter_latest);
        mBookChapterIndexTview = (TextView) findViewById(R.id.xlist_item_chapter_index);
        mBookChapterUpdateTview = (TextView) findViewById(R.id.xlist_item_chapter_update);
        mBookUpdateTimeTview = (TextView) findViewById(R.id.xlist_item_update_time);
    }

    public void setData(BookInfo bookInfo){
        String name = bookInfo.bookDetail.getName();
        String chapterLatest = bookInfo.bookDetail.getChapterLatest();
        int chapterIndex = bookInfo.bookChapter.getChapterIndex() + 1;
        int chapterCount = bookInfo.bookChapter.getChapterCount();
        String chapterText = String.valueOf(chapterIndex)+"/"+String.valueOf(chapterCount);
        mBookNameTview.setText(name);
        mBookChapterLatestTview.setText(chapterLatest);
        mBookChapterIndexTview.setText(chapterText);
        mBookUpdateTimeTview.setText(bookInfo.bookDetail.getUpdateTime());
        setTag(bookInfo.bookDetail.getId());
    }

    public void setUpdate(){
        mBookChapterUpdateTview.setVisibility(VISIBLE);
    }

    public void hideUpdate(){
        mBookChapterUpdateTview.setVisibility(INVISIBLE);
    }

}
