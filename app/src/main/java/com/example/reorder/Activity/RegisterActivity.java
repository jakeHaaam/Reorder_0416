package com.example.reorder.Activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reorder.Result.IdCheckResult;
import com.example.reorder.Result.JoinResult;
import com.example.reorder.R;
import com.example.reorder.api.IdCheckApi;
import com.example.reorder.api.JoinApi;
import com.example.reorder.globalVariables.serverURL;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    Button bt_sign_up_ok;
    Button bt_sign_up_id_check;
    EditText et_sign_up_id;
    EditText et_sign_up_password;
    EditText et_sign_up_password_check;
    String url= serverURL.getUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bt_sign_up_ok=(Button)findViewById(R.id.bt_sign_up_ok);
        et_sign_up_id=(EditText)findViewById(R.id.et_sign_up_id);
        et_sign_up_password=(EditText)findViewById(R.id.et_sign_up_password);
        et_sign_up_password_check=(EditText)findViewById(R.id.et_sign_up_password_check);
        bt_sign_up_id_check=(Button)findViewById(R.id.bt_sign_up_id_check);
        
        //아이디 중복 체크
        bt_sign_up_id_check.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String client_id= et_sign_up_id.getText().toString();

                if(et_sign_up_id.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"ID를 입력해 주세요.",Toast.LENGTH_SHORT).show();
                    bt_sign_up_ok.setClickable(false);
                }

                else
                    try {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        IdCheckApi idCheckAPI = retrofit.create(IdCheckApi.class);
                        idCheckAPI.getClient_id(client_id).enqueue(new Callback<IdCheckResult>() {
                            @Override
                            public void onResponse(Call<IdCheckResult> call, Response<IdCheckResult> response) {
                                Log.d("12321",response.message()+" "+response.toString());
                                //JsonObject post= new JsonObject().get(response.body().toString()).getAsJsonObject();
                                if (response.isSuccessful()) {
                                    IdCheckResult idCheckResult = response.body();
                                    switch (idCheckResult.getResult()) {
                                        case 0:
                                            Toast.makeText(getApplicationContext(), "이미 가입된 ID입니다.", Toast.LENGTH_SHORT).show();
                                            bt_sign_up_ok.setClickable(false);
                                            bt_sign_up_ok.setTextColor(Color.GRAY);
                                            break;
                                        case 1:
                                            Toast.makeText(getApplicationContext(), "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                                            bt_sign_up_ok.setClickable(true);
                                            bt_sign_up_ok.setTextColor(Color.WHITE);
                                            break;
                                        default:
                                            Toast.makeText(getApplicationContext(), "11111111 is NOT Successful", Toast.LENGTH_SHORT).show();
                                    }
                                } else
                                    Toast.makeText(getApplicationContext(), "response is NOT Successful", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<IdCheckResult> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "This is fail", Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });

        //비밀번호 체크
        bt_sign_up_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_sign_up_password.getText().toString().equals("")||et_sign_up_password_check.getText().toString().equals("")
                || et_sign_up_id.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"모든 항목을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
                if(!et_sign_up_password.getText().toString().equals(et_sign_up_password_check.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"비밀번호가 다릅니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        HashMap<String, String> input = new HashMap<>();
                        input.put("client_id", et_sign_up_id.getText().toString());
                        input.put("client_password", et_sign_up_password.getText().toString());
                        input.put("client_password2", et_sign_up_password_check.getText().toString());


                        Retrofit retrofit = new Retrofit.Builder().baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create()).build();

                        JoinApi joinApi = retrofit.create(JoinApi.class);

                        joinApi.postJoinUserInfo(input).enqueue(new Callback<JoinResult>() {
                            @Override
                            public void onResponse(Call<JoinResult> call, Response<JoinResult> response) {
                                if (response.isSuccessful()) {
                                    JoinResult map = response.body();
                                    if (map != null) {
                                        switch (map.getResult()) {
                                            case 1:
                                                Toast.makeText(RegisterActivity.this, "회원 가입이 되었습니다~!", Toast.LENGTH_SHORT).show();
                                                finish();
                                                break;
                                            case 0:
                                                Toast.makeText(RegisterActivity.this, "존재하는 아이디 입니다...", Toast.LENGTH_SHORT).show();
                                                break;

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JoinResult> call, Throwable t) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }            }
        });
    }
}
