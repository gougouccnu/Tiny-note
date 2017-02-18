package com.ggccnu.tinynote.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ggccnu.tinynote.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends Activity implements OnClickListener{

    EditText et_account,et_pwd;

    Button btn_login,btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        BmobUser user = BmobUser.getCurrentUser(this);
        if(user!=null){
            Intent intent = new Intent(LoginActivity.this, YearActivity.class);
            startActivity(intent);
        }
    }


    public void initView(){
        et_account = (EditText)findViewById(R.id.et_account);
        et_pwd = (EditText)findViewById(R.id.et_pwd);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register = (Button)findViewById(R.id.btn_register);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    String account,pwd;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_login://登陆
                account = et_account.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                if(account.equals("")){
                    toast("填写你的用户名");
                    return;
                }

                if(pwd.equals("")){
                    toast("填写你的密码");
                    return;
                }

                BmobUser user = new BmobUser();
                user.setUsername(account);
                user.setPassword(pwd);
                user.login(this, new SaveListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(LoginActivity.this, YearActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        toast("登陆失败："+arg1);
                    }
                });
                break;

            case R.id.btn_register://注册
                account= et_account.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                if(account.equals("")){
                    toast("填写你的用户名");
                    return;
                }
                if(pwd.equals("")){
                    toast("填写你的密码");
                    return;
                }
                BmobUser u = new BmobUser();
                u.setUsername(account);
                u.setPassword(pwd);
                u.setEmail("78006456@qq.com");
                u.signUp(this, new SaveListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(LoginActivity.this, YearActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        toast("注册失败："+arg1);
                    }
                });
                break;
            default:
                break;
        }
    }

    private void toast(String msg){
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}

