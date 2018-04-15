package moon.the.on.junburg.com.androidinstagramclone.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import moon.the.on.junburg.com.androidinstagramclone.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Junburg on 2018. 3. 28..
 * 사용자가 이메일 주소를 변경할 시에 띄우게 될 DialogFragment
 */

public class ConfirmPasswordDialog extends DialogFragment {

    private static final String TAG = "ConfirmPasswordDialog";

    TextView mPassword;

    // Interface를 만들어서 EditProfileFragment에서 구현과 사용
    public interface OnConfirmPasswordListener {
        void onConfirmPassword(String password);
    }

    OnConfirmPasswordListener mOnConfirmPasswordListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        Log.d(TAG, "onCreateView: started");

        mPassword = (TextView)view.findViewById(R.id.confirm_password);

        // 취소를 눌렀을 경우
        TextView cancelDialog = (TextView)view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the dialog");

                // 다이얼로그 dismiss
                getDialog().dismiss();
            }
        });

        // 확인을 눌렀을 경우
        TextView confirmDialog = (TextView)view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: captured password and confirming.");
                String password = mPassword.getText().toString();

                // 사용자가 입력한 비밀번호가 공백이 아니라면 EditProfileFragment에서 구현한
                // Interface에 비밀번호 값을 넘겨주고 DialogFragment 종료
                if(!password.equals("")) {
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }
                // 비밀번호가 공백이라면 토스트 메세지 띄움
                else {
                    Toast.makeText(getActivity(), "you must enter a password", Toast.LENGTH_SHORT).show();
                }


            }
        });
        return view;
    }

    /**
     * DialogFragment가 Attach되는 때에 EditProfileFragment에서 구현한 OnConfirmPasswordListener를 사용
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnConfirmPasswordListener = (OnConfirmPasswordListener)getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }
}
