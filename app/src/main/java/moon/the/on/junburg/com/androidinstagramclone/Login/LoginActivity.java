package moon.the.on.junburg.com.androidinstagramclone.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import moon.the.on.junburg.com.androidinstagramclone.Home.HomeActivity;
import moon.the.on.junburg.com.androidinstagramclone.R;

/**
 * Created by Junburg on 2018. 3. 26..
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Variables
    private Context mContext;

    // Widgets
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started");
        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();
    }

    /**
     * 입력한 문자열이 공백이라면 ture return, 그 반대의 경우 false return
     */
    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking string if null.");
        if (string.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /*
   ---------------------------------------firebase-------------------------------------------------
    */

    /**
     * 로그인 이벤트 처리
     */
    private void init() {

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:  attempting to log in");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                // 입력한 문자열 공백 확인
                if (isStringNull(email) && isStringNull(password)) {
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    // 이메일과 패스워드를 사용한 로그인
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "onComplete: " + task.isSuccessful());
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // 로그인 작업에 실패 했을 경우
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "failed", task.getException());

                                        Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                                Toast.LENGTH_SHORT).show();

                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);
                                    } else {
                                        // 사용자에게 가입 인증 메일을 보냈는지 확인
                                        // true라면 HomeActivity로, false라면 메세지와 함께 Sign Out
                                        try {
                                            if (user.isEmailVerified()) {
                                                Log.d(TAG, "onComplete: success. email is verified");
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(mContext, "Email is not verified \n check your mail box", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }
                                        } catch (NullPointerException e) {
                                            Log.e(TAG, "onComplete: NullPointerException" + e.getMessage());
                                        }
                                    }
                                }
                            });
                }
            }
        });

        // Register Activity로 이동하는 텍스트 뷰 클릭 리스너 설정
        TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick:  navigating to register screen");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        // 사용자가 로그인 되어 있으면 HomeActivity로 넘어가고 LoginActivity는 종료
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 사용자 관련 객체 setup
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    /**
     *  onStart 생명주기에서 로그인 상태 리스너 추가
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    /**
     *  onStop 생명주기에서 로그인 상태 리스너 추가
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
