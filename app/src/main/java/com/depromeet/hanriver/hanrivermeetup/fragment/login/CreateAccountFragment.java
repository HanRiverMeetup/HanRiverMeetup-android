package com.depromeet.hanriver.hanrivermeetup.fragment.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.depromeet.hanriver.hanrivermeetup.R;
import com.depromeet.hanriver.hanrivermeetup.activity.main.MainActivity;
import com.depromeet.hanriver.hanrivermeetup.common.PreferencesManager;
import com.depromeet.hanriver.hanrivermeetup.service.LoginService;
import com.facebook.login.Login;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.content.Context.MODE_PRIVATE;

public class CreateAccountFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        Button registerButton = view.findViewById(R.id.create_nickname_button);
        AppCompatEditText editText = view.findViewById(R.id.edit_nickname);

        registerButton.setOnClickListener(v -> {
            String nickName = editText.getText().toString();

            if(!nickName.isEmpty()) {
                LoginService.getInstance().registerUser(nickName)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(res -> {
                            if(res.code() == HttpsURLConnection.HTTP_OK) {
                                PreferencesManager.setNickname(res.body().nickname);
                                intentMain();
                            }
                            else {
                                Toast.makeText(getActivity(), "이미 가입된 아이디입니다.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .subscribe();
            }
        });

        return view;
    }

    private void intentMain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.getActivity().finish();
    }
}