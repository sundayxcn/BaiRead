package sunday.app.bairead.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.R;

/**
 * Created by sunday on 2016/12/8.
 */

public class BookDetailView extends LinearLayout {

    private TextView mDescriptionTView;
    private TextView mTimeTView;
//    private Button mReadBView;
//    private Button mBookcaseBView;
//    private Button mCacheBView;

    public BookDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupView();
    }



    public void setupView(){
        mDescriptionTView  = (TextView) findViewById(R.id.search_book_detail_description);
        mTimeTView = (TextView) findViewById(R.id.search_book_detail_chapter_time);
//        mReadBView = (Button) findViewById(R.id.search_book_detail_button_read);
//        mBookcaseBView = (Button) findViewById(R.id.search_book_detail_button_bookcase);
//        mCacheBView = (Button) findViewById(R.id.search_book_detail_button_cache);
        int count = getChildCount();
        for(int i = 0 ; i< count;i++){
            View view = getChildAt(i);
            if(view instanceof Button){
                view.setOnClickListener(onClickListener);
            }
        }

    }


    public void setDetail(BookDetail bookDetail){
        mDescriptionTView.setText(bookDetail.getDescription());
        //mTimeTView.setText(bookDetail.chapterTime);
    }

    public void animatorShow(boolean animator){
        if(animator){

        }else{
            setVisibility(VISIBLE);
        }
    }


    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.search_book_detail_button_read:
                    break;
                case R.id.search_book_detail_button_bookcase:
                    break;
                case R.id.search_book_detail_button_cache:
                    break;
                default:

            }
        }
    };
}
