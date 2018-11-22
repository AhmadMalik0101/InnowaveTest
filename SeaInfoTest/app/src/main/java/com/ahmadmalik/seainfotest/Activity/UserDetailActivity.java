package com.ahmadmalik.seainfotest.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmadmalik.seainfotest.DataModel.Item;
import com.ahmadmalik.seainfotest.R;
import com.ahmadmalik.seainfotest.Utills.ApiClient;
import com.ahmadmalik.seainfotest.Utills.ApiInterface;
import com.ahmadmalik.seainfotest.Utills.ImageLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends Activity {

    TextView tv_login_name,tv_id,tv_type;
    ArrayList<Item> userList;
    ImageView iv_image,iv_image2,iv_back;

    ApiInterface apiService;

    ImageLoader imageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        Intent intent = getIntent();
        userList = (ArrayList<Item>) intent.getSerializableExtra("USER_LIST");
        imageLoader = new ImageLoader(this);


        setView();

        String imageUrl = "https://www.gstatic.com/webp/gallery3/1.png";
        imageLoader.DisplayImage(imageUrl,iv_image2);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.downloadImage("https://www.gstatic.com/webp/gallery3/1.png");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // display the image data in a ImageView or save it
                        Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                        iv_image.setImageBitmap(bm);
                    } else {
                        // TODO
                    }
                } else {
                    // TODO
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });



    }

    private void setView()
    {

        tv_login_name = (TextView) findViewById(R.id.tv_login_name);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_type = (TextView) findViewById(R.id.tv_type);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_image2 = (ImageView) findViewById(R.id.iv_image2);
        iv_back = (ImageView) findViewById(R.id.iv_back);


        if(userList!=null)
        {
            if(userList.size()>0)
            {
                tv_login_name.setText(userList.get(0).getLogin());
                tv_id.setText(userList.get(0).getId().toString());
                tv_type.setText(userList.get(0).getType().toString());


            }
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }
}
