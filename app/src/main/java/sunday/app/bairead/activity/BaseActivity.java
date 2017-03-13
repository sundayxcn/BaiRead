package sunday.app.bairead.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import sunday.app.bairead.database.BaiReadApplication;
import sunday.app.bairead.tool.NetworkTool;
import sunday.app.bairead.tool.ThreadManager;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BaseActivity extends AppCompatActivity implements BaiReadApplication.INetworkListener {

    @Override
    public void networkChange(boolean connect, int type) {
//            if(type == ConnectivityManager.TYPE_MOBILE && connect || type == ConnectivityManager.TYPE_WIFI && connect){
//            }else{
//                //hideProgressDialog();
//                showToastNetworkUnconnect();
//            }
    }

    @Override
    public void unConnect() {
        //如果有正在下载的线程这弹出网络中断提示
//        showToastNetworkUnconnect();
    }

    private interface DialogListener{
        void onCancel();
        void onConfirm();
        void onConfirmAsync();
    }

    public static class DialogListenerIm implements DialogListener{

        @Override
        public void onCancel() {

        }

        @Override
        public void onConfirm() {

        }

        @Override
        public void onConfirmAsync() {

        }
    }

    protected Handler handler = new Handler();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaiReadApplication application  = (BaiReadApplication) getApplicationContext();
        application.addListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaiReadApplication application  = (BaiReadApplication) getApplicationContext();
        application.removeListener(this);
    }

    protected void showProgressDialog() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(BaseActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("数据加载中，请稍后");
                }
                progressDialog.show();
            }
        });

    }

    protected void showProgressDialog(final String progress) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(BaseActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("数据加载中，请稍后");
                }
                progressDialog.setMessage(progress);
                progressDialog.show();
            }
        });

    }

    protected void hideProgressDialog(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progressDialog != null) {
                    progressDialog.hide();
                    //progressDialog = null;
                }
            }
        });

    }


    public boolean isConnect(){
        return NetworkTool.isNetworkConnect(this);
    }

    public void showToastNetworkUnconnect(){
        showToast("网络连接不上，请检查网络");
    }

    public void showToast(final String text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(),text,Toast.LENGTH_SHORT).show();
            }
        });

    }

    AlertDialog confirmDialog;
    public void showConfirmDialog(String string,String confirmText,String cancelText,final DialogListenerIm dialogListenerIm) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(string)
                .setNegativeButton(confirmText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the position has problem
                        confirmDialog.dismiss();
                        dialogListenerIm.onConfirm();
                        new AsyncTask<Void,Void,Void>(){

                            @Override
                            protected Void doInBackground(Void... params) {
                                if(dialogListenerIm != null){
                                    dialogListenerIm.onConfirmAsync();
                                }
                                return null;
                            }
                        }.execute();
                    }
                })
                .setPositiveButton(cancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmDialog.dismiss();
                        dialogListenerIm.onCancel();
                    }
                });
        confirmDialog = builder.create();
        confirmDialog.show();
    }

    public void showConfirmDialog(String string,final DialogListenerIm dialogListenerIm) {
        showConfirmDialog(string,"确定","取消",dialogListenerIm);
    }

}
