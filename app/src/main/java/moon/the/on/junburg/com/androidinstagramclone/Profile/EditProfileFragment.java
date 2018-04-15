package moon.the.on.junburg.com.androidinstagramclone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;
import moon.the.on.junburg.com.androidinstagramclone.Dialogs.ConfirmPasswordDialog;
import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Share.ShareActivity;
import moon.the.on.junburg.com.androidinstagramclone.Utils.FirebaseMethods;
import moon.the.on.junburg.com.androidinstagramclone.Utils.UniversalImageLoader;
import moon.the.on.junburg.com.androidinstagramclone.models.User;
import moon.the.on.junburg.com.androidinstagramclone.models.UserAccountSettings;
import moon.the.on.junburg.com.androidinstagramclone.models.UserSettings;

/**
 * Created by Junburg on 2018. 3. 26..
 */

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener {

    // ConfirmPasswordDialog에서 선언한 OnConfirmPasswordListener를 implements, 구현
    @Override
    public void onConfirmPassword(String password) {
        // 사용자의 이메일과 패스워드를 get
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        // 재가입 요청
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) { // 재가입이 허락되면
                            Log.d(TAG, "User re-authenticated.");

                            // 데이터 베이스에 등록되어 있는 이메일이 아닌지 확인
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if(task.isSuccessful()) {
                                        try {
                                            // 이미 등록된 이메일이 있다면 (결과 값의 크기가 1이라면) Toast Message
                                            if (task.getResult().getProviders().size() == 1) {
                                                Log.d(TAG, "onComplete: that email is already in use");
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();

                                            } else { // 등록된 이메일이 아니라면 사용자의 이메일 데이터를 업데이트

                                                Log.d(TAG, "onComplete: That email is available");
                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "onComplete: User mail address updated");
                                                                    Toast.makeText(getActivity(), "email updated", Toast.LENGTH_SHORT).show();
                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }
                                        } catch(NullPointerException e) {
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );

                                        }
                                    }
                                }
                            });

                        } else { // 재가입이 허락되지 않았을 경우
                            Log.d(TAG, "onComplete: re-authentication failed");
                        }
                    }
                });
    }

    private static final String TAG = "EditProfileFragment";

    // Widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    // Variables
    private UserSettings mUserSettings;
    private User mUser;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mDisplayName = (EditText) view.findViewById(R.id.display_name);
        mUsername = (EditText) view.findViewById(R.id.username);
        mWebsite = (EditText) view.findViewById(R.id.website);
        mDescription = (EditText) view.findViewById(R.id.description);
        mEmail = (EditText) view.findViewById(R.id.email);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);
        mFirebaseMethods = new FirebaseMethods(getActivity());

        // setProfileImage();
        setupFirebaseAuth();

        // 뒤로가기 버튼 설정 to ProfileActivity
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigation back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = (ImageView) view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to sve changes");
                saveProfileSettings();
            }
        });
        return view;
    }

    /**
     * 사용자가 변경한 정보들을 데이터 베이스로 넘김
     * 넘기는 작업을 하기 전에 username값이 유일한 값인지 확인
     */
    private void saveProfileSettings() {
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());

        // 사용자가 username값을 바꾼 경우
        if (!mUserSettings.getUser().getUsername().equals(username)) {
            // 변경 값이 데이터 베이스상에서 유일한지 확인
            checkIfUsernameExists(username);
        }

        // 사용자가 이메일 값을 바꾼 경우 -> 데이터 베이스의 이메일 값과 입력한 이메일 값이 다른 경우
        if (!mUserSettings.getUser().getEmail().equals(email)) {
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            // 다이얼로그 show, R.string.confirm_password_dialog 문자열을 태그로 갖는 프래그먼트를 보여줌
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            // 현재 프래그먼트인 EditProfileFragment에게 다이얼로그에 입력한 결과값을 넘겨주도록 설정
            dialog.setTargetFragment(EditProfileFragment.this, 1);
        }

        // 이메일과 사용자 이름을 제외한 정보의 변경값 업데이트
        if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)) {
            // update displaname
            mFirebaseMethods.updateUserAccountSettings(displayName, null, null, 0);
        }

        if(!mUserSettings.getSettings().getWebsite().equals(website)) {
            // update website
            mFirebaseMethods.updateUserAccountSettings(null, website, null, 0);
        }

        if(!mUserSettings.getSettings().getDescription().equals(description)) {
            // update description
            mFirebaseMethods.updateUserAccountSettings(null, null, description, 0);
        }

        if(!(mUserSettings.getUser().getPhone_number() == phoneNumber)) {
            // update phoneNumber
            mFirebaseMethods.updateUserAccountSettings(null, null, null, phoneNumber);

        }
    }

    /**
     * 데이터 베이스의 사용자의 username이 존재하는지 확인
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if " + username + " already exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        // 사용자가 설정한 username과 같은 값을 쿼리로 가져옴
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username)) // username을 사전순으로 정렬
                .equalTo(username);

        // addListenerForSingleValueEvent는 한번만 실해되는 이벤트
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // 실핸한 쿼리로 부터 값이 존재하지 않으면(username이 중복이 되지 않는다면) 데이터 베이스로 업데이트
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "saved username", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "onDataChange: Found a natch" + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * 매개변수로 받은 UserSettings에 저장된 사용자 데이터들을 위젯에 set
     */
    private void setProfileWidget(UserSettings userSettings) {
        // Log.d(TAG, "setProfileWidget: setting widgets with data retrieving from firebase database: " + userSettings.toString());

        mUserSettings = userSettings;
        // User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 268435456
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

    }


    /*
    ---------------------------------------firebase-------------------------------------------------
     */

    /**
     * 사용자 인증 관련 set
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // database에서 사용자 데이터를 담은 UserSettings 클래스를 반환받아서
                // setProfileWidget()에 넘김
                setProfileWidget(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * 사용자 상태 리스너를 생성
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * onStop 생명주기에서 사용자 상태 리스너가 존재하면 해제사용자 상태 리스너 해제
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
