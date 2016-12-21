package sunday.app.bairead.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import sunday.app.bairead.UI.BookReadActivity;

/**
 * Created by sunday on 2016/12/16.
 */

public class BookTextView extends TextView implements View.OnClickListener{

    private Spanned text;

    public BookTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private  TextPaint textPaint;
    private int pageIndex = 0;
    private int mHeight;
    private int mWidth;
    private int mTop;
    private int paddingLeft;

    //分段、分行、分页
    private ArrayList<PageText> pageTextList = new ArrayList<>();

    public void setChapterText(Spanned spanned) {
        text = spanned;
        postInvalidate();
        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if(pageIndex > (pageTextList.size() -1)){
            pageIndex = pageTextList.size() -1;
        }else if(pageIndex < 0){
            pageIndex = 0;
        }
        pageTextList.get(pageIndex).onDraw(canvas);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mHeight != MeasureSpec.getSize(heightMeasureSpec)){
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            Point point = new Point();
            getDisplay().getSize(point);
            mTop = point.y - mHeight;
            createPageTextList();
        }

    }


    class PageText{
        public static final int LINE_HEIGHT = 80;
        public ArrayList<String> lineTextList = new ArrayList<>();
        public void onDraw(Canvas canvas){
            canvas.save();
            int count = lineTextList.size();
            for(int i = 0;i< count;i++) {
                String s = lineTextList.get(i);
                int height = mTop+  i * LINE_HEIGHT;
                canvas.drawText(s, paddingLeft, height, textPaint);
            }
            canvas.restore();
        }
    }

    /**
     * 将整个字符串先按段分成组，由于段落太长，一行放不下，所以需要将段处理成行，
     * 然后将行封装进每一页的结构中，在刷新过程中按页刷新
     * **/
    private void createPageTextList(){
        pageTextList.clear();
        String[] textArray = String.valueOf(text).trim().split("\n\n");

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(60);
        paddingLeft = 0;
        ArrayList<String> lineTextList = new ArrayList<>();
        for(String str : textArray) {
            str = "    "+str;//增加行首空格
            //测量一行能放几个字符，然后进行裁剪
            int count = textPaint.breakText(str, true, mWidth - paddingLeft * 2 , null);
            while(str.length() > count){
                String line = str.substring(0,count);
                lineTextList.add(line);
                str = str.substring(count, str.length());
                count = textPaint.breakText(str, true, mWidth - paddingLeft * 2, null);
            }
            lineTextList.add(str);
        }

        //根据高度计算一页能放多少行
        int lineCount = (int) Math.ceil(mHeight / PageText.LINE_HEIGHT);
        PageText pageText = new PageText();
        pageTextList.add(pageText);

        for(int i = 0 ,j = 0; i < lineTextList.size();i++,j++){
            if(j == lineCount){
                j = 0;
                pageText = new PageText();
                pageTextList.add(pageText);
            }
            pageText.lineTextList.add(lineTextList.get(i));
        }

    }

    @Override
    public void onClick(View v) {
        pageIndex++;
        postInvalidate();
    }
}
