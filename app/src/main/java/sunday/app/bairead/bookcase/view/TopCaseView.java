package sunday.app.bairead.bookcase.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sunday on 2017/3/21.
 */

public class TopCaseView extends TextView {
    public TopCaseView(Context context) {
        this(context,null);
    }

    public TopCaseView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TopCaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint paint;
    private void init(){
        paint = new Paint();
        paint.setARGB(155,48,63,159);
        paint.setStrokeWidth(25);
}


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        float[] l = {-20,getHeight(),getHeight(),-20};
        canvas.drawLines(l,paint);
        canvas.restore();
    }
}
