package in.andonsystem.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import in.andonsystem.App;
import in.andonsystem.AppClose;
import in.andonsystem.Constants;
import in.andonsystem.LoginActivity;
import in.andonsystem.R;
import in.andonsystem.entity.User;
import in.andonsystem.service.UserService;
import in.andonsystem.util.ErrorListener;
import in.andonsystem.util.RestUtility;
import in.andonsystem.view.LetterImageView;

public class ProfileActivity extends AppCompatActivity {

    private final String TAG = ProfileActivity.class.getSimpleName();

    private LetterImageView pImage;
    private TextView username;
    private TextView level;
    private TextView email;
    private LinearLayout layoutMobile;
    private TextView mobileTextView;
    private EditText mobileEditText;
    private ImageView mobileIcon;
    private ImageView mobileEdit;
    private ImageView mobileSave;
    private LinearLayout layoutPasswd;
    private EditText currPasswd;
    private EditText newPasswd;
    private EditText confirmNewPasswd;
    private Button changePasswdBtn;
    private Button savePasswdBtn;

    private SharedPreferences userPref;
    private Context mContext;
    private App app;
    private UserService userService;
    private User user;
    private RestUtility restUtility;
    private ErrorListener errorListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(getApplication(), "39a8187d");
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppClose.activity4 = this;
        mContext = this;
        app = (App)getApplication();
        userService = new UserService(app);
        userPref = getSharedPreferences(Constants.USER_PREF,0);

        pImage = (LetterImageView)findViewById(R.id.profile_letter_image);
        username = (TextView) findViewById(R.id.profile_username);
        level = (TextView) findViewById(R.id.profile_level);
        email = (TextView) findViewById(R.id.profile_email);
        layoutMobile = (LinearLayout) findViewById(R.id.profile_mobile_layout);
        mobileTextView = createTextView();
        mobileEditText = createEditText();
        layoutPasswd = (LinearLayout) findViewById(R.id.profile_passwd_layout);
        currPasswd = createEditText();
        newPasswd = createEditText();
        confirmNewPasswd = createEditText();
        changePasswdBtn = createButton("Change Password");
        savePasswdBtn = createButton("Save");

        mobileIcon = createImageView();
        mobileEdit = createImageView();
        mobileSave = createImageView();
        mobileIcon.setBackgroundResource(R.drawable.ic_phone_android_white_24dp);
        mobileEdit.setBackgroundResource(R.drawable.ic_edit_white_24dp);
        mobileSave.setBackgroundResource(R.drawable.ic_save_white_24dp);

        mobileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMobileSave();
            }
        });

        mobileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMobile();
            }
        });

        changePasswdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordEdit();
            }
        });

        savePasswdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        String emailId = userPref.getString(Constants.USER_EMAIL,null);
        Log.i(TAG, "email: " + emailId);
        if (emailId != null) {
            user = userService.findByEmail(emailId);
        }

        errorListener = new ErrorListener(mContext) {
            @Override
            protected void handleTokenExpiry() {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        };
        errorListener = new ErrorListener(this) {
            @Override
            protected void handleTokenExpiry() {
                //onStart();
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        };
        restUtility = new RestUtility(this){
            @Override
            protected void handleInternetConnRetry() {
                onStart();
            }

            @Override
            protected void handleInternetConnExit() {
                AppClose.close();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        String uName = user.getName();
        pImage.setOval(true);
        pImage.setLetter(uName.charAt(0));
        username.setText(uName);
        level.setText(user.getLevel());
        email.setText(user.getEmail());

        showMobileEdit();
        showPasswordBtn();

    }

    private void changeMobile(){
        String mobile = mobileEditText.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            showMessage("Enter new mobile number.");
            return;
        }else if (mobile.trim().length() != 10) {
            showMessage("Incorrect mobile number");
            return;
        }
        final String url = Constants.API2_BASE_URL + "/users/" + user.getId();
        Log.i(TAG, "url = " + url);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "mobile change Response :" + response.toString());
                try {
                    String mobile = response.getString("mobile");
                    user.setMobile(mobile);
                    userService.saveOrUpdate(user);
                    showMessage("mobile number updated successfully.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showMobileEdit();

            }
        };

        JSONObject reqData = new JSONObject();
        try {
            reqData.put("mobile",mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        restUtility.patch(url,reqData,listener,errorListener);
    }

    private void changePassword(){
        String currPass = currPasswd.getText().toString();
        String newPass = newPasswd.getText().toString();
        String confirmNewPass = confirmNewPasswd.getText().toString();
        if (TextUtils.isEmpty(currPass)) {
            showMessage("Enter Current password.");
        }
        else if (TextUtils.isEmpty(newPass)) {
            showMessage("Enter new password.");
        }
        else if (TextUtils.isEmpty(confirmNewPass)) {
            showMessage("Confirm new Password");
        }
        else if (!newPass.trim().equals(confirmNewPass.trim())) {
            showMessage("Passwords do not match.");
        }else {
            final String url = Constants.API2_BASE_URL + "/misc/change_password?email=" + user.getEmail() + "&oldPassword=" + currPass + "&newPassword=" + newPass;
            Log.i(TAG, "url = " + url);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "password change Response :" + response.toString());
                    try {
                        String status = response.getString("status");
                        if (status.equalsIgnoreCase("SUCCESS")){
                            showMessage("password changed successfully.");
                        }else {
                            showMessage("Incorrect current password. Try again.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showPasswordBtn();

                }
            };
            restUtility.put(url,null,listener,errorListener);
        }

    }

    private void showMobileEdit(){
        layoutMobile.removeAllViews();

        mobileTextView.setText(user.getMobile());
        layoutMobile.addView(mobileIcon);
        layoutMobile.addView(mobileTextView);
        layoutMobile.addView(mobileEdit);
    }

    private void showMobileSave(){
        layoutMobile.removeAllViews();;

        mobileEditText.setText(user.getMobile());
        mobileEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mobileEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        layoutMobile.addView(mobileIcon);
        layoutMobile.addView(mobileEditText);
        layoutMobile.addView(mobileSave);
    }

    private void showPasswordBtn(){
        layoutPasswd.removeAllViews();
        layoutPasswd.addView(changePasswdBtn);
    }

    private void showPasswordEdit(){
        layoutPasswd.removeView(changePasswdBtn);


        currPasswd.setHint("current password");
        currPasswd.setHintTextColor(ContextCompat.getColor(mContext,R.color.white));
        currPasswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswd.setHint("new password");
        newPasswd.setHintTextColor(ContextCompat.getColor(mContext,R.color.white));
        newPasswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmNewPasswd.setHint("confirm new password");
        confirmNewPasswd.setHintTextColor(ContextCompat.getColor(mContext,R.color.white));
        confirmNewPasswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layoutPasswd.addView(currPasswd);
        layoutPasswd.addView(newPasswd);
        layoutPasswd.addView(confirmNewPasswd);
        layoutPasswd.addView(savePasswdBtn);
    }

    private TextView createTextView(){
        TextView view = new TextView(mContext);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2
        );
        param.gravity = Gravity.CENTER_VERTICAL;
        view.setLayoutParams(param);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        int pixel = convertDpToPixel(5.0f);
        view.setPadding(pixel,pixel,pixel,pixel);
        view.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        return view;
    }

    private EditText createEditText(){
        EditText view = new EditText(mContext);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2
        );
        param.gravity = Gravity.CENTER_VERTICAL;
        view.setLayoutParams(param);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        int pixel = convertDpToPixel(10.0f);
        view.setPadding(pixel,pixel,pixel,pixel);
        view.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        return view;
    }


    private ImageView createImageView(){
        ImageView view = new ImageView(mContext);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        param.rightMargin = convertDpToPixel(10.0f);
        param.gravity = Gravity.CENTER_VERTICAL;
        view.setLayoutParams(param);

        return view;
    }

    private Button createButton(String text){
        Button button = new Button(mContext);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        param.gravity = Gravity.CENTER_HORIZONTAL;
        button.setLayoutParams(param);
        button.setText(text);
        //button.setBackgroundColor(Color.parseColor("#0000"));
        int pixel = convertDpToPixel(2.0f);
        button.setPadding(5*pixel,pixel,5*pixel,pixel);
        button.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
        button.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        return button;
    }

    private int convertDpToPixel(float dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void showMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

}
