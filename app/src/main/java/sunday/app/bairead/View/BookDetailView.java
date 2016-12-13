package sunday.app.bairead.View;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.R;

/**
 * Created by sunday on 2016/12/8.
 */

public class BookDetailView extends LinearLayout {

    private TextView mDescriptionTView;
    private TextView mTimeTView;

    private BookInfo bookInfo;

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

        int count = getChildCount();
        for(int i = 0 ; i< count;i++){
            View view = getChildAt(i);
            if(view instanceof Button){
                view.setOnClickListener(onClickListener);
            }
        }

    }


    public void setInfo(BookInfo bookInfo){
        this.bookInfo = bookInfo;
        Spanned spanned = Html.fromHtml(bookInfo.bookDetail.getDescription());
        mDescriptionTView.setText(spanned);
        mTimeTView.setText(bookInfo.bookDetail.getUpdateTime());
    }

    public void animatorShow(boolean animator){
        if(animator){

        }else{
            setVisibility(VISIBLE);
        }
    }

    public void animatorHide(boolean animator){
        if(animator){

        }else{
            setVisibility(GONE);
        }
    }


    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            BaiReadApplication application  = (BaiReadApplication) getContext().getApplicationContext();
            BookModel bookModel = application.getBookModel();

            switch(v.getId()){
                case R.id.search_book_detail_button_read:
                    break;
                case R.id.search_book_detail_button_bookcase:
                    bookModel.addBook(bookInfo);
                    break;
                case R.id.search_book_detail_button_cache:
                    //BookInfo bookInfo = v.get
                    bookModel.addBook(bookInfo);
                    break;
                default:

            }
        }
    };
}
