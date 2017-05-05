package sunday.app.bairead.bookDetail.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import sunday.app.bairead.data.setting.BookDetail;
import sunday.app.bairead.view.MyTextView;

/**
 * Created by sunday on 2017/3/17.
 */

public class BookTypeView extends MyTextView {

    private int type;

    public BookTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    @Override
    protected void setBackground() {
        //int strokeWidth = 5; // 边框宽度
        int roundRadius = 8; //  圆角半径
        //int strokeColor = Color.parseColor(TYPE_COLORS[type]);//边框颜色
        int fillColor = Color.parseColor(TYPE_COLORS[type]);//内部填充颜色

        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        //gd.setStroke(strokeWidth, strokeColor);

        setBackground(gd);

        setText(BookDetail.typeArray[type]);
    }

    public static final String[] TYPE_COLORS ={
            "#DFDFE0",
            "#FF8040",
            "#804040",
            "#0080C0",
            "#FF0990",
            "#FF8040",
            "#804040",
            "#0080C0",
            "#800080",
            "#8008A9",
            "#DFDFE0",
            "#FF8040",
    };

    public void setType(int type){
        this.type = type;
        setBackground();
    }
}
