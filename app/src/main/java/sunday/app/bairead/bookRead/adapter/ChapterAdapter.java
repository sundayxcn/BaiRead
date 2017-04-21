package sunday.app.bairead.bookRead.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sunday.app.bairead.R;
import sunday.app.bairead.bookRead.cache.BookChapterCacheNew;
import sunday.app.bairead.data.setting.BookChapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by zhongfei.sun on 2017/4/20.
 */

public class ChapterAdapter extends ReadAdapter<BookChapter.Chapter> {

    public ChapterAdapter(Context context, List<BookChapter.Chapter> list) {
        super(context, list);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chapter_list_item, null, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.chapter_list_item_text);
            viewHolder.cacheShowView = (TextView) convertView.findViewById(R.id.chapter_list_item_cache_text);
            convertView.setTag(viewHolder);
        }

        BookChapter.Chapter chapter = mList.get(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.textView.setText(chapter.getTitle());

        boolean isCache = BookChapterCacheNew.getInstance().isChapterExists(chapter);
        viewHolder.cacheShowView.setVisibility(isCache ? VISIBLE : INVISIBLE);

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
        TextView cacheShowView;
    }
}
