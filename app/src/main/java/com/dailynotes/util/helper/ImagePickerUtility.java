package com.dailynotes.util.helper;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

 import com.dailynotes.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class ImagePickerUtility extends AppCompatActivity {

    Activity activity;
    int requestCodeGallary = 159;
    int requestCodeCamera = 1231;
    String imgPath="";
    PermissionListener permissionlistener;

    public  ImagePickerUtility(){

    }


    public void ImagePickers(Activity activity){
        this.activity=activity;
        startTed();
    }

    public void startTed(){

        permissionlistener=new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionForCameraGallery();
                        //   return false;
                    }
                    else {
                        uploadImage();
                        //  return true;
                    }
                } else {
                    uploadImage();
                    // return true;
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
               // tedPermission();
            }
        };

        tedPermission();
       /* permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                //Ask for permission
                ImagePickers(mContext)
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(this@SignupActivity, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT).show()
            }
        }*/

    }

    public void tedPermission(){
        TedPermission.with(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleConfirmText("Permissions")
                .setRationaleTitle("Permission required.")
                .setRationaleMessage("We need this permission for image picker..")
                .setDeniedTitle("Permission denied")
                .setDeniedMessage(
                        "If you reject permission,you can not use image picker\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("Settings")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void uploadImage() {
        Dialog uploadImage = new Dialog(activity, R.style.Theme_Dialog);
        uploadImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        uploadImage.setContentView(R.layout.camera_gallery_popup);

        uploadImage.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        uploadImage.setCancelable(true);
        uploadImage.setCanceledOnTouchOutside(true);
        uploadImage.getWindow().setGravity(Gravity.BOTTOM);

        uploadImage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvCamera = uploadImage.findViewById(R.id.tvCamera);
        TextView tvGallery = uploadImage.findViewById(R.id.tvGallery);
        TextView tv_cancel = uploadImage.findViewById(R.id.tv_cancel);

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage.dismiss();


                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {

                        File myDirectory = new File(Environment.getExternalStorageDirectory(), "Pictures");

                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }

                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        //Log.i(TAG, "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                       activity.startActivityForResult(cameraIntent, requestCodeCamera);
                    }
                }
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage.dismiss();
            }
        });


        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage.dismiss();

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCodeGallary);

            }
        });


        uploadImage.show();


    }


    private File createImageFile() throws IOException {
        File image = null;

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";


        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName, // prefix
                ".jpg", // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        imgPath = "file:" + image.getAbsolutePath();

        return image;
    }




    public void requestPermissionForCameraGallery() {
      /*  requestPermissions(
                new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE_FOR_SCANNER
        );*/
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 14758);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 14758: {


                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                    // main logic
                    uploadImage();
                } else {
                    /*Toast.makeText(activity!!,"Permission Denied",Toast.LENGTH_SHORT).show()*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissionForCameraGallery();
                            }

                        }
                    }
                }
                break;
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCodeCamera && resultCode == Activity.RESULT_OK) {
            try {


                if (Uri.parse(imgPath) != null) {
                    CropImage.activity(Uri.parse(imgPath))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(activity);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == requestCodeGallary && resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();

                if (uri != null) {
                    if (uri != null) {
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(activity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == AppCompatActivity.RESULT_OK) {

                    Uri uri = result.getUri();
                    if (uri != null) {
                        imgPath = CommonMethods.getPath(activity, uri);

                        selectedImage(imgPath,uri);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    public abstract void selectedImage(String imagePath,Uri uri);


}
