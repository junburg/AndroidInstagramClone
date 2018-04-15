package moon.the.on.junburg.com.androidinstagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import moon.the.on.junburg.com.androidinstagramclone.Home.HomeActivity;
import moon.the.on.junburg.com.androidinstagramclone.Profile.AccountSettingsActivity;
import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.models.Photo;
import moon.the.on.junburg.com.androidinstagramclone.models.User;
import moon.the.on.junburg.com.androidinstagramclone.models.UserAccountSettings;
import moon.the.on.junburg.com.androidinstagramclone.models.UserSettings;

/**
 * Created by Junburg on 2018. 3. 27..
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    // Variable
    private Context mContext;
    private String userID;
    private double mPhotoUploadProgress = 0;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    /**
     * FirebaseMethod 생성자 초기화
     */
    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        // 사용자가 로그인 중이라면 uid get
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        } else {

        }
    }

    /**
     * 사용자 포스트, 프로필 이미지 업로드
     */
    public void uploadNewPhoto(String photoType, final String caption, int imageCount, final String imgUrl, Bitmap bm) {
        Log.d(TAG, "uploadNewPhoto: attempting to upload new photo");

        FilePaths filePaths = new FilePaths();

        // 새로운 이미지를 올릴 경우 (photoType 변수로 구분)
        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading new photo");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 사진이 저장될 Firebase Storage 경로
            // FIREBASE_IMAGE_STORAGE = "photos/users"
            // 최종 Storage 경로 photos/users/uid/(file name: photo + imageCount)
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (imageCount + 1));

            // 이미지url을 비트맵 형식으로 반환
            if(bm == null) {
                bm = ImageManager.getBitmap(imgUrl);
            }
            // 비트맵 이미지를 바이트 배열로 반환
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            // UploadTask로 바이트 배열을 Storage로 put
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            // UploadTask 수행이 성공했을 경우
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Firebase storage에 저장된 이미지의 url을 반환
                    Uri FirebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                    // Firebase Database로 사진 업로드(Not storage)
                    addPhotoToDatabase(caption, FirebaseUrl.toString());

                    // Database로 사진 업로드 후 Main Feed(HomeActivity)로 이동
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                }

                // 사진 업로드 작업이 실패했을 경우
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure:  Photo upload failed");
                    Toast.makeText(mContext, "Photo upload failed", Toast.LENGTH_SHORT).show();
                }

                // 데이터 전송 중 주기적으로 호출. 업로드 및 다운로드시에 진행상 표시에 이용할 수 있음
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    // 포스트(이미지) 업로드 과정 토스트 메세지로 표시
                    // (100.0 * 전송된 바이트 수) / 총 바이트 수 = 0~100 까지의 수로 업로드 진행률 표시
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    // 최소 15%의 간격을 두고 업로드 진행률 표시
                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress), Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;

                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }

        // 사용자 프로필 사진을 업로드 하는 경우
        else if (photoType.equals(mContext.getString(R.string.profile_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading new profile photo");
            Log.d(TAG, "uploadNewPhoto: uploading new photo");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 사진이 저장될 Firebase Storage 경로
            // FIREBASE_IMAGE_STORAGE = "photos/users"
            // 최종 Storage 경로 photos/users/uid/(file name: profile_photo)
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            // 비트맵으로 이미지 변환
            if(bm == null) {
                bm = ImageManager.getBitmap(imgUrl);
            }

            // 비트맵 이미지를 바이트 배열로 반환
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            // UploadTask로 바이트 배열을 Storage로 put
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            // UploadTask 수행이 성공했을 경우
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Firebase storage에 저장된 이미지의 url을 반환
                    Uri FirebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    // user_account_settings 노드로 프로필 사진 경로를 Database로 업로드
                    setProfilePhoto(FirebaseUrl.toString());

                    // 프로필 이미지 수정을 마치면 EditProfileFragment를 ViewPager에 set
                    ((AccountSettingsActivity)mContext).setViewPager(
                            ((AccountSettingsActivity)mContext).pagerAdapter
                                    .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure:  Photo upload failed");
                    Toast.makeText(mContext, "Photo upload failed", Toast.LENGTH_SHORT).show();
                }
                // part 55
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    // 포스트(이미지) 업로드 과정 토스트 메세지로 표시
                    // (100.0 * 전송된 바이트 수) / 총 바이트 수 = 0~100 까지의 수로 업로드 진행률 표시
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    // 최소 15%의 간격을 두고 업로드 진행률 표시
                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress), Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;

                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }

    }

    /**
     * 사용자 프로필 사진 set
     */
    private void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    /**
     * 서울의 현재 시각을 get 반환
     */
    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(new Date());
    }

    /**
     * 데이터 베이스에 사진 추가
     */
    private void addPhotoToDatabase(String caption, String url) {
        Log.d(TAG, "addPhotoToDatabase: adding photo to databse.");

        // caption에서 해시태그 문자를 분류
        String tags = StringManipulation.getTags(caption);

        // 업로할 이미지에 대한 고유 키를 생성해서 반환
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();

        // Firebase photos 노드에 업로드할 모델 클래스 Photo 인스턴스 생성
        Photo photo = new Photo();

        // photo 모델에 데이터 set
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        // user_photos(사용자별 업로드한 이미지 집합 테이블) 노드에 photo 데이터 업로드
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(mAuth.getCurrentUser().getUid()).child(newPhotoKey).setValue(photo);

        // 모든 사진을 업로드하는 photos 노드에 데이터 업로드, 생성한 고유키 사용
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }

    /**
     * 사용자의 이미지 게시 수가 얼마나 되는지 수를 반환
     */
    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(userID).getChildren()) {
            count++;

        }
        return count;
    }

    /**
     * 사용자 계정 설정 노드를 업데이트
     */
    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber) {
        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if (displayName != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }

        if (website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }

        if (description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

        if (phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);

            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);

        }
    }

    /**
     * 사용자의 username값을 users와 user account settings 노드로 업데이트
     */
    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: updating username to: " + username);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    /**
     * 사용자의 이메일 값 업데이트
     */
    public void updateEmail(String email) {
        Log.d(TAG, "updateEmail: updating email to: " + email);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }

    /**
     * 이메일과 비밀번호 가입으로 새로운 사용자 등록
     */
    public void registerNewEmail(final String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            // 가입에 성공하면 인증메일을 보냄
                            sendVerificationEmail();
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }
                    }
                });
    }

    /**
     * 사용자 가입시 인증 메을 보냄
     */
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "couldn't send verification email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * users, users_account_settings node에 추가 데이터 추가
     */
    public void addNewUser(String email, String username, String description, String website, String profile_photo) {
        User user = new User(userID, 1, email, StringManipulation.condeseUsername(username));
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                StringManipulation.condeseUsername(username),
                website,
                userID
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);

    }

    /**
     * database에서 users 와 user account settings 노드의 값들을 가져와서 UserSettings 넣고 반환
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserAccountSettings:  retrieving user account settings from firebase");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            // user account settings 노드의 데이터를  get
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds.child(userID));

                try {
                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );
                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );
                    settings.setWebsite(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite()
                    );
                    settings.setDescription(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()
                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );
                    settings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()
                    );
                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );
                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information: " + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }
            }
            // users 노트의 데이터들을 get
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: " + ds);
                user.setUsername(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUsername()
                );
                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setPhone_number(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPhone_number()
                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );
                Log.d(TAG, "getUser: retrieved user information: " + user.toString());
            }
        }
        // UserSettings에 넣어서 반환
        return new UserSettings(user, settings);
    }
}