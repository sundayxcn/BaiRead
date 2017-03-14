package sunday.app.bairead.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import sunday.app.bairead.database.BookChapter;

/**
 * Created by sunday on 2016/12/16.
 */

public class BookTextView extends TextView {


    private IChapterChangeListener chapterChangeListener;
    private TextPaint textPaint;
    private int pageIndex;
    private int initPage;
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
        pageIndex = text == null ? initPage : 0;
        text = chapterText;
        if (mHeight != 0) {
            createPageTextList();
        }
        postInvalidate();

    }

    public void initPage(int page){
        initPage = page;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (pageTextList.size() > 0) {
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
            Log.e("sunday","createPageTextList-text == null");
            return;
        }
        pageTextList.clear();
        String[] textArray = text.trim().split("\n\n");
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

        if(chapterChangeListener != null) {
            int pageCount = pageTextList.size();
            if(pageIndex > pageCount - 1 ){
                pageIndex = pageCount - 1 ;
            }
            chapterChangeListener.onPageChange(pageIndex, pageCount);
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

        int pageCount = pageTextList.size();

        if (pageIndex > (pageCount - 1)) {
            pageIndex = pageCount - 1;
            if (chapterChangeListener != null) {
                chapterChangeListener.onChapterNext();
            }
        } else if (pageIndex < 0) {
            pageIndex = 0;
            if (chapterChangeListener != null) {
                chapterChangeListener.onChapterPrev();
            }
        } else {
            if(chapterChangeListener != null){
                chapterChangeListener.onPageChange(pageIndex,pageCount);
            }
            postInvalidate();
        }

    }

    public void setReadSize(ReadSize readSize) {
        this.readSize = readSize;
        createPageTextList();
        postInvalidate();
    }

    public interface IChapterChangeListener {
        void onChapterNext();

        void onChapterPrev();

        void onPageChange(int page,int pageCount);
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
