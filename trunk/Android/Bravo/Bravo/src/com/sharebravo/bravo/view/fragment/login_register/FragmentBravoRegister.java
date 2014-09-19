package com.sharebravo.bravo.view.fragment.login_register;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.internal.da;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sharebravo.bravo.R;
import com.sharebravo.bravo.control.activity.HomeActivity;
import com.sharebravo.bravo.model.SessionLogin;
import com.sharebravo.bravo.model.user.BravoUser;
import com.sharebravo.bravo.model.user.ObGetLoginedUser;
import com.sharebravo.bravo.sdk.log.AIOLog;
import com.sharebravo.bravo.sdk.util.network.AsyncHttpPost;
import com.sharebravo.bravo.sdk.util.network.AsyncHttpPostImage;
import com.sharebravo.bravo.sdk.util.network.AsyncHttpResponseProcess;
import com.sharebravo.bravo.sdk.util.network.ParameterFactory;
import com.sharebravo.bravo.utils.BravoConstant;
import com.sharebravo.bravo.utils.BravoSharePrefs;
import com.sharebravo.bravo.utils.BravoUtils;
import com.sharebravo.bravo.utils.BravoWebServiceConfig;
import com.sharebravo.bravo.utils.EmailValidator;
import com.sharebravo.bravo.utils.StringUtility;
import com.sharebravo.bravo.view.fragment.FragmentBasic;

public class FragmentBravoRegister extends FragmentBasic {
    // ====================Constant Define=================
    private static final int REQUEST_CODE_CAMERA  = 3003;
    private static final int REQUEST_CODE_GALLERY = 3004;
    private static final int CROP_FROM_CAMERA     = 3005;
    // ====================Class Define====================
    private EmailValidator   mEmailValidator;
    // ====================Variable Define=================
    private EditText         mEditTextUserName;
    private EditText         mEditTextUserEmail;
    private EditText         mEditTextPassWord;
    private Button           mBtnDoneRegisterBravo;
    private ImageView        mImgChoosePicture;
    private ImageView        mImgUserPicture;
    private Uri              mCapturedImageURI;
    private boolean          isUserNameNotValid;
    private boolean          isUserEmailNotValid;
    private boolean          isPasswordNotValid;

    private Bitmap           mUserAvatarBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = (ViewGroup) inflater.inflate(R.layout.page_fragment_bravo_register, container);

