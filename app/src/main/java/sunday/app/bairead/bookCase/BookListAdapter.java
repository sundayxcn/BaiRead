package sunday.app.bairead.bookCase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import sunday.app.bairead.R;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.utils.NewChapterShow;
import sunday.app.bairead.utils.TimeFormat;

/**
 * Created by Administrator on 2017/3/27.
 */

public class BookListAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<BookInfo> bookInfos;

    //存储操作过的checkbox
    private HashMap<Long, Boolean> checkMap = new HashMap<>();
    private ArrayList<ViewHolder> viewHolders = new ArrayList<>();
    //当前书架的状态
    private boolean isEdit;
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener();


    public BookListAdapter(Context context) {
        this.context = context;
    }

    //返回选中的checkbox
    public ArrayList<BookInfo> getBookInfoList() {
        return bookInfos;
    }

    public void setBookInfoList(ArrayList<BookInfo> list) {
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
    public Object getItem(int position) {
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
            ViewHolder viewHolder = new ViewHolder(convertView);
            onCheckedChangeListener.setBookListAdapter(this);
            viewHolders.add(viewHolder);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        BookInfo bookInfo = bookInfos.get(position);
        boolean isEdit = getEdit();
        boolean isCheck = isItemCheck(bookInfo.bookDetail.getId());
        viewHolder.setValue(bookInfo, isEdit, isCheck);
        return convertView;
    }

    private class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        private BookListAdapter booklistAdapter;

        public void setBookListAdapter(BookListAdapter booklistAdapter) {
            this.booklistAdapter = booklistAdapter;
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                RelativeLayout parent = (RelativeLayout) buttonView.getParent();
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                if (booklistAdapter != null) {
                    setCheck(viewHolder.getBookId(), isChecked);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ViewHolder {
        @Bind(R.id.xlist_item_check_box)
        CheckBox checkBox;
        @Bind(R.id.xlist_item_cover_image)
        ImageView coverImageView;
        @Bind(R.id.xlist_item_top_case)
        TopCaseView topCaseView;
        @Bind(R.id.xlist_item_name)
        TextView nameTView;
        @Bind(R.id.xlist_item_chapter_latest)
        TextView chapterLatestTView;
        @Bind(R.id.xlist_item_update_time)
        TextView updateTimeTView;
        @Bind(R.id.xlist_item_chapter_index)
        TextView chapterIndexTView;
        @Bind(R.id.xlist_item_chapter_update)
        TextView chapterUpdate;

        private long bookId;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void changeCheckBox() {
            boolean isCheck = checkBox.isChecked();
            checkBox.setChecked(!isCheck);
        }

        @OnCheckedChanged(R.id.xlist_item_check_box)
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                RelativeLayout parent = (RelativeLayout) buttonView.getParent();
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                setCheck(viewHolder.getBookId(), isChecked);
            } catch (Exception e) {
                e.printStackTrace();
            }
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