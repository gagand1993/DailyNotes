package com.dailynotes.util.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.dailynotes.R;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;



public class CommonMethods {


    private static Dialog mProgress;


    // check is the given email is in valid format.
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



    public static Uri getImageUriFromBitmap(Context inContext, Bitmap inImage) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        String path=MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, String.valueOf(System.currentTimeMillis()), null);


        return Uri.parse(path);
    }



    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }



    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {


        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

//    public void failureMethod(Context context,String message){
//
//        if (message.equals(INVALID_AUTHKEY)){
//            Intent  intents = new Intent(context, IntroSliderActivity.class);
//            intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intents);
//
//        }else {
//            CommonMethods.failureMethod(context,message);
//        }
//    }






    // show the common progress which is used in all application
    public static void showProgress(Context mContext) {

        try {

            try {
                if (mProgress == null) {
                    mProgress = new Dialog(mContext);
                    mProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                mProgress.show();
                mProgress.setContentView(R.layout.custom_progress_dialog);
                mProgress.setCancelable(false);


            } catch (Exception e ) {
                e.printStackTrace();
                mProgress = null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            mProgress = null;
        }
    }



    public static String   difftime(long sDateStamp,long eDateStamp) {
        String finalValue="";
        try {
            long millisUntilFinished = 0;
            long oldLong  = 0;
            long NewLong   = 0;

            String startDateTime=CommonMethods.convertTimestampToDate(sDateStamp,"dd-MM-yyyy HH:mm");
            String endDateTime=CommonMethods.convertTimestampToDate(eDateStamp,"dd-MM-yyyy HH:mm");

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            Date sDate;
            Date eDate;
            try {
                sDate = formatter.parse(startDateTime);
                eDate = formatter.parse(endDateTime);
                oldLong = sDate.getTime();
                NewLong = eDate.getTime();
                millisUntilFinished = NewLong - oldLong;


                int noDays= (int) TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                int week=(noDays/7);
                int daysLeft=(noDays%7);

                String weeks = (String.valueOf(week));
                        String days =  String.valueOf(noDays) ;
                String Hours =String.valueOf((TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))) ;
                String mins = String.valueOf(((TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)))));



                if (!days.isEmpty()&&!days.equals("0")){
                    finalValue=days+" day ";

                }
                if (!Hours.isEmpty()&&!Hours.equals("0")){
                    finalValue=finalValue+Hours+" hour ";
                }

                if (!mins.isEmpty()&&!mins.equals("0")){
                    finalValue=finalValue+mins+" min ";
                }

            } catch (Exception e ) {
                e.printStackTrace();
            }
            return finalValue;

        } catch (Exception e) {
            return finalValue;
         }


    }

    public static  String parseDateToddMMyyyy(String dateAndTime,String dateFormate) {


        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(dateFormate);
        //ne line
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateAndTime);
            str = outputFormat.format(date);

            //new line
            outputFormat.setTimeZone(TimeZone.getDefault());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getFormattedDateLikeWhatsapp(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today " + android.text.format.DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday " + android.text.format.DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return android.text.format.DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return android.text.format.DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }


    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    // hide the common progress which is used in all application.
    public static void hideProgress() {
        try {
            if (mProgress != null) {
                mProgress.hide();
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mProgress = null;
        }
    }





    public static long calenderDateToTimeStamp( String str_date ,  String date_formate)  {
        long time_stamp  = Long.valueOf(0);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(date_formate);
            //SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date date = formatter.parse(str_date);
            time_stamp= date.getTime();
            time_stamp = time_stamp / 1000;
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (Exception ex ) {
            ex.printStackTrace();
        }

        //
        return time_stamp;
    }






    public static String convertTimestampToDate(long timestamp,String dateFormateStyle) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormateStyle);
        sdf.setTimeZone(tz);
        Date currenTimeZone = new Date(timestamp * 1000);
        return sdf.format(currenTimeZone);
    }







}
