package sunday.app.bairead.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.Tool.PreferenceSetting;
import sunday.app.bairead.UI.BookReadActivity;
import sunday.app.bairead.presenter.BookReadPresenter;

/**
 * Created by sunday on 2016/12/16.
 */

public class BookTextView extends TextView {


    private IChapterChangeListener chapterChangeListener;
    private TextPaint textPaint;
    private int pageIndex = -1;
    private int mHeight;
    private int mWidth;
    private int paddingLeft;
    //private int marginSize;
    //分段、分行、分页
    private ArrayList<PageText> pageTextList = new ArrayList<>();
    private String text;
    private ReadSize readSize;

    public BookTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChapterText(String chapterText) {
        text = chapterText;
        pageIndex = 0;
        if (mHeight != 0) {
            createPageTextList();
        }
        postInvalidate();

        //setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (pageIndex != -1) {
            pageTextList.get(pageIndex).onDraw(canvas);
        }
    }

    public void setText(String text) {
        //this.text = text;
        setChapterText(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeight != MeasureSpec.getSize(heightMeasureSpec)) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            if (text != null) {
                createPageTextList();
            }
        }

    }

    /**
     * 将整个字符串先按段分成组，由于段落太长，一行放不下，所以需要将段处理成行，
     * 然后将行封装进每一页的结构中，在刷新过程中按页刷新
     **/
    private void createPageTextList() {
        if (text == null) {
            return;
        }
        pageTextList.clear();
        String[] textArray = text.trim().split("\n\n");
        pageIndex = 0;
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(readSize.textSize);
        paddingLeft = 0;
        ArrayList<String> lineTextList = new ArrayList<>();
        for (String str : textArray) {
            str = "    " + str;//增加行首空格
            //测量一行能放几个字符，然后进行裁剪
            int count = textPaint.breakText(str, true, mWidth - paddingLeft * 2 - readSize.marginSize * 2, null);
            while (str.length() > count) {
                String line = str.substring(0, count);
                lineTextList.add(line);
                str = str.substring(count, str.length());
                count = textPaint.breakText(str, true, mWidth - paddingLeft * 2 - readSize.marginSize * 2, null);
            }
            lineTextList.add(str);
        }

        //根据高度计算一页能放多少行
        PageText pageText = new PageText(readSize.textSize + readSize.lineSize);
        int lineCount = (int) Math.ceil(mHeight / pageText.getHeight());

        pageTextList.add(pageText);

        for (int i = 0, j = 0; i < lineTextList.size(); i++, j++) {
            if (j == lineCount) {
                j = 0;
                pageText = new PageText(readSize.textSize + readSize.lineSize);
                pageTextList.add(pageText);
            }
            pageText.lineTextList.add(lineTextList.get(i));
        }

    }

    public void setOnChangeListener(IChapterChangeListener listener) {
        chapterChangeListener = listener;
    }

    public void readNext(boolean next) {
        if (next) {
            pageIndex++;
        } else {
            pageIndex--;
        }


        if (pageIndex > (pageTextList.size() - 1)) {
            pageIndex = -1;
            if (chapterChangeListener != null) {
                chapterChangeListener.onChapterNext();
            }
        } else if (pageIndex < 0) {
            pageIndex = -1;
            if (chapterChangeListener != null) {
                chapterChangeListener.onChapterNext();
            }
        } else {
            postInvalidate();
        }

    }

    public void setReadSize(ReadSize readSize) {
        this.readSize = readSize;
        createPageTextList();
        postInvalidate();
    }

    public void setLast(boolean isLast) {
        if (isLast) {
            pageIndex = pageTextList.size() - 1;
        }
    }

    public void setBegin(boolean isBegin) {
        if (isBegin) {
            pageIndex = 0;
        }
    }

    public interface IChapterChangeListener {
        void onChapterNext();

        void onChapterPrev();
    }

    public static class ReadSize {
        public static final int TYPE_TEXT = 0;
        public static final int TYPE_LINE = TYPE_TEXT + 1;
        public static final int TYPE_MAEGIN = TYPE_LINE + 1;

        public int textSize;
        public int lineSize;
        public int marginSize;

        public ReadSize(int textSize, int lineSize, int marginSize) {
            this.textSize = textSize;
            this.lineSize = lineSize;
            this.marginSize = marginSize;
        }
    }

    public static class ReadText {
        public String text;
        public String title;

        public ReadText(BookChapter.Chapter chapter) {
            text = chapter.getText();
            title = chapter.getTitle();
        }
    }

    class PageText {
        public int mHeight = 80;
        public ArrayList<String> lineTextList = new ArrayList<>();

        public PageText(int height) {
            mHeight = height;
        }

        public int getHeight() {
            return mHeight;
        }

        public void onDraw(Canvas canvas) {
            canvas.save();
            int count = lineTextList.size();
            int top = getTop();
            for (int i = 0; i < count; i++) {
                String s = lineTextList.get(i);
                int height = top + i * mHeight;
                canvas.drawText(s, paddingLeft + readSize.marginSize, height, textPaint);
            }
            canvas.restore();
        }
    }

}
