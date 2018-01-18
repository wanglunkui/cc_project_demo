package com.wlk.android.job_club.http;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wanglunkui on 2018/1/17.
 */

public class Common {
        public static boolean isApkDebugable(Context context, String packageName) {
            try {
                PackageInfo pkginfo = context.getPackageManager().getPackageInfo(
                        packageName, PackageManager.GET_ACTIVITIES);
                if (pkginfo != null) {
                    ApplicationInfo info = pkginfo.applicationInfo;
                    return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                }

            } catch (Exception e) {

            }
            return false;

        }

        public static String getVersion(Context context) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(
                        context.getPackageName(), 0);
                String version = info.versionName;
                return version;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        public static String getVersionCode(Context context) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(
                        context.getPackageName(), 0);
                String version = "" + info.versionCode;
                return version;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        public static String getAndroidVersion() {
            // TODO Auto-generated method stub
            return android.os.Build.VERSION.RELEASE;
        }

        public static String getModel() {
            String model = android.os.Build.MODEL;
            String brand = android.os.Build.BRAND;


            if (TextUtils.isEmpty(brand)) {
                brand = "(unknown)";
            }

            if (!TextUtils.isEmpty(model)) {
                brand = brand + " " + model;
            }

            return brand;

        }

        public static String toString(Object src, Class clx) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(src, clx);
            return jsonString;
        }

        public static boolean isMobile(String code, String phone) {
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(phone)) {
                return false;
            }
            Pattern p = Pattern.compile("^\\d{11}$");
            if (code.equals("852") || code.equals("853")) {
                p = Pattern.compile("^\\d{8}$");
            } else if (code.equals("886")) {
                p = Pattern.compile("^\\d{10}$");
            }
            Matcher m = p.matcher(phone);
            return m.matches();
        }
    }

