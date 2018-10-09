package com.example.aliyildiz.linkedinsdk;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    String first_name, last_name, email, pictureURL;
    TextView nameText, emailText;
    ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameText = findViewById(R.id.name);
        emailText = findViewById(R.id.email);
        profilePicture = findViewById(R.id.profilePicture);
        handleLogin();
    }

    private void handleLogin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(),
                new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                fetchPersonalInfo();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.e("Failed",error.toString());
            }
        }, true);
    }
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,
                requestCode, resultCode, data);
    }

    private void fetchPersonalInfo(){

        final String url  = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name," +
                "public-profile-url,picture-url,email-address,picture-urls::(originals))";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                JSONObject jsonobject = apiResponse.getResponseDataAsJson();
                try {
                    first_name = jsonobject.getString("firstName");
                    last_name = jsonobject.getString("lastName");
                    email = jsonobject.getString("emailAddress");
                    pictureURL = jsonobject.getString("pictureUrl");
                    nameText.setText(first_name+last_name);
                    emailText.setText(email);
                    Picasso.with(getApplicationContext()).load(pictureURL).into(profilePicture);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onApiError(LIApiError LIApiError) {

                Log.e("Failed to Login",LIApiError.getMessage());
            }
        });
    }

}
