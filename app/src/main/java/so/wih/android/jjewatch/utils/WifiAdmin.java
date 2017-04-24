package so.wih.android.jjewatch.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ==============================================
 * Created by HuWei on 2017/2/8.
 * WifiAdmin类用于连接wifi
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class WifiAdmin {

    public WifiManager mWifiManager;

    /**
     *  获取系统Wifi服务   WIFI_SERVICE
     */

    public WifiAdmin(Context context) {
        if(mWifiManager == null){
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
    }

    /**打开Wifi**/
    public void openWiFi() {
        if(!mWifiManager.isWifiEnabled()){ //当前wifi不可用
            mWifiManager.setWifiEnabled(true);
        }
    }
    public int getWifiState(){
        return mWifiManager.getWifiState();
    }

    /**
     *  得到Scan结果
     */
    public List<ScanResult> getScanResults(){
        mWifiManager.startScan();
        return mWifiManager.getScanResults();//得到扫描结果
    }

    /**
     * 获取SSID列表 （去重之后）
     * @return
     */
    public List<String> getSSIDList(){
        List<String> ssidList=new ArrayList<>();
        List<ScanResult>  results=getScanResults();

        for (int i = 0; i <results.size(); i++) {

            if (ssidList.contains(results.get(i).SSID)){

            }else {
                ssidList.add(results.get(i).SSID);
            }

        }
        return ssidList;
    }


    /**
     * 添加指定WIFI的配置信息,原列表不存在此SSID
     */

    public int AddWifiConfig(List<ScanResult> wifiList,String ssid,String pwd){
        int wifiId = -1;
        for(int i = 0;i < wifiList.size(); i++){
            ScanResult wifi = wifiList.get(i);
            if(wifi.SSID.equals(ssid)){
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.SSID = "\""+ssid+"\"";//\"转义字符，代表"
                wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK密码
                wifiCong.hiddenSSID = false;
                wifiCong.status = WifiConfiguration.Status.ENABLED;
                wifiId = mWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
                if(wifiId != -1){
                    return wifiId;
                }
            }
        }
        return wifiId;
    }


    /**
     * 连接指定SSID的WIFI
     */

    public boolean connectWiFi(String ssId ,String password){
        if (TextUtils.isEmpty(ssId)||TextUtils.isEmpty(password)){
            return false;
        }
        openWiFi();
        // getConfiguration();

        boolean wifiIsInScope=false;

        for (int i = 0; i <getSSIDList().size() ; i++) {
            if (getSSIDList().get(i).equals(ssId)){
                wifiIsInScope=true;
            };
        }
        if (!wifiIsInScope){
//            ToastMgr.show("WiFi不在范围内");
            return false;
        }
       /* else ToastMgr.show("WiFi连接中...");*/

        int netId= AddWifiConfig(getScanResults(),ssId,password);
        return mWifiManager.enableNetwork(netId,true);
    }
}
