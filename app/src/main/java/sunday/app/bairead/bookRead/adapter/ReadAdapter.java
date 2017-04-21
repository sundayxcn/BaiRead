package sunday.app.bairead.bookRead.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

import butterknife.OnClick;

/**
 * Created by zhongfei.sun on 2017/4/20.
 */

public abstract class ReadAdapter<T> extends BaseAdapter{
    private Context mContext;
    protected List<T> mList;

    public ReadAdapter(Context context,List<T> list){
        mContext = context;
        mList = list;
    }

    public Context getContext(){
        return mContext;
    }

    public List getList(){
        return mList;
    }

    public void removeItem(T t){
        mList.remove(t);
        notifyDataSetChanged();
    }

    public void clear(){
        mList.clear();
        notifyDataSetChanged();
    }

}
