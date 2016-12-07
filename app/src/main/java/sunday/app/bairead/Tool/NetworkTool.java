package sunday.app.bairead.Tool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sunday on 2016/12/7.
 */

public class NetworkTool {

    interface NetworkListener{
        void networkChange(boolean connect,int type);
    }

    private Context context;
    private ArrayList<NetworkListener> mListenerList = new ArrayList<>();

    private void addListener(NetworkListener listener){
        mListenerList.add(listener);
    }

    private void removeListener(NetworkListener listener){
        mListenerList.remove(listener);
    }

    private void clearListener(){
        mListenerList.clear();
    }

    public NetworkTool(Context c ){
        context = c;
    }


    public static boolean isNetworkConnect(Context context){
        return isMobileConnected(context) || isWifiConnected(context);
    }

    public static boolean isMobileConnected(Context context) {
        return isConnected(context,ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isWifiConnected(Context context) {
        return isConnected(context,ConnectivityManager.TYPE_WIFI);
    }

    private static boolean isConnected(Context context,int type){
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Network network = mConnectivityManager.getActiveNetwork();
            NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
            if(networkInfo == null){
                return false;
            }else {
                return networkInfo.isConnected() && networkInfo.getType() == type;
            }
        }
        return false;
    }

    public void addReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(connectionReceiver, intentFilter);
    }

    public void removeReceiver(){
        context.unregisterReceiver(connectionReceiver);
    }

    BroadcastReceiver connectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
            Network network = connectMgr.getActiveNetwork();
            NetworkInfo networkInfo = connectMgr.getNetworkInfo(network);
            boolean isConnected = false;
            int type = -1;//ConnectivityManager.TYPE_NONE;
            if(networkInfo == null){
                isConnected =false;
                Toast.makeText(context,"network is unConnect",Toast.LENGTH_SHORT).show();
            }else if(networkInfo.isConnected()){
                type = networkInfo.getType();
                if(type == ConnectivityManager.TYPE_MOBILE){
                    Toast.makeText(context,"Mobile Network is Connect",Toast.LENGTH_SHORT).show();
                }else if(type == ConnectivityManager.TYPE_WIFI){
                    Toast.makeText(context,"WIFI is Connect",Toast.LENGTH_SHORT).show();
                }
            }

            doListener(isConnected,type);

        }
    };


    private void doListener(boolean connect,int type){
        for(NetworkListener listener:mListenerList){
            listener.networkChange(connect,type);
        }
    }

}