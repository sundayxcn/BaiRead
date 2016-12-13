package sunday.app.bairead.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import sunday.app.bairead.DataBase.BookInfo;

/**
 * Created by sunday on 2016/12/9.
 */

public class BookcaseView extends RelativeLayout {
    public BookcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookcaseView(Context context) {
        super(context, null);
    }

    public void setData(BookInfo bookInfo){

    }

}
