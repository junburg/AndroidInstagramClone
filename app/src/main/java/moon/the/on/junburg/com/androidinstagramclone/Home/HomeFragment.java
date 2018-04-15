package moon.the.on.junburg.com.androidinstagramclone.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moon.the.on.junburg.com.androidinstagramclone.R;
import moon.the.on.junburg.com.androidinstagramclone.Utils.MainfeedListAdapter;
import moon.the.on.junburg.com.androidinstagramclone.models.Comment;
import moon.the.on.junburg.com.androidinstagramclone.models.Like;
import moon.the.on.junburg.com.androidinstagramclone.models.Photo;
import moon.the.on.junburg.com.androidinstagramclone.models.UserAccountSettings;

/**
 * Created by Junburg on 2018. 3. 25..
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // Variables
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mFollowing;
    private ArrayList<Photo> mPaginatedPhotos;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;
    private int mResults;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();

        getFollowing();

        return view;
    }

    /**
     * 팔로잉 유저 get
     */
    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        // following 노드에서 사용자가 팔로잉하는 유저를 가져오는 query
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    // mFollowing 리스트에 사용자가 팔로잉 하는 유저의 uid를 add
                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }
                // mFollowing 리스트에 사용자 uid add
                mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                //get the photos
                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * user_photos 노드의 유저 포스트 이미지 정보 get
     */
    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for (int i = 0; i < mFollowing.size(); i++) {
            final int count = i;

            // 사용자가 팔로잉하는 유저의 포스트를 photo 모델로 get
            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        // user_photos의 uid 노드 하위 정보를 Map 형태로 get
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        // Photo 모델을 생성하고 가져온 데이터를 set
                        Photo photo = new Photo();
                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        // Comment 모델을 담는 리스트 생성
                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        // DataSnapshot에서 코멘트 정보를 get
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()) {
                            // Comment 모델 set
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            // 리스트에 모델 add
                            comments.add(comment);
                        }


                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    if (count >= mFollowing.size() - 1) {
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * 포스트 이미지 display
     */
    private void displayPhotos() {
        mPaginatedPhotos = new ArrayList<>();

        // mPhotos 리스트에 Photo 모델이 존재하면
        if (mPhotos != null) {
            try {
                // getDate_created(포스트 생성날짜)를 기준으로 정렬
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                }
                });

                // 리스트 사이즈가 10 보다 크면 iterations = 10
                int iterations = mPhotos.size();
                if (iterations > 10) {
                    iterations = 10;
                }

                mResults = 10;
                // 최근 10개의 포스트만 리스트에 저장
                for (int i = 0; i < iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                // mPaginatedPhotos 리스트로 어댑터를 생성하고 set
                mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            } catch (NullPointerException e) {
                Log.d(TAG, "displayPhotos: NullPointerException" + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.d(TAG, "displayPhotos: IndexOutOfBoundsException" + e.getMessage());
            }
        }
    }

    /**
     * 10개 이상의 포스트를 볼 경우
     */
    public void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try {
            if (mPhotos.size() > mResults && mPhotos.size() > 0) {
                int iterations;
                if (mPhotos.size() > (mResults + 1)) {
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;


                } else {
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - mResults;
                }

                // add the new Photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "displayPhotos: NullPointerException" + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "displayPhotos: IndexOutOfBoundsException" + e.getMessage());
        }
    }
}



