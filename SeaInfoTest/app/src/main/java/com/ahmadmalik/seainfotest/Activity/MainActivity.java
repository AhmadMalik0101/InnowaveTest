package com.ahmadmalik.seainfotest.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmadmalik.seainfotest.DataModel.Item;
import com.ahmadmalik.seainfotest.DataModel.UserResponse;
import com.ahmadmalik.seainfotest.R;
import com.ahmadmalik.seainfotest.Utills.ApiClient;
import com.ahmadmalik.seainfotest.Utills.ApiInterface;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_email;
    Button search_btn;
    String s_email = "", emailPattern, email;

    ApiInterface apiService;
    private ProgressDialog dialog;

    final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, MULTIPLE_PERMISSIONS);
        }

        setView();

    }

    private void setView() {
        et_email = (EditText) findViewById(R.id.et_email);
        search_btn = (Button) findViewById(R.id.search_btn);

        email = et_email.getText().toString().trim();
        emailPattern = "[a-zA-Z0-9._-]+@[a-z-_.]+\\.+[a-z]+";
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().matches(emailPattern) && s.length() > 0) {
                    s_email = s.toString();
                } else {
                    et_email.requestFocus();
                    et_email.setError("Enter valid email");
                    s_email = "";
                }

                if (s.toString().equals("")) {
                    et_email.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        search_btn.setOnClickListener(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);

    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.search_btn:

                if (validSearch()) {

                    if (isNetworkAvailable()) {

                        hideSoftKeyboard(this);
                        dialog = new ProgressDialog(this);
                        dialog.setMessage("Please Wait...");
                        dialog.show();

                        Call<UserResponse> call = apiService.getEmailResponse(s_email);

                        call.enqueue(new Callback<UserResponse>() {
                            @Override
                            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                                int count = response.body().getTotalCount();
                                boolean incomplete_result = response.body().getIncompleteResults();

                                List<Item> userList = response.body().getItems();
                                dialog.dismiss();

                                Intent userIntent = new Intent(MainActivity.this , UserDetailActivity.class);
                                userIntent.putExtra("USER_LIST", (Serializable) userList);
                                startActivity(userIntent);

                            }

                            @Override
                            public void onFailure(Call<UserResponse> call, Throwable t) {

                                //Log error here since request failed
                                Log.e("Main_Activity", t.toString());
                                dialog.dismiss();

                            }
                        });
                    } else {

                        Toast.makeText(MainActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    }


                }
                break;
        }
    }

    private boolean validSearch() {
        s_email = et_email.getText().toString();

        if (s_email == null || s_email.equals("")) {
            et_email.requestFocus();
            et_email.setError("Please enter email first");
            return false;
        }

        return true;
    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.

                } else {
                    // no permissions granted.
                }
                return;
            }
        }
    }


}
