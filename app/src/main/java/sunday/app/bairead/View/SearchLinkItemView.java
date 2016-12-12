package sunday.app.bairead.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.R;

/**
 * Created by sunday on 2016/12/7.
 */

public class SearchLinkItemView extends LinearLayout{

    private TextView nameTView;
    private TextView authorTView;
    private TextView sourceTView;
    private TextView chapterLatestTView;
    private TextView chapterTimeTView;

    public SearchLinkItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupView();
    }

    public void setupView(){
        nameTView = (TextView) findViewById(R.id.search_fragment_list_item_name);
        authorTView = (TextView) findViewById(R.id.search_fragment_list_item_author);
        sourceTView = (TextView) findViewById(R.id.search_fragment_list_item_source);
        chapterLatestTView = (TextView) findViewById(R.id.search_fragment_list_item_chapter_latest);
        chapterTimeTView = (TextView) findViewById(R.id.search_fragment_list_item_chapter_time);

    }

    public void setInfo(BookDetail info){
        nameTView.setText(nameTView.getText()+info.name+" | "+authorTView.getText()+info.author);
        //authorTView.setText(authorTView.getText()+info.author);
//        sourceTView.setText(sourceTView.getText()+info.sourceName);
//        chapterLatestTView.setText(chapterLatestTView.getText()+info.chapterLatest);
//        chapterTimeTView.setText(chapterTimeTView.getText()+info.chapterTime);

        setTag(info);
    }

}
