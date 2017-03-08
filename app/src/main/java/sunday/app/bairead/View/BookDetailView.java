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

/**
 * 搜索点击的书籍详情页，供用户操作
 * */
public class BookDetailView extends LinearLayout {

    private TextView mDescriptionTView;
    private TextView mTimeTView;
    private Button mBookcaseBView;
    private Button mBookReadBView;
    private Button mBookCacheBView;
    private BookInfo bookInfo;

    private BookModel bookModel;

    public BookDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BaiReadApplication application  = (BaiReadApplication) getContext().getApplicationContext();
        bookModel = application.getBookModel();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupView();
    }



    public void setupView(){
        mDescriptionTView  = (TextView) findViewById(R.id.search_book_detail_description);
        mTimeTView = (TextView) findViewById(R.id.search_book_detail_chapter_time);
        mBookcaseBView = (Button) findViewById(R.id.search_book_detail_button_bookcase);
        mBookReadBView = (Button) findViewById(R.id.search_book_detail_button_read);
        mBookCacheBView = (Button) findViewById(R.id.search_book_detail_button_cache);
        mBookcaseBView.setOnClickListener(onClickListener);
        mBookReadBView.setOnClickListener(onClickListener);
        mBookCacheBView.setOnClickListener(onClickListener);
    }


    public void setInfo(BookInfo bookInfo){
        this.bookInfo = bookInfo;
        Spanned spanned = Html.fromHtml(bookInfo.bookDetail.getDescription());
        mDescriptionTView.setText(spanned);
        mTimeTView.setText(bookInfo.bookDetail.getUpdateTime());

        if(bookModel.isCase(bookInfo.bookDetail)){
            mBookcaseBView.setText(R.string.search_book_detail_button_bookcase_add_text);
            mBookcaseBView.setEnabled(false);
        }

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

                    //BookcasePresenter.readBook(application,bookInfo.bookDetail.getId());
                    break;
                case R.id.search_book_detail_button_bookcase:
                    mBookcaseBView.setText(R.string.search_book_detail_button_bookcase_add_text);
                    mBookcaseBView.setEnabled(false);
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
