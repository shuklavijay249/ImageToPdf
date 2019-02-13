package com.example.imagetopdfexample;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import vijay.createpdf.PathFile;
import vijay.createpdf.activity.ImgToPdfActivity;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */


public class LoanApplicationFragment_3 extends Fragment {

    static Context context;
    public static Fragment mFragment;
    public Button uploadimages,pdfOpen,btnDownload;
    static View view;
    public static String coBorrowerID = "";
    public String applicantType = "", documentTypeNo = "", userID = "";
    public int REQUEST_CAMERA = 0, SELECT_FILE = 1, SELECT_DOC = 2;
    public String userChoosenTask;
    static FragmentTransaction transaction;
    String uploadFilePath = "";

    StringBuffer sb;

    public LoanApplicationFragment_3() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_loan, container, false);
        context = getContext();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mFragment = new vijay.createpdf.activity.LoanApplicationFragment_3();

        uploadimages = (Button) view.findViewById(R.id.uploadimages);
        pdfOpen = (Button) view.findViewById(R.id.pdfOpen);
        btnDownload = (Button) view.findViewById(R.id.btnDownload);
        transaction = getFragmentManager().beginTransaction();

        uploadimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimages.setVisibility(View.GONE);
                applicantType = "1_SD_PhotoDoc";
                documentTypeNo = "1";
                imageToPdf();
            }
        });

        return view;
    }

    private void imageToPdf() {
        Intent intent = new Intent(getActivity(), ImgToPdfActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("strapplicantId", "523");
        bundle.putString("strapplicantType", "1");
        bundle.putString("documentTypeNo", "31");
        bundle.putString("toolbarTitle", "Upload PAN");
        bundle.putString("note", "Applicant's Profile Picture - Applicant's single picture required to be uploaded");
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);// Activity is started with requestCode 2
    }

    private Bitmap decodeUri(Uri selectedImage,Context context) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(selectedImage), null, o2);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {

            String message = data.getStringExtra("PATH");
            String documentTypeNo = data.getStringExtra("documentTypeNo");
            String strapplicantType = data.getStringExtra("strapplicantType");
            String strapplicantId = data.getStringExtra("strapplicantId");

            Log.e(TAG, "documentTypeNo: " + documentTypeNo + "strapplicantType: " + strapplicantType + "strapplicantId: " + strapplicantId  + "message: " + message);

        }
        if (requestCode == 1) {
            String message = data.getStringExtra("BACK");
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == SELECT_DOC) {
                Bitmap bm = null;
                String FileExtn = null;
                Long FileSize = null;
                try {//mDensity = 440 mHeight = 375 mWidth = 500
                    bm = decodeUri(data.getData(),context);//5383513
//                    bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri selectedImage = data.getData();
                uploadFilePath = PathFile.getPath(context, selectedImage);

                try {
                    Cursor returnCursor =
                            context.getContentResolver().query(selectedImage, null, null, null, null);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();

                    FileSize = returnCursor.getLong(sizeIndex);//5383513 //26435143

                } catch (Exception e) {
                    e.printStackTrace();
                }

                FileExtn = uploadFilePath.substring(uploadFilePath.lastIndexOf(".")+1);// Without dot jpg, png

                if(FileExtn.equals("jpg") || FileExtn.equals("jpeg") || FileExtn.equals("png") || FileExtn.equals("pdf")||
                        FileExtn.equals("bmp") || FileExtn.equals("webp") || FileExtn.equals("zip") || FileExtn.equals("rar")) {

                    if (FileSize < 30000000) {
                        Log.e("TAG", "onActivityResult: DOC PATH " + uploadFilePath);

                    }
                    else{
                        Toast.makeText(context, "File size exceeds limit of 30 MB", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(context, "file is not in supported format", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        uploadFilePath = destination.toString();
        Log.e("TAG", "onCaptureImageResult: " + uploadFilePath);
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                Uri selectedFileUri = data.getData();
                uploadFilePath = PathFile.getPath(context, selectedFileUri);
                Log.e("TAG", "onSelectFromGalleryResult: " + uploadFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
