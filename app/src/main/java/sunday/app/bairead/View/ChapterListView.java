package sunday.app.bairead.View;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.R;

/**
 * Created by sunday on 2017/1/10.
 */

public class ChapterListView extends ListView {

    private BookInfo bookInfo;
    private Context mContext;


    public ChapterListView(Context context) {
        super(context);
        mContext = context;
        setBackgroundResource(R.color.colorChapterListBg);
    }


    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
        setAdapter(new ChapterAdapter());
    }

    private class ChapterAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bookInfo.bookChapter.getChapterCount();
        }

        @Override
        public Object getItem(int position) {
            return bookInfo.bookChapter.getChapter(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textView = new TextView(mContext);
                textView.setHeight(100);
                convertView = textView;
            }
            TextView view = (TextView) convertView;
            String text = bookInfo.bookChapter.getChapter(position).getTitle();
            view.setText(text);

            return view;
        }
    }

}
