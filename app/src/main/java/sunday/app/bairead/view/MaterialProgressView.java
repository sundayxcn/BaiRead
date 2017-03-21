package sunday.app.bairead.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by sunday on 2017/3/21.
 */

public class MaterialProgressView extends ImageView {

    private final int color = 0xFFFF0000;
    private final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private MaterialProgressDrawable materialProgressDrawable;

    public MaterialProgressView(Context context) {
        this(context, null);
    }


    public MaterialProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //materialProgressView.setMaterialProgressDrawable();
        materialProgressDrawable = new MaterialProgressDrawable(getContext(), this);
        materialProgressDrawable.setBackgroundColor(CIRCLE_BG_LIGHT);
        //圈圈颜色,可以是多种颜色
        materialProgressDrawable.setColorSchemeColors(color);
        //设置圈圈的各种大小
        materialProgressDrawable.updateSizes(MaterialProgressDrawable.LARGE);
        materialProgressDrawable.setAlpha(255);
        materialProgressDrawable.showArrow(true);
        materialProgressDrawable.setStartEndTrim(0, 1f);
        setImageDrawable(materialProgressDrawable);
    }


    public void start() {
        materialProgressDrawable.start();
    }

    public void stop() {
        materialProgressDrawable.stop();
    }


}
