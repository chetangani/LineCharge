package com.transvision.linecharge.fragments;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.transvision.linecharge.LoginActivity;
import com.transvision.linecharge.MainActivity;
import com.transvision.linecharge.R;
import com.transvision.linecharge.posting.SendingData;
import com.transvision.linecharge.posting.SendingData.LC_Issue_Details;
import com.transvision.linecharge.values.FunctionsCall;
import com.transvision.linecharge.values.GetSetValues;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.transvision.linecharge.values.ConstantValues.DETAILS_UPDATE_FAILURE;
import static com.transvision.linecharge.values.ConstantValues.DETAILS_UPDATE_SUCCESS;

public class MainFragment extends Fragment implements View.OnClickListener {
    View view;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 10;

    private static Uri fileUri; // file url to store image/video
    static File mediaFile;

    EditText et_issue_handler, et_issue_number, et_issue, et_issue_desp;
    ImageView issue_pic;
    Bitmap bitmap;
    Button issue_submit_btn;
    String issue_handler="", issue_number="", issue="", issue_desp="", issue_image="", linemen_ID="", sub_division_code="";

    SharedPreferences.Editor editor;

    GetSetValues getSetValues;
    SendingData sendingData;
    FunctionsCall functionsCall;
    ProgressDialog progressDialog;

    public MainFragment() {
        // Required empty public constructor
    }

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DETAILS_UPDATE_SUCCESS:
                        progressDialog.dismiss();
                        reset_Content();
                        Toast.makeText(getActivity(), "Details updated Successfully..", Toast.LENGTH_SHORT).show();
                        et_issue_number.requestFocus();
                        break;

                    case DETAILS_UPDATE_FAILURE:
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Details not updated Successfully..!!\nPlease try Once again..", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        getSetValues = ((MainActivity) getActivity()).getGetSetValues();
        sendingData = new SendingData();
        functionsCall = new FunctionsCall();

        editor = ((MainActivity) getActivity()).getEditor();

        et_issue_handler = (EditText) view.findViewById(R.id.et_issue_handler);
        et_issue_number = (EditText) view.findViewById(R.id.et_line_num);
        et_issue_number.requestFocus();
        et_issue = (EditText) view.findViewById(R.id.et_line_issue);
        et_issue_desp = (EditText) view.findViewById(R.id.et_issue_desp);
        issue_pic = (ImageView) view.findViewById(R.id.issue_pic);
        issue_pic.setOnClickListener(this);
        issue_submit_btn = (Button) view.findViewById(R.id.submit_btn);
        issue_submit_btn.setOnClickListener(this);

        linemen_ID = getSetValues.getLinemen_id();
        sub_division_code = getSetValues.getSubdiv_code();
        et_issue_handler.setText(getSetValues.getLinemen_name());

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.issue_pic:
                captureImage();
                break;

            case R.id.submit_btn:
                submitdetails();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.logout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                getSetValues.setLinemen_id("");
                getSetValues.setLinemen_name("");
                getSetValues.setSubdiv_code("");
                editor.putBoolean("login", false);
                editor.putString("linemen_id", "");
                editor.putString("linemen_name", "");
                editor.putString("subdivision_code", "");
                editor.commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void submitdetails() {
        issue_handler = et_issue_handler.getText().toString();
        if (!TextUtils.isEmpty(issue_handler)) {
            issue_number = et_issue_number.getText().toString();
            if (!TextUtils.isEmpty(issue_number)) {
                issue = et_issue.getText().toString();
                if (!TextUtils.isEmpty(issue)) {
                    issue_desp = et_issue_desp.getText().toString();
                    if (!TextUtils.isEmpty(issue_desp)) {
                        progressDialog = ProgressDialog.show(getActivity(), "Update", "Updating result please wait..", true);
                        try {
                            issue_image = functionsCall.encodeImage(bitmap, 100);
                        } catch (OutOfMemoryError error) {
                            error.printStackTrace();
                            issue_image = functionsCall.encodeImage(bitmap, 75);
                        }
                        LC_Issue_Details lcIssueDetails = sendingData.new LC_Issue_Details(mHandler, getSetValues);
                        lcIssueDetails.execute(linemen_ID, issue_handler, issue, issue_desp, issue_image,
                                sub_division_code, issue_number);
                    } else Toast.makeText(getActivity(), "Enter Issue Resolved Description", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getActivity(), "Enter Line Issue", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(getActivity(), "Enter Issue Line Number", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getActivity(), "Enter Issue Handler Name", Toast.LENGTH_SHORT).show();
    }

    private void reset_Content() {
        et_issue_number.setText("");
        et_issue.setText("");
        et_issue_desp.setText("");
        issue_pic.setImageResource(R.drawable.camera);
        issue_number="";
        issue="";
        issue_desp="";
        issue_image="";
    }

    @TargetApi(24)
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, getActivity());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void previewCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            issue_pic.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            issue_pic.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
        }
    }

    public static Bitmap rotateImage(Bitmap src, String Imagepath) {
        Bitmap bmp = null;
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(Imagepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.d("debug", "" + orientation);
        if (orientation == 1) {
            bmp = src;
        } else if (orientation == 3) {
            matrix.postRotate(180);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else {
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        return bmp;
    }

    public Uri getOutputMediaFileUri(int type, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", getOutputMediaFile(type));
        } else return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(android.os.Environment.getExternalStorageDirectory(), "LineCharge");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("MMdd_HHmmss", Locale.getDefault()).format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }
}
