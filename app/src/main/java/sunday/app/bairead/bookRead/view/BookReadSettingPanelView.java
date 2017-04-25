package sunday.app.bairead.bookRead.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sunday.app.bairead.R;
import sunday.app.bairead.bookRead.BookReadActivity;
import sunday.app.bairead.bookRead.BookReadChapterFragment;
import sunday.app.bairead.bookRead.BookReadContract;
import sunday.app.bairead.bookRead.BookReadMarkFragment;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.PreferenceSetting;


/**
 * Created by sunday on 2017/2/23.
 */

public class BookReadSettingPanelView extends RelativeLayout implements BookReadContract.ViewSetting {

    RelativeLayout settingTopPanel;
    LinearLayout settingBottomPanel;
    private BookReadSize mBookReadSize;
    private LinearLayout mBookSizeSetting;
    private BookReadContract.Presenter mPresenter;
    private OnClickListener buttonOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.book_read_setting_panel_chapter_menu:
                    mPresenter.goToChapterMenu();
                    break;
                case R.id.book_read_setting_panel_book_mark:
                    mPresenter.goToMarkMenu();
                    break;
                case R.id.book_read_setting_panel_text_font:
                    showBookTextSizePanel();
                    break;
                case R.id.book_read_setting_panel_more:
                    break;
                case R.id.book_read_setting_panel_mark_add:
                    mPresenter.addBookMark();
                    break;
                case R.id.book_read_setting_top_panel_add_case:
                    mPresenter.addBook();
                    break;
                case R.id.book_read_setting_top_panel_cache_book:
                    mPresenter.downBook();

                default:
                    break;
            }
        }
    };


    private OnClickListener sizeOnReduceClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String key = (String) v.getTag();
            if (key.equals(PreferenceSetting.KEY_TEXT_SIZE)) {
                if (mBookReadSize.textSize > 30) {
                    mBookReadSize.textSize -= 4;
                }
            } else if (key.equals(PreferenceSetting.KEY_LINE_SIZE)) {
                if (mBookReadSize.lineSize > 6) {
                    mBookReadSize.lineSize -= 6;
                }
            } else if (key.equals(PreferenceSetting.KEY_MARGIN_SIZE)) {
                if (mBookReadSize.marginSize > 6) {
                    mBookReadSize.marginSize -= 6;
                }
            }
            //mReadSizeListener.onReadSize(mBookReadSize);
            mPresenter.updateTextSize(mBookReadSize);
        }
    };
    private OnClickListener sizeOnAddClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String key = (String) v.getTag();
            if (key.equals(PreferenceSetting.KEY_TEXT_SIZE)) {
                mBookReadSize.textSize += 4;
            } else if (key.equals(PreferenceSetting.KEY_LINE_SIZE)) {
                mBookReadSize.lineSize += 6;
            } else if (key.equals(PreferenceSetting.KEY_MARGIN_SIZE)) {
                mBookReadSize.marginSize += 6;
            }
            //mReadSizeListener.onReadSize(mBookReadSize);
            mPresenter.updateTextSize(mBookReadSize);
        }
    };

    public BookReadSettingPanelView(Context context) {
        super(context);
    }

    public BookReadSettingPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookReadSettingPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        settingTopPanel = (RelativeLayout) findViewById(R.id.book_read_setting_panel_top_panel);
        settingBottomPanel = (LinearLayout) findViewById(R.id.book_read_setting_panel_bottom_panel);
        setOnClick(settingTopPanel, buttonOnClickListener);
        setOnClick(settingBottomPanel, buttonOnClickListener);
    }

    public void setPresenter(BookReadContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showToast(@NonNull @StringRes int resId) {

    }

    private void setOnClick(ViewGroup viewGroup, OnClickListener onClickListener) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = viewGroup.getChildAt(i);
            v.setOnClickListener(onClickListener);
        }

    }

    private void setupTypeView(View parent, int imgLeftId, int imgRightId, int titleId, String key) {
        TextView titleView = (TextView) parent.findViewById(R.id.book_read_setting_size_line_title);
        ImageView reduceButton = (ImageView) parent.findViewById(R.id.book_read_setting_size_line_button_reduce);
        reduceButton.setImageResource(imgLeftId);
        ImageView addButton = (ImageView) parent.findViewById(R.id.book_read_setting_size_line_button_add);
        addButton.setImageResource(imgRightId);
        titleView.setText(titleId);
        reduceButton.setTag(key);
        addButton.setTag(key);
        reduceButton.setOnClickListener(sizeOnReduceClickListener);
        addButton.setOnClickListener(sizeOnAddClickListener);
    }

    private void showBookTextSizePanel() {
        mBookSizeSetting = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.book_read_setting_size_panel, null, false);
        PreferView[] preferViews = new PreferView[]{
                new PreferView(R.drawable.ic_font_reduce, R.drawable.ic_font_add, R.string.book_read_preference_title_font, PreferenceSetting.KEY_TEXT_SIZE),
                new PreferView(R.drawable.ic_line_reduce, R.drawable.ic_line_add, R.string.book_read_preference_title_line, PreferenceSetting.KEY_LINE_SIZE),
                new PreferView(R.drawable.ic_margin_reduce, R.drawable.ic_margin_add, R.string.book_read_preference_title_margin, PreferenceSetting.KEY_MARGIN_SIZE),
        };


        int childCount = mBookSizeSetting.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mBookSizeSetting.getChildAt(i);
            setupTypeView(view, preferViews[i].imgLeftId, preferViews[i].imgRightId, preferViews[i].titleId, preferViews[i].prefKey);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mBookSizeSetting, layoutParams);

    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        if (mBookSizeSetting != null) {
            removeView(mBookSizeSetting);
            mBookSizeSetting = null;
        }
        setVisibility(GONE);
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    @Override
    public void initTextSize(BookReadSize bookReadSize) {
        mBookReadSize = bookReadSize;
    }

    static class PreferView {
        int imgLeftId;
        int imgRightId;
        int titleId;
        String prefKey;

        public PreferView(int imgLeftId, int imgRightId, int titleId, String key) {
            this.imgLeftId = imgLeftId;
            this.imgRightId = imgRightId;
            this.titleId = titleId;
            this.prefKey = key;
        }
    }

}