        initializeView(root);
        mEmailValidator = new EmailValidator();
        return root;
    }

    private void initializeView(View root) {
        mBtnDoneRegisterBravo = (Button) root.findViewById(R.id.btn_done_register_bravo);
        mBtnDoneRegisterBravo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (StringUtility.isEmpty(mEditTextPassWord)) {
                    isPasswordNotValid = true;
                    mEditTextPassWord.setText("");
                    mEditTextPassWord.setHint(getString(R.string.password_is_empty));
                    mEditTextPassWord.setHintTextColor(getActivity().getResources().getColor(R.color.red));
                }
                if (StringUtility.isEmpty(mEditTextUserEmail)) {
                    isUserEmailNotValid = true;
                    mEditTextUserEmail.setText("");
                    mEditTextUserEmail.setHint(getString(R.string.email_is_empty));
                    mEditTextUserEmail.setHintTextColor(getActivity().getResources().getColor(R.color.red));
                }
                if (!StringUtility.isEmpty(mEditTextUserName)) {
                    onClickRegisterBravoUser();
                }
                else {
                    isUserNameNotValid = true;
                    mEditTextUserName.setText("");
                    mEditTextUserName.setHint(getString(R.string.username_is_empty));
                    mEditTextUserName.setHintTextColor(getActivity().getResources().getColor(R.color.red));
                }
            }
        });

        mEditTextUserName = (EditText) root.findViewById(R.id.edittext_input_user_name_register);
        mImgUserPicture = (ImageView) root.findViewById(R.id.img_user_picture);
        mImgChoosePicture = (ImageView) root.findViewById(R.id.img_picture_choose);
        mImgChoosePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogChooseImage();
            }
        });
        mEditTextUserEmail = (EditText) root.findViewById(R.id.edittext_input_email_register);
        mEditTextPassWord = (EditText) root.findViewById(R.id.edittext_input_pass_register);
        mEditTextUserEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isUserEmailNotValid || mEditTextUserEmail.getText().length() == 0) {
                    isUserEmailNotValid = false;
                    mEditTextUserEmail.setText("");
                    mEditTextUserEmail.setHint(getString(R.string.email_address));
                    mEditTextUserEmail.setHintTextColor(getActivity().getResources().getColor(R.color.black));
                }
            }
        });
        mEditTextPassWord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isPasswordNotValid || mEditTextPassWord.getText().length() == 0) {
                    isPasswordNotValid = false;
                    mEditTextPassWord.setText("");
                    mEditTextPassWord.setHint(getString(R.string.pass_word));
                    mEditTextPassWord.setHintTextColor(getActivity().getResources().getColor(R.color.black));
                }
            }
        });
        mEditTextUserName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isUserNameNotValid || mEditTextUserName.getText().length() == 0) {
                    isUserNameNotValid = false;
                    mEditTextUserName.setText("");
                    mEditTextUserName.setHint(getString(R.string.text_user_name));
                    mEditTextUserName.setHintTextColor(getActivity().getResources().getColor(R.color.black));
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void onClickRegisterBravoUser() {
        String email = mEditTextUserEmail.getText().toString();
        String password = mEditTextPassWord.getText().toString();
        String name = mEditTextUserName.getText().toString();
        if (isValidEmail_PassWord(email, password)) {
            BravoUser _bravoUser = new BravoUser();
            _bravoUser.mUserEmail = email;
            _bravoUser.mUserName = name;
            _bravoUser.mUserPassWord = password;
            _bravoUser.mTimeZone = TimeZone.getDefault().getID();
            Locale current = getResources().getConfiguration().locale;
            _bravoUser.mLocale = current.toString();
            _bravoUser.mAuthenMethod = "Bravo";
            _bravoUser.mForeign_Id = "No";
            AIOLog.d("mTimeZone " + _bravoUser.mTimeZone);
            requestToPostBravoUser(_bravoUser);
        }
    }

    /**
     * request to register user to bravo server by bravo account
     * 
     * @param bravoUser
     */
    private void requestToPostBravoUser(final BravoUser bravoUser) {

        HashMap<String, String> subParams = new HashMap<String, String>();
        subParams.put("Auth_Method", bravoUser.mAuthenMethod);
        subParams.put("Full_Name", bravoUser.mUserName);
        subParams.put("Email", bravoUser.mUserEmail);
        subParams.put("Foreign_ID", bravoUser.mForeign_Id);
        subParams.put("Password", bravoUser.mUserPassWord);
        subParams.put("Time_Zone", bravoUser.mTimeZone);
        subParams.put("Locale", bravoUser.mLocale);
        JSONObject jsonObject = new JSONObject(subParams);
        String subParamsStr = jsonObject.toString();

        List<NameValuePair> params = ParameterFactory.createSubParams(subParamsStr);
        AsyncHttpPost postRegister = new AsyncHttpPost(getActivity(), new AsyncHttpResponseProcess(getActivity(), this) {
            @Override
            public void processIfResponseSuccess(String response) {
                AIOLog.d("response postRegister by bravo:===>" + response);
                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonObject == null)
                    return;

                String status = null;
                try {
                    status = jsonObject.getString("status");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                Gson gson = new GsonBuilder().serializeNulls().create();
                ObGetLoginedUser obPostUserFailed;
                if (status == String.valueOf(BravoWebServiceConfig.STATUS_RESPONSE_DATA_SUCCESS)) {
                    /* save data to share preferences */
                    BravoUtils.saveResponseToSharePreferences(getActivity(), BravoConstant.REGISTER_BY_BRAVO_ACC, response);

                    if (mUserAvatarBitmap != null) {
                        requestToPostUserWithAvatarImage(bravoUser, mUserAvatarBitmap);
                    } else {
                        /* go to home screen */
                        Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        getActivity().finish();
                    }
                } else {
                    obPostUserFailed = gson.fromJson(response.toString(), ObGetLoginedUser.class);
                    showToast(obPostUserFailed.error);
                }
            }

            @Override
            public void processIfResponseFail() {
                AIOLog.d("response error");
            }
        }, params, true);
        postRegister.execute(BravoWebServiceConfig.URL_POST_USER);
    }

    private void requestToPostUserWithAvatarImage(BravoUser bravoUser, Bitmap userAvatarBmp) {
        if (userAvatarBmp == null) {
            /* go to home screen */
            Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            getActivity().finish();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 1;
        options.inPurgeable = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userAvatarBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // bitmap object
        byte byteImage_photo[] = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte imageByte[] = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        if (bitmap == null)
            AIOLog.d("bitmap:" + bitmap);
        mImgUserPicture.setImageBitmap(bitmap);
        int _loginBravoViaType = BravoSharePrefs.getInstance(getActivity()).getIntValue(BravoConstant.PREF_KEY_SESSION_LOGIN_BRAVO_VIA_TYPE);
        SessionLogin _sessionLogin = BravoUtils.getSession(getActivity(), _loginBravoViaType);
        String userId = _sessionLogin.userID;
        String accessToken = _sessionLogin.accessToken;

        HashMap<String, String> subParams = new HashMap<String, String>();
        subParams.put("Profile_Img", encodedImage);
        subParams.put("Cover_Img", "");
        subParams.put("Profile_Img_Del", "0");
        subParams.put("Cover_Img_Del", "0");
        subParams.put("About_Me", "xxxxx");
        subParams.put("UserId", userId);

        JSONObject jsonObject = new JSONObject(subParams);
        String subParamsStr = jsonObject.toString();

        AIOLog.d("encodedImage:" + encodedImage);
        String putUserUrl = BravoWebServiceConfig.URL_PUT_USER.replace("{User_ID}", userId).replace("{Access_Token}", accessToken);
        AIOLog.d("putUserUrl:" + putUserUrl);
        List<NameValuePair> params = ParameterFactory.createSubParams(subParamsStr);
        AsyncHttpPostImage postRegister = new AsyncHttpPostImage(getActivity(), new AsyncHttpResponseProcess(getActivity(), this) {
            @Override
            public void processIfResponseSuccess(String response) {
                AIOLog.d("reponse after uploading image:" + response);
                /* go to home screen */
                Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                getActivity().finish();
            }

            @Override
            public void processIfResponseFail() {
                AIOLog.d("response error");
            }
        }, params, true);
        postRegister.execute(putUserUrl);

    }

    private void showDialogChooseImage() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View dialog_view = inflater.inflate(R.layout.dialog_choose_picture, null);
        Button btnTakeAPicture = (Button) dialog_view.findViewById(R.id.btn_take_picture);
        btnTakeAPicture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // when user click camera to get image
                try {
                    String fileName = "cover" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    mCapturedImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } catch (Exception e) {
                    AIOLog.e("exception:" + e.getMessage());
                }
                dialog.dismiss();

            }
        });
        Button btnChooseFromLibrary = (Button) dialog_view.findViewById(R.id.btn_choose_from_library);
        btnChooseFromLibrary.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // when user click gallery to get image
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button) dialog_view.findViewById(R.id.btn_choose_picture_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(dialog_view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        // This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        dialog.show();
    }

    @SuppressWarnings("static-access")
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        AIOLog.d("requestCode:" + requestCode + ", resultCode:" + resultCode + ", data:" + data);
        switch (requestCode) {
        case REQUEST_CODE_CAMERA:
            if (resultCode == getActivity().RESULT_OK) {
                if (mCapturedImageURI == null) {
                    AIOLog.d("mCapturedImageURI is null");
                    if (data == null || data.getExtras() == null)
                        return;
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (photo == null)
                        return;
                    else {
                        cropImageFromUri(data.getData());
//                        mUserAvatarBitmap = photo;
//                        mImgUserPicture.setImageBitmap(photo);
                        return;
                    }
                }

                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(mCapturedImageURI, projection, null, null, null);
                if (cursor == null) {
                    AIOLog.d("cursor is null");
                    return;
                }
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                // THIS IS WHAT YOU WANT!
                String capturedImageFilePath = cursor.getString(column_index_data);

                File file = new File(capturedImageFilePath);
                String imagePath = capturedImageFilePath;
                if (file.exists()) {
                    Uri fileUri = Uri.fromFile(file);
                    cropImageFromUri(fileUri);
//                    int orientation = BravoUtils.checkOrientation(fileUri);
//                    Bitmap bmp;
//                    bmp = BravoUtils.decodeSampledBitmapFromFile(imagePath, 100, 100, orientation);
//                    mUserAvatarBitmap = bmp;
//                    mImgUserPicture.setImageBitmap(bmp);
//                    mUserAvatarBitmap = bmp;
                }
            }
            break;
        case REQUEST_CODE_GALLERY:
            if (resultCode == getActivity().RESULT_OK) {
                AIOLog.d("data=" + data);
                if (data == null) {
                    AIOLog.d("Opps!Can not get data from gallery.");
                    return;
                }

                Uri uri = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imagePath = cursor.getString(columnIndex);
                cursor.close();

                File file = new File(imagePath);
                Uri fileUri = null;
                if (file.exists()) {
                    fileUri = Uri.fromFile(file);
                    cropImageFromUri(fileUri);
//                    int orientation = BravoUtils.checkOrientation(fileUri);
//                    Bitmap bmp;
//                    bmp = BravoUtils.decodeSampledBitmapFromFile(imagePath, 100, 100, orientation);
//                    mUserAvatarBitmap = bmp;
//                    mImgUserPicture.setImageBitmap(bmp);
//                    mUserAvatarBitmap = bmp;
                } else {
                    AIOLog.d("file don't exist !");
                }
            }
            break;
        case CROP_FROM_CAMERA:
            if (resultCode == getActivity().RESULT_OK) {
                Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    mUserAvatarBitmap = photo;
                    mImgUserPicture.setImageBitmap(photo);
                }
            }
            break;
        default:
            return;
        }
    }

    private boolean isValidEmail_PassWord(String email, String passWord) {
        if (mEmailValidator.validate(email))
            if (passWord.length() >= 8)
                return true;
            else {
                isPasswordNotValid = true;
                mEditTextPassWord.setText("");
                mEditTextPassWord.setHint(getString(R.string.password_not_valid));
                mEditTextPassWord.setHintTextColor(getActivity().getResources().getColor(R.color.red));
                return false;
            }
        else {
            isUserEmailNotValid = true;
            mEditTextUserEmail.setText("");
            mEditTextUserEmail.setHint(getString(R.string.email_not_valid));
            mEditTextUserEmail.setHintTextColor(getActivity().getResources().getColor(R.color.red));
            return false;
        }
    }

    private void cropImageFromUri(Uri uri) {
        // final ArrayList<CropOption> cropOptions = new
        // ArrayList<CropOption>();
        AIOLog.d("uri:" + uri);
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();
        AIOLog.d("size:" + size);
        if (size == 0) {
            showToast("Can not crop image");
            return;
        } else {
            intent.setData(uri);
            intent.putExtra("outputX", 500);
            intent.putExtra("outputY", 500);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            if (size >= 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                startActivityForResult(i, CROP_FROM_CAMERA);
            }
        }
    }
}