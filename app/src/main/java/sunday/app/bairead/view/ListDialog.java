package sunday.app.bairead.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import sunday.app.bairead.R;

/**
 * Created by zhongfei.sun on 2017/3/25.
 */

public class ListDialog extends Dialog {
    private TextView titleView;
    private ListView listView;
    private String[] strings;

    public ListDialog(Context context) {
        super(context, R.style.ListDialog);
        View parent = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_layout,null,false);
        setContentView(parent);
        setupView(parent);

    }

    private void setupView(View parent){
        titleView = (TextView) parent.findViewById(R.id.dialog_select_layout_title);
        listView = (ListView) parent.findViewById(R.id.dialog_select_layout_list_view);
    }

    public void show(String title, String[] texts, AdapterView.OnItemClickListener onItemClickListener){
        titleView.setText(title);
        strings = texts;
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return strings.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    TextView button = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_list_item,null,false);
                    button.setHeight(120);
                    convertView = button;
                }

                ((TextView)convertView).setText(strings[position]);

                return convertView;
            }
        });
        listView.setOnItemClickListener(onItemClickListener);
        show();
    }
}
