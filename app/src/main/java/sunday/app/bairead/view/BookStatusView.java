package sunday.app.bairead.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

/**
 * Created by sunday on 2017/3/17.
 */

public class BookStatusView extends MyTextView {

    private boolean status;

    public BookStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean isSeli) {
        status = isSeli;
        setBackground();
    }

    @Override
    protected void setBackground() {
        //int strokeWidth = 1; // 边框宽度
        int roundRadius = 8; //  圆角半径
        //int strokeColor = Color.parseColor("#00FFFF");//边框颜色
        int fillColor = Color.parseColor("#808080");//内部填充颜色

        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        //gd.setStroke(strokeWidth, strokeColor);

        setBackground(gd);

        String text = status ? "连" : "完";
        setText(text);
    }

}
