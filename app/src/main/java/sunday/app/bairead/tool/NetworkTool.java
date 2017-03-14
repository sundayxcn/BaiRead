package sunday.app.bairead.tool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sunday on 2016/12/7.
 */

public class NetworkTool {

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

            Network[] networks = mConnectivityManager.getAllNetworks();
            for(Network network : networks){
                NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
                if(networkInfo == null){
                    return false;
                }else if(networkInfo.getType() == type){
                    return networkInfo.isConnected();
                }
            }

        }
        return false;
    }

}