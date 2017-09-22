package com.transvision.linecharge.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.transvision.linecharge.LoginActivity;
import com.transvision.linecharge.MainActivity;
import com.transvision.linecharge.R;
import com.transvision.linecharge.posting.SendingData;
import com.transvision.linecharge.posting.SendingData.LC_Login;
import com.transvision.linecharge.values.GetSetValues;

import static com.transvision.linecharge.values.ConstantValues.LOGIN_FAILURE;
import static com.transvision.linecharge.values.ConstantValues.LOGIN_SUCCESS;

public class LoginFragment extends Fragment {
    View view;

    Button login_btn;
    EditText et_login_id, et_password;
    String login_id="", password="";
    ProgressDialog progressDialog;

    GetSetValues getSetValues;
    SendingData sendingData;

    SharedPreferences.Editor editor;

    public LoginFragment() {
        // Required empty public constructor
    }

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LOGIN_SUCCESS:
                        progressDialog.dismiss();
                        getSetValues.setLinemen_id(login_id);
                        editor.putBoolean("login", true);
                        editor.putString("linemen_id", login_id);
                        editor.putString("linemen_name", getSetValues.getLinemen_name());
                        editor.putString("subdivision_code", getSetValues.getSubdiv_code());
                        editor.commit();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("values", getSetValues);
                        startActivity(intent);
                        getActivity().finish();
                        break;

                    case LOGIN_FAILURE:
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Login ID & Password are not matching... Please check it..", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        getSetValues = ((LoginActivity) getActivity()).getGetSetValues();
        sendingData = new SendingData();

        login_btn = (Button) view.findViewById(R.id.login_btn);
        et_login_id = (EditText) view.findViewById(R.id.et_login_id);
        et_password = (EditText) view.findViewById(R.id.et_login_pass);

        editor = ((LoginActivity) getActivity()).getEditor();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_id = et_login_id.getText().toString();
                if (!TextUtils.isEmpty(login_id)) {
                    password = et_password.getText().toString();
                    if (!TextUtils.isEmpty(password)) {
                        progressDialog = ProgressDialog.show(getActivity(), "Login", "Logining In please wait..", true);
                        LC_Login lcLogin = sendingData.new LC_Login(mHandler, getSetValues);
                        lcLogin.execute(login_id, password);
                    } else {
                        et_password.setError("Enter Password..");
                        Toast.makeText(getActivity(), "Enter Password..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    et_login_id.setError("Enter User Login ID..");
                    Toast.makeText(getActivity(), "Enter User Login ID..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }
}
