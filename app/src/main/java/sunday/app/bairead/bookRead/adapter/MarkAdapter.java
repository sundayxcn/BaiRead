package sunday.app.bairead.bookRead.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sunday.app.bairead.R;
import sunday.app.bairead.data.setting.BookMarkInfo;

/**
 * Created by zhongfei.sun on 2017/4/20.
 */

public class MarkAdapter extends ReadAdapter<BookMarkInfo> {

    public MarkAdapter(Context context, List<BookMarkInfo> list) {
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
        return mList.get(position).chapterIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mark_list_item, null, false);
            MarkViewHolder markViewHolder = new MarkViewHolder();
            markViewHolder.titleView = (TextView) convertView.findViewById(R.id.mark_list_item_title);
            markViewHolder.textView = (TextView) convertView.findViewById(R.id.mark_list_item_text);
            convertView.setTag(markViewHolder);
        }

        MarkViewHolder markViewHolder = (MarkViewHolder) convertView.getTag();
        markViewHolder.titleView.setText(mList.get(position).title);
        markViewHolder.textView.setText(mList.get(position).text);
        return convertView;
    }

    static class MarkViewHolder{
        TextView titleView;
        TextView textView;
    }
}
