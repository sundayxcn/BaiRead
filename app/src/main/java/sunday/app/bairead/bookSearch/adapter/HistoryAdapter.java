package sunday.app.bairead.bookSearch.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sunday.app.bairead.R;

/**
 * Created by Administrator on 2017/5/6.
 */

public class HistoryAdapter extends BaseAdapter {

    private List<String> list;
    private LayoutInflater mLayoutInflater;

    public HistoryAdapter(@NonNull LayoutInflater layoutInflater,
                          @NonNull List<String> list) {
        mLayoutInflater = layoutInflater;
        this.list = list;
    }

    public void addItem(String name) {
        list.add(0, name);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.history_list_item, null, false);
            convertView.setMinimumHeight(200);
        }

        ((TextView) convertView).setText(list.get(position));
        return convertView;
    }
}
