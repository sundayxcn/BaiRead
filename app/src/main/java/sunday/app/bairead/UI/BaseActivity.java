package sunday.app.bairead.UI;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BaseActivity extends AppCompatActivity {


    protected interface DialogListener{
        void confirmed();
    }


    protected void showProgressDialog(String text){

    }

    protected void hideProgressDialog(){

    }

    protected void showConfirmDialog(DialogListener dialogListener){

    }

}
