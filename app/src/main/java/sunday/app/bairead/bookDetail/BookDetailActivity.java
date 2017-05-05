package sunday.app.bairead.bookDetail;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import sunday.app.bairead.base.BaseActivity;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.R;
import sunday.app.bairead.utils.Temp;
import sunday.app.bairead.bookDetail.view.BookStatusView;
import sunday.app.bairead.bookDetail.view.BookTypeView;

/**
 * Created by sunday on 2017/3/7.
 */

public class BookDetailActivity extends BaseActivity implements BookDetailContract.View{

    private TextView nameTView;
    private TextView authorTView;
    private TextView sourceTView;
    private TextView chapterLatestTView;
    private TextView chapterTimeTView;
    private TextView mDescriptionTView;
    private BookTypeView bookTypeView;
    private BookStatusView bookStatusView;

    private TextView mButtonReadView;
    private TextView mButtonCaseView;
    private TextView mButtonCahceView;


    private BookDetailContract.Presenter mPresenter;
    private BookInfo mBookInfo;
    //private BookDetailPresenter bookDetailPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail_activity);
        mBookInfo = Temp.getInstance().getBookInfo();
        //Temp.getInstance().clearBookInfo();
        //bookDetailPresenter = new BookDetailPresenter(this,this);
        mPresenter = new BookDetailPresenter(this);
        setTitle("图书简介");
        setupView();
        initView();

    }
    private void setupView(){
        nameTView = (TextView)findViewById(R.id.book_detail_activity_name);
        authorTView = (TextView)findViewById(R.id.book_detail_activity_author);
        authorTView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        sourceTView = (TextView)findViewById(R.id.book_detail_activity_source);
        chapterLatestTView = (TextView)findViewById(R.id.book_detail_activity_chapter_latest);
        chapterTimeTView = (TextView)findViewById(R.id.book_detail_activity_chapter_time);

        bookTypeView = (BookTypeView) findViewById(R.id.book_detail_activity_type);
        bookStatusView = (BookStatusView) findViewById(R.id.book_detail_activity_status);
        mDescriptionTView = (TextView) findViewById(R.id.book_detail_activity_description);

        mButtonReadView = (TextView) findViewById(R.id.book_detail_activity_button_read);
        mButtonCaseView = (TextView) findViewById(R.id.book_detail_activity_button_case);
        mButtonCahceView = (TextView) findViewById(R.id.book_detail_activity_button_cache);
        mButtonReadView.setOnClickListener(onClickListener);
        mButtonCaseView.setOnClickListener(onClickListener);
        mButtonCahceView.setOnClickListener(onClickListener);
        //authorTView.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.book_detail_activity_button_read:
                    BookDetailPresenter.readBook(getApplicationContext(),mBookInfo);
                    break;
                case R.id.book_detail_activity_button_case:
                    BookDetailPresenter.addToBookCase(getApplicationContext(),mBookInfo);
                    mButtonCaseView.setEnabled(false);
                    break;
                case R.id.book_detail_activity_button_cache:
                    BookDetailPresenter.cacheBook(mBookInfo);
                    break;
                //case R.id.book_detail_activity_author:
                default:
                    break;
            }
        }
    };

    private void initView(){
        nameTView.setText(mBookInfo.bookDetail.getName());
        authorTView.setText(mBookInfo.bookDetail.getAuthor());
        chapterLatestTView.setText(mBookInfo.bookDetail.getChapterLatest());
        chapterTimeTView.setText(mBookInfo.bookDetail.getUpdateTime());
        mDescriptionTView.setText(mBookInfo.bookDetail.getDescription());
        long id = mBookInfo.bookDetail.getId();
        if(BookRepository.getInstance(this).getBook(id) != null) {
            mButtonCaseView.setEnabled(false);
        }

        int type = mBookInfo.bookDetail.getType();
        bookTypeView.setType(type);
        boolean status = mBookInfo.bookDetail.isStatus();
        bookStatusView.setStatus(status);
    }

    @Override
    public void setPresenter(BookDetailContract.Presenter presenter) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
