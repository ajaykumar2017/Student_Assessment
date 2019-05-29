package com.tecent.student_assessment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class CreatePostQueryDoubts extends AppCompatActivity {
    ImageView iv_cancel_post, iv_profile_image, iv_set_image;
    TextView tv_username, tv_btn_post, path_image;
    EditText et_post_text;
    SharedPreferences sharedPreferences;
    ACProgressFlower dialog;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_query);
        iv_cancel_post = findViewById(R.id.iv_cancel_post);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        tv_username = findViewById(R.id.tv_username);
        et_post_text = findViewById(R.id.et_post_text);
        tv_btn_post = findViewById(R.id.tv_btn_post);
        path_image = findViewById(R.id.path_image);
        iv_set_image=findViewById(R.id.iv_set_image);
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = this.getSharedPreferences("studentAssessment", Context.MODE_PRIVATE);
        String userdp=sharedPreferences.getString("userdp","");
        requestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl+"userdp/"+userdp, iv_profile_image));
        String name = sharedPreferences.getString("name", "");
        String userid=sharedPreferences.getString("userid","");
        tv_username.setText(name);
        //progress dialog
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).text("Uploading....")
                .fadeColor(Color.BLACK).build();
        dialog.setCancelable(false);

        iv_cancel_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_post_text.getText().toString().trim().length() < 5) {
                    Toast.makeText(CreatePostQueryDoubts.this, "Post should contain at least 5 characters", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();
                    if (ExtraFunctions.isNetworkStatusAvialable(CreatePostQueryDoubts.this)) {
                        if (iv_set_image.getDrawable() != null)
                            volleyImageRequest();
                        else
                            volleyTestWithoutImage();
                    }
                    else {
                        dialog.dismiss();
                        Toast.makeText(CreatePostQueryDoubts.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        et_post_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_post_text.getText().toString().trim().length() >= 5) {
                    tv_btn_post.setBackgroundResource(R.color.green);
                } else if (et_post_text.getText().toString().trim().length() < 5) {
                    tv_btn_post.setBackgroundResource(R.color.smalldarkgrey);
                }
//                Toast.makeText(CreatePostQueryDoubts.this, String.valueOf(et_post_text.getText().toString().trim().length()), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    //image picker
    public void openImagePicker(View view) {
        CropImage.activity().setAspectRatio(1, 1)
                .start(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final String path = resultUri.getPath();
                //now we have path of file we should compress image
                iv_set_image.setImageBitmap(compressImage(path));
                String[] paths = path.split("/");
                path_image.setText(paths[paths.length-1]);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //on error
                dialog.dismiss();
                Toast.makeText(this, "An error occured! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap compressImage(String path) {
        byte[] imageData = null;
        try {
            final int THUMBNAIL_SIZE = 256;
            FileInputStream fis = new FileInputStream(new File(path));
            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE,
                    THUMBNAIL_SIZE, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();
            Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            return bmp;
        } catch (Exception ex) {
            return null;
        }
    }

    public void volleyImageRequest(){
        String url = ExtraFunctions.serverurl+"postdoubts.php";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("result");
                    if (status.equals("successful"))
                    {
                        dialog.dismiss();
                        Toast.makeText(CreatePostQueryDoubts.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    if (status.equals("error")){
                        dialog.dismiss();
                        Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(CreatePostQueryDoubts.this, "Volley Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", sharedPreferences.getString("userid",""));
                params.put("posttext", et_post_text.getText().toString().replace("'","\\'"));
                params.put("email", sharedPreferences.getString("email",""));
                params.put("name", sharedPreferences.getString("name",""));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("doubtimage", new DataPart("file_image.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), iv_set_image.getDrawable()), "image/jpeg"));
                //DataPart second parameter is byte[]
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    //Google volley
    public void volleyTestWithoutImage() {
        String url = ExtraFunctions.serverurl + "postdoubts1.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonParser(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(CreatePostQueryDoubts.this, error.toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("userid", sharedPreferences.getString("userid",""));
                MyData.put("posttext", et_post_text.getText().toString().replace("'","\\'"));
                MyData.put("email", sharedPreferences.getString("email",""));
                MyData.put("name", sharedPreferences.getString("name",""));
                return MyData;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void jsonParser(String jsontext) {
        try {
            JSONObject emp = (new JSONObject(jsontext));
            String result = emp.getString("result");
            if (result.equals("successful")) {
                dialog.dismiss();
                Toast.makeText(CreatePostQueryDoubts.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
            if (result.equals("error")) {
                dialog.dismiss();
                Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception) {
            dialog.dismiss();
            exception.printStackTrace();
        }
    }
}