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

import sunday.app.bairead.Tool.PreferenceSetting;
import sunday.app.bairead.UI.BookReadActivity;

/**
 * Created by sunday on 2016/12/16.
 */

public class BookTextView extends TextView {

    private String text;

    public BookTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private  TextPaint textPaint;
    private int pageIndex = -1;
    private int mHeight;
    private int mWidth;
    private int paddingLeft;
    private int marginSize;
    //分段、分行、分页
    private ArrayList<PageText> pageTextList = new ArrayList<>();

    public void setChapterText(String chapterText) {
        text = chapterText;
        pageIndex = 0;
        if(mHeight != 0){
            createPageTextList();
        }
        postInvalidate();

        //setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if(pageIndex != -1) {
            pageTextList.get(pageIndex).onDraw(canvas);
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mHeight != MeasureSpec.getSize(heightMeasureSpec)){
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            if(text != null) {
                createPageTextList();
            }
        }

    }

    class PageText{
        public int mHeight = 80;
        public ArrayList<String> lineTextList = new ArrayList<>();

        public PageText(int height){
            mHeight = height;
        }

        public int getHeight(){
            return mHeight;
        }

        public void onDraw(Canvas canvas){
            canvas.save();
            int count = lineTextList.size();
            int top = getTop();
            for(int i = 0;i< count;i++) {
                String s = lineTextList.get(i);
                int height = top +  i * mHeight;
                canvas.drawText(s, paddingLeft + marginSize, height, textPaint);
            }
            canvas.restore();
        }
    }

    /**
     * 将整个字符串先按段分成组，由于段落太长，一行放不下，所以需要将段处理成行，
     * 然后将行封装进每一页的结构中，在刷新过程中按页刷新
     * **/
    private void createPageTextList(){
        if(text == null){
            return;
        }
        pageTextList.clear();
        String[] textArray = text.trim().split("\n\n");
        pageIndex = 0;
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        paddingLeft = 0;
        ArrayList<String> lineTextList = new ArrayList<>();
        for(String str : textArray) {
            str = "    "+str;//增加行首空格
            //测量一行能放几个字符，然后进行裁剪
            int count = textPaint.breakText(str, true, mWidth - paddingLeft * 2 - marginSize * 2 , null);
            while(str.length() > count){
                String line = str.substring(0,count);
                lineTextList.add(line);
                str = str.substring(count, str.length());
                count = textPaint.breakText(str, true, mWidth - paddingLeft * 2 - marginSize * 2, null);
            }
            lineTextList.add(str);
        }

        //根据高度计算一页能放多少行
        PageText pageText = new PageText(textSize+lineSize);
        int lineCount = (int) Math.ceil(mHeight / pageText.getHeight());

        pageTextList.add(pageText);

        for(int i = 0 ,j = 0; i < lineTextList.size();i++,j++){
            if(j == lineCount){
                j = 0;
                pageText = new PageText(textSize+lineSize);
                pageTextList.add(pageText);
            }
            pageText.lineTextList.add(lineTextList.get(i));
        }

    }

    private BookReadActivity.ReadHandler handler ;

    public void setReadHandler(BookReadActivity.ReadHandler readHandler){
        handler = readHandler;
    }




    public void readNext(boolean next){
        if(next){
            pageIndex++;
        }else{
            pageIndex--;
        }


        if(pageIndex > (pageTextList.size() -1)){
            pageIndex = -1;
            handler.sendEmptyMessage(BookReadActivity.HANDLE_MESSAGE_CHAPTER_NEXT);
        }else if(pageIndex < 0){
            pageIndex =-1;
            handler.sendEmptyMessage(BookReadActivity.HANDLE_MESSAGE_CHAPTER_PREV);
        }else {
            postInvalidate();
        }

    }

    private int textSize;
    private int lineSize;


    public void setTextSize(int textSize){
        this.textSize = textSize;
        createPageTextList();
    }

    public void setLineSize(int lineSize){
        this.lineSize = lineSize;
        createPageTextList();
    }

    public void setMarginSize(int marginSize){
        this.marginSize = marginSize;
        createPageTextList();
    }

    public void setLast(boolean isLast){
        if(isLast){
            pageIndex = pageTextList.size() -1;
        }
    }

    public void setBegin(boolean isBegin){
        if(isBegin){
            pageIndex = 0;
        }
    }

}
