package com.android.demo;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SystemInfoUtils {
    /**
     * 返回手机品牌
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 返回手机制造商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 返回手机型号
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 返回系统版本
     *
     * @return
     */
    public static String getVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getPhoneName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        return myDevice.getName();
    }

    /**
     * 返回手机当前屏幕宽带
     *
     * @return < 0 - 失败
     */
    public static int getWidth(Context context) {
        return getDispInfo(context)[0];
    }

    /**
     * 返回手机当前屏幕高度
     *
     * @return < 0 - 失败
     */
    public static int getHeight(Context context) {
        return getDispInfo(context)[1];
    }

    public static int[] info = null;

    public static int[] getCurrentPhonePixInfo(Context context) {
        if (info == null) {
            info = new int[2];
            Activity ac = (Activity) context;
            info[0] = ac.getWindowManager().getDefaultDisplay().getWidth();
            info[1] = ac.getWindowManager().getDefaultDisplay().getHeight();
        }
        return info;
    }

    public static String getAndroidId(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }

    /**
     * 返回手机分辨率（宽*高）。注意：返回值手机屏幕是否旋转无关。
     *
     * @return null - 失败
     */
    public static String getResolution(Context context) {
        int[] info = getDispInfo(context);
        if (info[0] == -1 || info[1] == -1) return null;
        if (info[2] == Surface.ROTATION_0 || info[2] == Surface.ROTATION_180)
            return String.valueOf(info[0]) + "x" + info[1];
        else return String.valueOf(info[1]) + "x" + info[0];
    }

    /**
     * 返回手机RAM总空间
     *
     * @return < 0 - 失败
     */
    public static long getRamTotal() {
        return getRamInfo()[0];
    }

    /**
     * 返回手机RAM剩余空间
     *
     * @return < 0 - 失败
     */
    public static long getRamFree() {
        return getRamInfo()[1];
    }

    /**
     * 返回手机ROM总空间
     *
     * @return < 0 - 失败
     */
    public static long getRomTotal() {
        return getFileInfo(getDataDirectory())[0];
    }

    /**
     * 返回手机ROM剩余空间
     *
     * @return < 0 - 失败
     */
    public static long getRomFree() {
        return getFileInfo(getDataDirectory())[1];
    }

    /**
     * 返回手机External Storage总空间
     *
     * @return < 0 - 失败
     */
    public static long getExternalStorageTotal() {
        if (!isExternalStorageAvailable()) return -1;
        return getFileInfo(getExternalStorageDirectory())[0];
    }

    /**
     * 返回手机External Storage剩余空间
     *
     * @return < 0 - 失败
     */
    public static long getExternalStorageFree() {
        if (!isExternalStorageAvailable()) return -1;
        return getFileInfo(getExternalStorageDirectory())[1];
    }

    /**
     * 返回手机SD Card总空间
     *
     * @return < 0 - 失败
     */
    public static long getSdCardTotal() {
        if (!isSdCardAvailable()) return -1;
        return getFileInfo(getSdCardDirectory())[0];
    }

    /**
     * 返回手机SD Card剩余空间
     *
     * @return < 0 - 失败
     */
    public static long getSdCardFree() {
        if (!isSdCardAvailable()) return -1;
        return getFileInfo(getSdCardDirectory())[1];
    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位)
     * 读取失败为"0000000000000000"
     */
    public static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {
            //读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    //查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        //提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1,
                                str.length());
                        //去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    //文件结尾
                    break;
                }
            }
        } catch (Exception ex) {
            //赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }

    /**
     * 返回手机CPU型号
     *
     * @return null - 失败
     */
    public static String getCpuName() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String[] array = reader.readLine().split(":\\s+", 2);
            return array[1].trim();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回手机CPU最大频率
     *
     * @return null - 失败
     */
    public static String getCpuMaxFreq() {
        return getCpuFreqInfo("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
    }

    /**
     * 返回手机CPU最小频率
     *
     * @return null - 失败
     */
    public static String getCpuMinFreq() {
        return getCpuFreqInfo("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
    }

    /**
     * 返回手机CPU当前频率
     *
     * @return null - 失败
     */
    public static String getCpuCurFreq() {
        return getCpuFreqInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
    }

    /**
     * 获取手机SIM卡的状态
     *
     * @return SIM_STATE_READY（5）- SIM卡正常，SIM_STATE_UNKNOWN（0）- 失败，其他 - SIM卡异常
     */
    public static int getSimState(Context context) {
        if (context == null) return TelephonyManager.SIM_STATE_UNKNOWN;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return TelephonyManager.SIM_STATE_UNKNOWN;
        return tm.getSimState();
    }

    public static int CHINA_MOBILE = 46000;
    public static int CHINA_UNICOM = 46001;
    public static int CHINA_TELECOM = 46003;

    /**
     * 获取手机SIM卡的运营商
     *
     * @return CHINA_MOBILE - 中国移动，CHINA_UNICOM - 中国联通，CHINA_TELECOM - 中国电信, -1 - SIM卡不存在或者未知运营商
     */
    public static int getSimOperator(Context context) {
        String imsi = getSimImsi(context);
        if (imsi == null) return -1;
        if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
            return CHINA_MOBILE;
        } else if (imsi.startsWith("46001")) {
            return CHINA_UNICOM;
        } else if (imsi.startsWith("46003")) {
            return CHINA_TELECOM;
        } else {
            return -1;
        }
    }

    /**
     * 获取手机SIM卡的IMSI地址
     *
     * @return null - 不存在或失败
     */
    public static String getSimImsi(Context context) {
        if (context == null) return null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null || tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT) return null;
        return tm.getSubscriberId();
    }


    public static String getImei(Context context) {
        String id;
        //android.telephony.TelephonyManager
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getImei() != null) {
            id = mTelephony.getImei();
        } else {
            //android.provider.Settings;
            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }


    public static String getDeviceId(Context context) {
        String id;
        //android.telephony.TelephonyManager
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            id = mTelephony.getDeviceId();
        } else {
            //android.provider.Settings;
            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    /**
     * 获取WIFI的MAC地址
     *
     * @return null - 不存在或失败
     */
    public static String getWifiMac(Context context) {
        if (context == null) return null;
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) return null;
        WifiInfo info = wifi.getConnectionInfo();
        if (info == null) return null;
        return info.getMacAddress();
    }

    /**
     * 检查网络是否可用
     *
     * @return true/false
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn == null) return false;
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) return false;
        return true;
    }

    /**
     * 检查ExternalStorage是否可用。
     * 注意：ExternalStorage并不一定就是SD Card，根据不同手机的具体实现不同，ExternalStorage有可能只是手机内部存储。
     *
     * @return true/false
     */
    public static boolean isExternalStorageAvailable() {
        String status = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(status)) return false;
        return true;
    }

    /**
     * 检查SD Card是否可用
     *
     * @return true/false
     */
    public static boolean isSdCardAvailable() {
        if (isExternalStorageIsSdCard()) return isExternalStorageAvailable();
        List<String> info = getExternalStorageInfo();
        if (info == null || info.size() == 0) return false;
        return true;
    }

    /**
     * 返回Exetrnal Storage是否是SD Card
     *
     * @return true/false
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageIsSdCard() {
        if (Build.VERSION.SDK_INT < 9) return true;
        else return Environment.isExternalStorageRemovable();
    }

    /**
     * 返回系统数据分区的文件路径
     *
     * @return
     */
    public static String getDataDirectory() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    /**
     * 返回当前app的data目录路径
     *
     * @return
     */
    public static String getAppDirectory(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 返回External Storage分区的文件路径
     *
     * @return
     */
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 返回SD Card分区的文件路径
     *
     * @return null - SD Card不存在或者暂时不可用
     */
    public static String getSdCardDirectory() {
        if (isExternalStorageIsSdCard()) return getExternalStorageDirectory();
        List<String> info = getExternalStorageInfo();
        if (info == null || info.size() == 0) return null;
        return info.get(0);
    }

    // 内部函数
    private static int[] getDispInfo(Context context) {
        if (context == null) return new int[]{-1, -1};
        Display dm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (dm == null) return new int[]{-1, -1};
        if (Build.VERSION.SDK_INT < 14) {
            return new int[]{dm.getWidth(), dm.getHeight(), dm.getRotation()};
        } else {
            try {
                Point size = new Point();
                Method method = dm.getClass().getMethod("getRealSize", Point.class);
                method.invoke(dm, size);
                return new int[]{size.x, size.y, dm.getRotation()};
            } catch (Exception e) {
                return new int[]{-1, -1};
            }
        }
    }

    private static long[] getRamInfo() {
        BufferedReader reader = null;
        long freeRam = -1, totalRam = -1;
        try {
            reader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
            String line1 = reader.readLine();
            if (line1 != null) {
                line1 = line1.replace("MemTotal:", "").replace("kB", "").trim();
                totalRam = Long.valueOf(line1) * 1024;
            }
            String line2 = reader.readLine();
            if (line2 != null) {
                line2 = line2.replace("MemFree:", "").replace("kB", "").trim();
                freeRam = Long.valueOf(line2) * 1024;
            }
        } catch (IOException e) {
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new long[]{totalRam, freeRam};
    }

    private static String getCpuFreqInfo(String name) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(name));
            return reader.readLine().trim();
        } catch (IOException ex) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static long[] getFileInfo(String path) {
        StatFs stat = new StatFs(path);
        return new long[]{(long) stat.getBlockCount() * (long) stat.getBlockSize(), (long) stat.getAvailableBlocks() * (long) stat.getBlockSize()};
    }

    private static List<String> getExternalStorageInfo() {
        BufferedReader reader = null;
        List<String> info = new ArrayList<String>(5);
        try {
            Process process = Runtime.getRuntime().exec("mount");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String columns[] = line.split(" ");
                if (columns == null || columns.length < 4) continue;
                if (columns[1].contains("secure")) continue;
                if (columns[1].contains("asec")) continue;
                if (!columns[2].contains("fat")) continue;
                if (!columns[3].contains("rw")) continue;
                info.add(columns[1]);
            }
        } catch (IOException e) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        info.remove(getExternalStorageDirectory());
        return info;
    }

    public static String getIPAddress(Context mcontext) {
        NetworkInfo info = ((ConnectivityManager)
                mcontext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) mcontext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    public static String getNetWorkStates(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return "no";//没网
        }

        int type = activeNetworkInfo.getType();
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:
                return "4g/3g";//移动数据
            case ConnectivityManager.TYPE_WIFI:
                return "wifi";//WIFI
            default:
                break;
        }
        return "no";
    }

}



//        android_id=f8eb69799b794f0d
//        WIF-SSID=DL-402260
//        WIF-BSSID=73:3f:b9:aa:34:39
//        WIF-MacId=62:69:3e:d7:29:9b
//        subscriberId=460014250210286
//        simOperator=46001
//        simState=5
