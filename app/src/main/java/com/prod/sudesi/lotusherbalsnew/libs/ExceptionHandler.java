package com.prod.sudesi.lotusherbalsnew.libs;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.NotificationCompat;


import com.prod.sudesi.lotusherbalsnew.R;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by tushar on 9/13/16.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Activity myContext;
    private final String LINE_SEPARATOR = "\n";
    private static final String CRASH_LOG_MAIL_SUBJECT = " crash log";
    private static final String CRASH_LOG_MAIL_BODY = "Following is the crash log info of the application.";
    private String
            VersionName,
            PackageName,
            FilePath,
            PhoneModel,
            AndroidVersion,
            Board,
            Brand,
            CPU_ABI,
            Device,
            Display,
            FingerPrint,
            Host,
            ID,
            Manufacturer,
            Model,
            Product,
            Tags,
            Type,
            User;
    private long Time;

    public ExceptionHandler(Activity context) {
        myContext = context;

    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();

        collectDeviceInformation();//newly added

        errorReport.append("\n\nCrash Info :\n--------------\n\n");//newly added
        errorReport.append(createCrashLog());//newly added

        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");

        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append(LINE_SEPARATOR);

        errorReport.append("Total Internal memory : " +
                getTotalInternalMemorySize());
        errorReport.append("\n");
        errorReport.append("Available Internal memory : " +
                getAvailableInternalMemorySize());
        errorReport.append("\n");

        createNotification(myContext, errorReport.toString());

        android.os.Process.killProcess(android.os.Process.myPid());

        System.exit(10);
        //createCrashLog(myContext);

    }

    public void createNotification(Context context, String log) {

        String applicationName = "Lotus";

        CharSequence tickerText = applicationName + " error";
        long when = System.currentTimeMillis();

        String subject = applicationName + CRASH_LOG_MAIL_SUBJECT;
        String body = CRASH_LOG_MAIL_BODY + "\n" + log;

        Intent sendIntent = new Intent(Intent.ACTION_SEND);

        String[] strEmails = new String[]{
                "tushar@adstringo.in","mahesh@sudesi.in",
        };
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

        sendIntent.putExtra(Intent.EXTRA_EMAIL, strEmails);
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0,
                        Intent.createChooser(sendIntent, tickerText), 0);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(when)
                .setSmallIcon(R.drawable.logo)
                .setTicker(tickerText)
                .setContentTitle(tickerText + " report. Click to review and send.")
                .setContentText("from " + applicationName + " Service")
                //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

        //context.finish();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(homeIntent);
    }

    public String createCrashLog() {
        StringBuffer logBuf = new StringBuffer();
        logBuf.append("Version : " + VersionName);
        logBuf.append("\n");
        logBuf.append("Package : " + PackageName);
        logBuf.append("\n");
        logBuf.append("FilePath : " + FilePath);
        logBuf.append("\n");
        logBuf.append("Phone Model" + PhoneModel);
        logBuf.append("\n");
        logBuf.append("Android Version : " + AndroidVersion);
        logBuf.append("\n");
        logBuf.append("Board : " + Board);
        logBuf.append("\n");
        logBuf.append("Brand : " + Brand);
        logBuf.append("\n");
        logBuf.append("CPU ABI : " + CPU_ABI);
        logBuf.append("\n");
        logBuf.append("Device : " + Device);
        logBuf.append("\n");
        logBuf.append("Display : " + Display);
        logBuf.append("\n");
        logBuf.append("Finger Print : " + FingerPrint);
        logBuf.append("\n");
        logBuf.append("Host : " + Host);
        logBuf.append("\n");
        logBuf.append("ID : " + ID);
        logBuf.append("\n");
        logBuf.append("Manufacturer : " + Manufacturer);
        logBuf.append("\n");
        logBuf.append("Model : " + Model);
        logBuf.append("\n");
        logBuf.append("Product : " + Product);
        logBuf.append("\n");
        logBuf.append("Tags : " + Tags);
        logBuf.append("\n");
        logBuf.append("Time : " + Time);
        logBuf.append("\n");
        logBuf.append("Type : " + Type);
        logBuf.append("\n");
        logBuf.append("User : " + User);
        logBuf.append("\n");
        logBuf.append("Total Internal memory : " +
                getTotalInternalMemorySize());
        logBuf.append("\n");
        logBuf.append("Available Internal memory : " +
                getAvailableInternalMemorySize());
        logBuf.append("\n");
        return logBuf.toString();
    }

    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private void collectDeviceInformation() {
        PackageManager pm = myContext.getPackageManager();
        try {
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(myContext.getPackageName(), 0);
            VersionName = pi.versionName;
            // Package name
            PackageName = pi.packageName;
            // Files dir for storing the stack traces
            //FilePath = Configurator.getInstance(context).getLogsDirectory().getAbsolutePath();
            FilePath = myContext.getFilesDir().getAbsolutePath();
            // Device model
            PhoneModel = Build.MODEL;
            // Android version
            AndroidVersion = Build.VERSION.RELEASE;

            Board = Build.BOARD;
            Brand = Build.BRAND;
            CPU_ABI = Build.CPU_ABI;
            Device = Build.DEVICE;
            Display = Build.DISPLAY;
            FingerPrint = Build.FINGERPRINT;

            Host = Build.HOST;
            ID = Build.ID;
            Manufacturer = Build.MANUFACTURER;
            Model = Build.MODEL;
            Product = Build.PRODUCT;
            Tags = Build.TAGS;
            Time = Build.TIME;
            Type = Build.TYPE;
            User = Build.USER;
        } catch (PackageManager.NameNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
