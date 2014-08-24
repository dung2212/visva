package com.sharebravo.bravo.view.fragment;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sharebravo.bravo.R;
import com.sharebravo.bravo.sdk.log.AIOLog;
import com.sharebravo.bravo.sdk.provider.VolleyProvider;
import com.sharebravo.bravo.sdk.util.network.AsyncHttpPost;
import com.sharebravo.bravo.sdk.util.network.AsyncHttpResponseProcess;
import com.sharebravo.bravo.sdk.util.network.ParameterFactory;
import com.sharebravo.bravo.sdk.util.volley.IVolleyResponse;
import com.sharebravo.bravo.utils.BravoWebServiceConfig;
import com.sharebravo.bravo.utils.EmailValidator;

public class FragmentForgotPassword extends FragmentBasic {
    // ====================Constant Define=================
    // ====================Class Define====================
    private EmailValidator mEmailValidator;
    // ====================Variable Define=================
    private EditText       mEditTextEmailForgot;
    private Button         mBtnResetPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = (ViewGroup) inflater.inflate(R.layout.page_fragment_forgot_password, container);
        mEmailValidator = new EmailValidator();
        initializeView(root);
        return root;
    }

    private void initializeView(View root) {
        mEditTextEmailForgot = (EditText) root.findViewById(R.id.edittext_input_email_reset_pw);
        mBtnResetPassword = (Button) root.findViewById(R.id.btn_reset_password);
        mBtnResetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickResetPassword();
            }
        });
    }

    private void onClickResetPassword() {
        String email = mEditTextEmailForgot.getText().toString();
        if (checkValidateEmail(email)) {
            requestToCheckForgotPassword(email);
        } else
            mEditTextEmailForgot.setError(getString(R.string.email_not_valid));
    }

    private void requestToCheckForgotPassword(String email) {
        AIOLog.d("email:=" + email);
        HashMap<String, String> subParams = new HashMap<String, String>();
        subParams.put("Email", email);
        JSONObject jsonObject = new JSONObject(subParams);
        String subParamsStr = jsonObject.toString();
        List<NameValuePair> params = ParameterFactory.createSubParams(subParamsStr);
        AsyncHttpPost postForgotPassword = new AsyncHttpPost(getActivity(), new AsyncHttpResponseProcess(getActivity()) {
            @Override
            public void processIfResponseSuccess(String response) {
                AIOLog.d("response " + response);
            }

            @Override
            public void processIfResponseFail() {
                AIOLog.d("response error");
            }
        }, params, true);
        postForgotPassword.execute(BravoWebServiceConfig.URL_POST_FORGOT);
    }

    private boolean checkValidateEmail(String email) {
        if (mEmailValidator.validate(email))
            return true;
        else {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}