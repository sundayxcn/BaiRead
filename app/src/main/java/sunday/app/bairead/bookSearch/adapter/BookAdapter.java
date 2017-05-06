package sunday.app.bairead.bookSearch.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sunday.app.bairead.R;
import sunday.app.bairead.data.setting.BookInfo;

/**
 * Created by Administrator on 2017/5/6.
 */

public class BookAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;

    private List<BookInfo> list;

    public BookAdapter(@NonNull LayoutInflater layoutInflater,
                       @NonNull List<BookInfo> list) {
        mLayoutInflater = layoutInflater;
        this.list = list;
    }

    public void addItem(BookInfo bookInfo) {
        list.add(bookInfo);
        notifyDataSetChanged();
    }

    public void clearData(){
        list.clear();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public BookInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.search_list_item, null, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.setValue(list.get(position));
        return convertView;
    }


    static class ViewHolder {
        private TextView nameTView;
        private TextView authorTView;
        private TextView sourceTView;
        private TextView chapterLatestTView;
        private TextView chapterTimeTView;

        ViewHolder(View parent) {
            nameTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_name);
            authorTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_author);
            sourceTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_source);
            chapterLatestTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_chapter_latest);
            chapterTimeTView = (TextView) parent.findViewById(R.id.search_fragment_list_item_chapter_time);
        }

        void setValue(BookInfo searchResult) {
            nameTView.setText(searchResult.bookDetail.getName());
            authorTView.setText(searchResult.bookDetail.getAuthor());
            //sourceTView.setText(sourceTView.getText()+info.get);
            chapterLatestTView.setText(searchResult.bookDetail.getChapterLatest());
            chapterTimeTView.setText(searchResult.bookDetail.getUpdateTime());
        }

    }
}
