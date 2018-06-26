package project.view.ChangePassword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import project.view.R;
import project.view.Register.Regex;

public class ChangePasswordActivity extends AppCompatActivity{
    private  Button btnChangePass;
    private TextView tvOldPass, tvConfirmPass,tvNewPass;
    private TextInputEditText oldPass, newPass, confirmPass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tvOldPass = findViewById(R.id.tvOldPass);
        tvNewPass = findViewById(R.id.tvNewPassword);
        tvConfirmPass = findViewById(R.id.tvConfirmPass);

        oldPass = findViewById(R.id.etOldPassword);
        newPass = findViewById(R.id.etNewPassword);
        confirmPass = findViewById(R.id.etConfirmPass);
        newPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    checkPassword(newPass.getText().toString());
                }
            }
        });

        btnChangePass = findViewById(R.id.btnChangePass);
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPassword(newPass.getText().toString(),confirmPass.getText().toString());
                if(oldPass.getText().toString().isEmpty()){
                    tvOldPass.setText(R.string.error_empty);
                }
                if(newPass.getText().toString().isEmpty()){
                    tvNewPass.setText(R.string.error_empty);
                }
                if(confirmPass.getText().toString().isEmpty()){
                    tvConfirmPass.setText(R.string.error_empty);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmPassword(String pass,String passConfirm){
       if(!pass.equals(passConfirm)){
           tvConfirmPass.setText(R.string.error_confirm_password);
       } else
       tvConfirmPass.setText("");
    }

    private void checkPassword(String input){
        Regex validate = new Regex();
        if(!input.isEmpty()&& !validate.isPassWord(input)) {
            tvNewPass.setText(R.string.error_validate_password);
        } else
            tvNewPass.setText("");
    }
}
