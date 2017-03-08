package sunday.app.bairead.tool;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by sunday on 2016/12/23.
 */

public class DialogManager {
    public static final int TYPE_DOWNLOAD = 5;

    private Context context;
    private static AlertDialog alertDialogDownLoad;


    public DialogManager(Context c ){
        context = c;
    }

//    public DialogManager getInstance(){
//        if(DialogManager == null){
//
//        }
//        return DialogManager;
//    }

    public void show(int type){
        if(alertDialogDownLoad == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("下载中，请稍后");
            builder.create();
        }
        alertDialogDownLoad.show();
    }

    public void hide(){
        alertDialogDownLoad.hide();
    }

}
