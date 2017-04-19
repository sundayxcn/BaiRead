package sunday.app.bairead.base;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.widget.Toast;

import sunday.app.bairead.R;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class BaseFragment extends Fragment {


    private AlertDialog mConfirmDialog;
    private ProgressDialog mProgressDialog;
    private void showConfirmDialog(String title,
                                   String confirmText,
                                   String cancelText,
                                   DialogInterface.OnClickListener onConfirmListener,
                                   DialogInterface.OnClickListener onCancelListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(title).
                setNegativeButton(confirmText, onConfirmListener).
                setPositiveButton(cancelText, onCancelListener);
        mConfirmDialog = builder.create();
        mConfirmDialog.show();
    }


//

    public void showConfirmDialog(int resId,DialogInterface.OnClickListener onConfirmListener) {
        showConfirmDialog(
                resId,
                R.string.confirm,
                R.string.cancel,
                onConfirmListener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mConfirmDialog.dismiss();
                    }
                });
    }

    public void showConfirmDialog(int resId,
                                  DialogInterface.OnClickListener onConfirmListener,
                                  DialogInterface.OnClickListener onCancelListener) {
        showConfirmDialog(
                resId,
                R.string.confirm,
                R.string.cancel,
                onConfirmListener,
                onCancelListener);
    }

    public void showConfirmDialog(@StringRes int resId,
                                  @StringRes int confirmResId,
                                  @StringRes int cancelResId,
                                  DialogInterface.OnClickListener onConfirmListener,
                                  DialogInterface.OnClickListener onCancelListener){
        String title = getResources().getString(resId);
        String confirm = getResources().getString(confirmResId);
        String cancel = getResources().getString(cancelResId);
        showConfirmDialog(
                title,
                confirm,
                cancel,
                onConfirmListener,
                onCancelListener);
    }

    public void showConfirmDialog(@StringRes int resId,
                                  @StringRes int confirmResId,
                                  @StringRes int cancelResId,
                                  DialogInterface.OnClickListener onConfirmListener){
        String title = getResources().getString(resId);
        String confirm = getResources().getString(confirmResId);
        String cancel = getResources().getString(cancelResId);
        showConfirmDialog(
                title,
                confirm,
                cancel,
                onConfirmListener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mConfirmDialog.dismiss();
                    }
                });
    }

    public void showToast(@StringRes int resId){
        Toast.makeText(getActivity(),resId,Toast.LENGTH_SHORT).show();
    }


    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("数据加载中，请稍后");
        }
        mProgressDialog.show();
    }

    protected void showProgressDialog(String progress) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.setMessage(progress);
        mProgressDialog.show();

    }

    protected void hideProgressDialog(){
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
            //progressDialog = null;
        }
    }

    protected boolean onBackPressed(){
        return false;
    }
}
