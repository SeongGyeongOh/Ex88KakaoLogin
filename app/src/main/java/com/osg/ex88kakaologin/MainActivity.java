package com.osg.ex88kakaologin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity {

    TextView tvName, tvEmail;
    CircleImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //키 해시 얻어와서 Logcat 창에 출력하기
        String hashKey = getKeyHash(this);
        Log.i("TAG", hashKey);

        //카카오로그인 버튼은 별도의 클릭이벤트 처리 없이 자동으로 웹뷰를 실행하여 로그인 웹페이지를 보여줌.
        //웹뷰의 로그인 응답결과를 받기 위한 세션(서버와 연결하는 통로같은 개념)을 연결해야 함
        Session.getCurrentSession().addCallback(sessionCallback);


        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        profileImg = findViewById(R.id.iv_profile);

    }

    //카카로 로그인 서버와 연결을 시도하는 세션작업의 결과를 듣는 리스너
    ISessionCallback sessionCallback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Toast.makeText(MainActivity.this, "로그인 세션 연결 성공", Toast.LENGTH_SHORT).show();

            //로그인된 사용자의 정보들을 얻어오기
            requestUserInfo();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
    };

    //로그인 사용자 정보 받기
    void requestUserInfo(){
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {          }
            @Override
            public void onSuccess(MeV2Response result) {
                UserAccount account = result.getKakaoAccount(); //사용자 정보 객체 얻어옴
                if(account==null) return;

                //1. 이메일 정보
                tvEmail.setText(account.getEmail());

                //2. 기본 프로필 정보(닉네임, 이미지, 섬네일 이미지)
                Profile profile = account.getProfile();
                if(profile==null) return;

                String nickname = profile.getNickname();
                String imgUrl = profile.getProfileImageUrl();
                String thumbnailImgUrl = profile.getThumbnailImageUrl();

                tvName.setText(nickname);
                Glide.with(MainActivity.this).load(imgUrl).into(profileImg);

            }
        });
    }

    //앱이 종료될 때 세션 종료하기
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    //카카오 키 해시 리턴하는 함수
    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("TAG", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    public void clickBtnOut(View view) {
        //로그아웃 버틴
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Toast.makeText(MainActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
            }
        }); 
    }
}