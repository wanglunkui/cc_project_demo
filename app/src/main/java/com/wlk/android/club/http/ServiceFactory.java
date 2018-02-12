package com.wlk.android.club.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wanglunkui on 2018/1/17.
 */

public class ServiceFactory {
    public static String CLUB_KEY = "132ac1d48740a11e6bccc00163e000233";
    public static String CLUB_SECRET = "q32ac1ff0740a11e6bccc00163e000233";
    private static String BASE_URL = "ahttps://union.codoon.com";
    private Retrofit client;
    private Context mContext;
    private OkHttpClient.Builder httpClient;

    private static ServiceFactory instance;

    public static ServiceFactory getInstance(Context context){
        if(instance == null){
            synchronized (ServiceFactory.class){
                if(instance == null){
                    instance = new ServiceFactory(context);
                }
            }
        }
        return instance;
    }

    private ServiceFactory(Context context) {
        this.mContext = context;
        httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                builder.addHeader("Cache-Control", "max-age=0");
                String authCombination = CLUB_KEY + ":" + CLUB_SECRET;
                byte[] tmpByte = authCombination.getBytes();
                String encodeStr = Base64.encode(tmpByte, 0, tmpByte.length);
                builder.addHeader("Authorization", "Basic " + encodeStr);
                String timeStampStr = String.valueOf(System.currentTimeMillis() / 1000);
                String tokenString = "0";
                String urlPath = chain.request().url().toString();
                String paraStr = bodyToString(chain.request().body());
                String gaeaStr = getGaea(mContext, urlPath, paraStr,
                        tokenString, timeStampStr);
                builder.addHeader("Gaea", gaeaStr);
                builder.addHeader("Uranus", getUranus(mContext, gaeaStr, timeStampStr));
                builder.addHeader("Timestamp", timeStampStr);
                builder.addHeader("User-Agent", getUserAgent(mContext));
//                builder.addHeader("Accept-Encoding", "gzip");
                builder.addHeader("did", getImei(mContext));
                // for cmwap proxy
                String urlt = urlPath.toString();
                String pUrl = "https://10.0.0.172";
                urlt = urlt.replace("https://", "");
                pUrl = pUrl + urlt.substring(urlt.indexOf("/"));
                urlt = urlt.substring(0, urlt.indexOf("/"));
                URL url;
                // 如果是wap连接
                if (isCmwap(mContext)) {
                    url = new URL(pUrl);
                    builder.addHeader("X-Online-Host", urlt);

                } else {
                    url = new URL(urlPath.toString());
                }
                builder.url(url);


                Request request = builder.build();
                return chain.proceed(request);
            }
        });
        client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <T> T getClient(Class<T> cls) {
        return client.create(cls);
    }

    public static boolean isCmwap(Context context) {

        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }

        String extraInfo = info.getExtraInfo();

        if (TextUtils.isEmpty(extraInfo) || (extraInfo.length() < 3)) {
            return false;
        }

        if (extraInfo.toLowerCase().indexOf("wap") > 0) {
            return true;
        }
        return false;
    }


    @SuppressLint("MissingPermission")
    public static String getImei(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceidString = mTelephonyMgr.getDeviceId();
        if (deviceidString == null || deviceidString.length() == 0) {
            final String tmDevice, tmSerial, tmPhone, androidId;
            tmDevice = "" + mTelephonyMgr.getDeviceId();
            tmSerial = "" + mTelephonyMgr.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceidString = deviceUuid.toString();
        }
        // 针对咕咚运动+Android客户端的IMEI，在前面添加"24-"

        try {
            int device = 1;
            device = Integer.parseInt(deviceidString);
            if (device == 0) {
//                deviceidString = UserData.GetInstance(context)
//                        .GetUserBaseInfo().id;
                deviceidString = "anonymous";
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "24-" + deviceidString;

    }


    public static String getUranus(Context context, String gaeaStr, String timeStampStr) {
        String uranusStr = gaeaStr+"^"+timeStampStr;
        String uranusDESStr = "";
        try {
            uranusDESStr = DESUitls.encode(uranusStr);
        } catch (Exception e) {

        }
        return uranusDESStr;
    }

    public static String getUserAgent(Context context) {
//        String userAgent = "CodoonSport" + "(" + Common.getVersion(context)
//                + " " + Common.getVersionCode(context) + ";android "
//                + Common.getAndroidVersion() + ";" + Common.getModel() + ")";

        String userAgent = "CodoonUserSDK" + "(" + Common.getVersion(context)
                + " " + Common.getVersionCode(context) + ";android "
                + Common.getAndroidVersion() + ";" + Common.getModel() + ")";
        return userAgent;
    }

    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "";
        }
    }

    public static String getGaea(Context context, String pathStr, String paramStr,
                                 String tokenStr, String timeStampStr) {
        if (TextUtils.isEmpty(paramStr)) {
            paramStr = "0";
        }
        String signStr = pathStr +"^"+ paramStr +"^"+ tokenStr +"^"+ timeStampStr;
        String signMD5Str = MD5Utils.encode(signStr);
        return signMD5Str;
    }
}
