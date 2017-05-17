package sunday.app.bairead.bookcase;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import sunday.app.bairead.R;
import sunday.app.bairead.bookcase.view.TopCaseView;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.download.BookCaseImageCache;
import sunday.app.bairead.download.ImageCache;
import sunday.app.bairead.utils.NewChapterShow;
import sunday.app.bairead.utils.TimeFormat;

/**
 * Created by Administrator on 2017/3/27.
 */

public class BookcaseListAdapter extends BaseAdapter {

    private Context context;

    private List<BookInfo> bookInfos;

    //存储操作过的checkbox
    private HashMap<Long, Boolean> checkMap = new HashMap<>();
    private ArrayList<ViewHolder> viewHolders = new ArrayList<>();
    //当前书架的状态
    private boolean isEdit;

    public BookcaseListAdapter(Context context) {
        this.context = context;
    }

    //返回选中的checkbox
    public List<BookInfo> getBookInfoList() {
        return bookInfos;
    }

    public void setBookInfoList(List<BookInfo> list) {
        bookInfos = list;
    }

    public boolean getEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void setCheck(long bookId, boolean isCheck) {
        checkMap.put(bookId, isCheck);
    }

    //返回选中的书籍ID
    public ArrayList<Long> getCheckList() {
        ArrayList<Long> list = new ArrayList<>();
        Set set = checkMap.keySet();
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            long key = (long) iter.next();
            boolean check = checkMap.get(key);
            if (check) {
                list.add(key);
            }
        }
        return list;
    }

    public boolean isItemCheck(long bookId) {
        Boolean check = checkMap.get(bookId);
        return check == null ? false : check;
    }

    public void clear() {
        checkMap.clear();
        for (ViewHolder viewHolder : viewHolders) {
            viewHolder.checkBox.setChecked(false);
        }
        viewHolders.clear();
    }

    @Override
    public int getCount() {
        return bookInfos.size();
    }

    @Override
    public BookInfo getItem(int position) {
        return bookInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.xlist_item, null);
            ViewHolder viewHolder = new ViewHolder(this,new BookCaseImageCache(context),convertView);
            viewHolders.add(viewHolder);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        BookInfo bookInfo = bookInfos.get(position);
        boolean isEdit = getEdit();
        boolean isCheck = isItemCheck(bookInfo.bookDetail.getId());
        try {
            viewHolder.setValue(bookInfo, isEdit, isCheck);
        }catch (NullPointerException e){
            Log.e("sunday","bookInfo"+bookInfo);
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.xlist_item_check_box)
        CheckBox checkBox;
        @BindView(R.id.xlist_item_cover_image)
        ImageView coverImageView;
        @BindView(R.id.xlist_item_top_case)
        TopCaseView topCaseView;
        @BindView(R.id.xlist_item_name)
        TextView nameTView;
        @BindView(R.id.xlist_item_chapter_latest)
        TextView chapterLatestTView;
        @BindView(R.id.xlist_item_update_time)
        TextView updateTimeTView;
        @BindView(R.id.xlist_item_chapter_index)
        TextView chapterIndexTView;
        @BindView(R.id.xlist_item_chapter_update)
        TextView chapterUpdate;

        private long bookId;

        private BookcaseListAdapter bookcaseListAdapter;
        private ImageCache mImageCache;

        ViewHolder(BookcaseListAdapter bookcaseListAdapter,
                   ImageCache imageCache,View view) {
            this.bookcaseListAdapter = bookcaseListAdapter;
            mImageCache = imageCache;
            ButterKnife.bind(this, view);
        }

        public void changeCheckBox() {
            boolean isCheck = checkBox.isChecked();
            checkBox.setChecked(!isCheck);
        }

        @OnCheckedChanged(R.id.xlist_item_check_box)
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bookcaseListAdapter.setCheck(bookId, isChecked);
        }

        public void setValue(BookInfo bookInfo, boolean isEdit, boolean isCheck) {
            String name = bookInfo.bookDetail.getName();
            String chapterLatest = bookInfo.bookDetail.getChapterLatest();
            int chapterIndex = bookInfo.bookChapter.getChapterIndex() + 1;
            int chapterCount = bookInfo.bookChapter.getChapterCount();
            String chapterText = String.valueOf(chapterIndex) + "/" + String.valueOf(chapterCount);
            nameTView.setText(name);
            chapterLatestTView.setText(chapterLatest);
            chapterIndexTView.setText(chapterText);


            mImageCache.loadImage(bookInfo.bookDetail.getCoverImageLink(),coverImageView);

            String timeString = TimeFormat.getTimeString(bookInfo.bookDetail.getUpdateTime());
            updateTimeTView.setText(timeString);
            boolean newChapter = NewChapterShow.getInstance().isHaveNewChapter(bookInfo.bookDetail.getId());
            chapterUpdate.setVisibility(newChapter ? View.VISIBLE : View.INVISIBLE);
            bookId = bookInfo.bookDetail.getId();

            if (isEdit) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(isCheck);
            } else {
                checkBox.setVisibility(View.GONE);
            }
            topCaseView.setVisibility(bookInfo.bookDetail.isTopCase() ? View.VISIBLE : View.GONE);
        }

        public long getBookId() {
            return bookId;
        }

    }
}