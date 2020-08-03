package com.osg.ex88kakaologin;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //카카오 SDK와 앱의 Application을 연결 - KakaoAdapter
        // SDK 초기화
        KakaoSDK.init(new KakaoAdapter() { //추상 클래스
            @Override
            //Application이 가지고 있는 정보를 얻기 위한 인터페이스
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return MyApplication.this; //나의 주소를 리턴
                    }
                };
            }
        });

    }
}
