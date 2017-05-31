package sunday.app.bairead.bookRead.view;

import android.content.Context;
import android.content.Intent;
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
    private BookReadContract.SettingPresenter mPresenter;
    private OnClickListener sizeOnReduceClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int type = (int) v.getTag();
            if (type == 0) {
                if (mBookReadSize.textSize > 30) {
                    mBookReadSize.textSize -= 4;
                }
            } else if (type == 1) {
                if (mBookReadSize.lineSize > 6) {
                    mBookReadSize.lineSize -= 6;
                }
            } else if (type == 2) {
                if (mBookReadSize.marginSize > 6) {
                    mBookReadSize.marginSize -= 6;
                }
            }
            mPresenter.updateReadSize(mBookReadSize);

        }
    };
    private OnClickListener sizeOnAddClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int type = (int) v.getTag();
            if (type == 0) {
                mBookReadSize.textSize += 4;
            } else if (type == 1) {
                mBookReadSize.lineSize += 6;
            } else if (type == 2) {
                mBookReadSize.marginSize += 6;
            }
            mPresenter.updateReadSize(mBookReadSize);
        }
    };
    private OnClickListener buttonOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.book_read_setting_panel_chapter_menu:
                    getContext().sendBroadcast(new Intent(BookReadActivity.ACTION_CHAPTER));
                    break;
                case R.id.book_read_setting_panel_book_mark:
                    getContext().sendBroadcast(new Intent(BookReadActivity.ACTION_MARK));
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


    @Override
    public void setPresenter(BookReadContract.SettingPresenter presenter) {
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

    private void setupTypeView(View parent, int imgLeftId, int imgRightId, int titleId, int sizeType) {
        TextView titleView = (TextView) parent.findViewById(R.id.book_read_setting_size_line_title);
        ImageView reduceButton = (ImageView) parent.findViewById(R.id.book_read_setting_size_line_button_reduce);
        reduceButton.setImageResource(imgLeftId);
        ImageView addButton = (ImageView) parent.findViewById(R.id.book_read_setting_size_line_button_add);
        addButton.setImageResource(imgRightId);
        titleView.setText(titleId);
        reduceButton.setTag(sizeType);
        addButton.setTag(sizeType);
        reduceButton.setOnClickListener(sizeOnReduceClickListener);
        addButton.setOnClickListener(sizeOnAddClickListener);
    }

    private void showBookTextSizePanel() {
        mBookSizeSetting = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.book_read_setting_size_panel, null, false);
        PreferView[] preferViews = new PreferView[]{
                new PreferView(R.drawable.ic_font_reduce, R.drawable.ic_font_add, R.string.book_read_preference_title_font, 0),
                new PreferView(R.drawable.ic_line_reduce, R.drawable.ic_line_add, R.string.book_read_preference_title_line, 1),
                new PreferView(R.drawable.ic_margin_reduce, R.drawable.ic_margin_add, R.string.book_read_preference_title_margin, 2),
        };


        int childCount = mBookSizeSetting.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mBookSizeSetting.getChildAt(i);
            setupTypeView(view, preferViews[i].imgLeftId, preferViews[i].imgRightId, preferViews[i].titleId, preferViews[i].sizeType);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mBookSizeSetting, layoutParams);

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBookSizeSetting != null) {
            removeView(mBookSizeSetting);
            mBookSizeSetting = null;
        }
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    @Override
    public void setReadSize(BookReadSize bookReadSize) {
        mBookReadSize = bookReadSize;
    }

    static class PreferView {
        int imgLeftId;
        int imgRightId;
        int titleId;
        int sizeType;

        public PreferView(int imgLeftId, int imgRightId, int titleId, int sizeType) {
            this.imgLeftId = imgLeftId;
            this.imgRightId = imgRightId;
            this.titleId = titleId;
            this.sizeType = sizeType;
        }
    }

}
