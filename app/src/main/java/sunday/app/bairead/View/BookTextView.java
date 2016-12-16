package sunday.app.bairead.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sunday on 2016/12/16.
 */

public class BookTextView extends TextView {

    private String text;

    public BookTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setChapterText(String text) {
        this.text = text;
        setText(text);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if(text != null) {
//            canvas.save();
//            TextPaint tp = new TextPaint();
//            tp.setColor(Color.BLUE);
//            tp.setStyle(Paint.Style.FILL);
//            tp.setTextSize(50);
//            StaticLayout myStaticLayout = new StaticLayout(text, tp, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//            myStaticLayout.draw(canvas);
//            canvas.restore();
//        }
//    }
}
