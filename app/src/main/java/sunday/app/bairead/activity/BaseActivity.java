package sunday.app.bairead.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BaseActivity extends AppCompatActivity {


    public interface DialogListener{
        void onCancel();
        void onConfirmed();
    }

    protected Handler handler = new Handler();

    ProgressDialog progressDialog;
    protected void showProgressDialog() {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (progressDialog == null) {
//                    progressDialog = new ProgressDialog(BaseActivity.this);
//                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                    progressDialog.setMessage("数据加载中，请稍后");
//                }
//                progressDialog.show();
//            }
//        });

    }

    protected void hideProgressDialog(){
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if(progressDialog != null) {
//                    progressDialog.hide();
//                    progressDialog = null;
//                }
//            }
//        });

    }

    public void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    AlertDialog confirmDialog;
    public void showConfirmDialog(String string,String confirmText,String cancelText,final DialogListener dialogListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(string)
                .setNegativeButton(confirmText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the position has problem
                        confirmDialog.dismiss();
                        dialogListener.onConfirmed();
                    }
                })
                .setPositiveButton(cancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmDialog.dismiss();
                        dialogListener.onCancel();
                    }
                });
        confirmDialog = builder.create();
        confirmDialog.show();
    }

}
