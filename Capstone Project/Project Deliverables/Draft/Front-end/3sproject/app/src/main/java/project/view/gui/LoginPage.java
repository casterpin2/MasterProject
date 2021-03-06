package project.view.gui;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;

import project.objects.User;
import project.retrofit.APIService;
import project.retrofit.ApiUtils;
import project.view.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import project.view.model.Store;
import project.view.model.Login;
import project.view.util.CustomInterface;
import project.view.util.MD5Library;
import project.view.util.Regex;
import retrofit2.Call;
import retrofit2.Response;

public class LoginPage extends AppCompatActivity {

    private ScrollView scroll;
    private Button loginBtn, loginFBBtn, loginGPBtn;
    private TextView toForgetPasswordPage, toRegisterPage, errorMessage,errorMessUserName, usernameValue, passwordValue ;
    private APIService mAPI;
    public static Login login = new Login();
    private CallbackManager callbackManager;
    private ProgressBar loadingBar;
    private RelativeLayout  main_layout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        findView();
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        configGoogleLogin();
        mAPI = ApiUtils.getAPIService();
        callbackManager = CallbackManager.Factory.create();
        getSupportActionBar().setTitle(getResources().getString(R.string.login_page_login3SBtn));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorApplication)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CustomInterface.setStatusBarColor(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        main_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CustomInterface.hideKeyboard(view,getBaseContext());
                return false;
            }
        });
        CustomInterface.setStatusBarColor(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);


        toRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegisterPage = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(toRegisterPage);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Regex regex = new Regex();
                boolean isName = regex.checkPass(errorMessage,passwordValue);
                boolean isPass = regex.checkUserName(errorMessUserName,usernameValue);
                if(isName && isPass){
                    Call<Login> call = mAPI.login(usernameValue.getText().toString(),MD5Library.md5(passwordValue.getText().toString()));
                    new LoginTo3S().execute(call);
                }
            }
        });


        loginFBBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginPage.this, Arrays.asList("public_profile"));
            }
        });

        toForgetPasswordPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toForgetPasswordPageLayout = new Intent(getBaseContext(), GetPasswordPage.class);
                startActivity(toForgetPasswordPageLayout);
            }
        });

        //Facebook Login/////////////////////////////////////////////////////////////
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final User user = new User();
                final Profile profile = Profile.getCurrentProfile();
                if (profile.getProfilePictureUri(100,100).getPath() != null)
                    user.setImage_path("http://graph.facebook.com" + profile.getProfilePictureUri(100,100).getPath());
                else {user.setImage_path("");}
                user.setFirst_name(profile.getFirstName());
                user.setLast_name(profile.getLastName());
                try {
                    Log.d("facebookId",profile.getId()+"");
                    Call<Login> call = mAPI.loginFB(user, profile.getId());
                    new LoginTo3S().execute(call);
                } catch(Exception e){

                }
            }

            @Override
            public void onCancel() {

                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                        LoginManager.getInstance().logInWithReadPermissions(LoginPage.this, Arrays.asList("public_profile"));
                        return;
                    }
                }
                errorMessage.setText("Không có kết nối. Vui lòng thử lại");
            }
        });
        //Facebook Login///////////////////////////////////////////////////////////////////
        usernameValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                errorMessage.setText("");
            }
        });
        passwordValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                errorMessage.setText("");
            }
        });


    }

    private void findView(){
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginFBBtn = (Button) findViewById(R.id.loginFBBtn);
        loginGPBtn = (Button) findViewById(R.id.loginGPBtn);
        scroll = (ScrollView) findViewById(R.id.scrollView);
        toRegisterPage = (TextView) findViewById(R.id.toRegisterPageBtn);
        errorMessage = (TextView) findViewById(R.id.errorMessage);
        usernameValue = (TextView) findViewById(R.id.usernameValue);
        passwordValue = (TextView) findViewById(R.id.passwordValue);
        String username = getIntent().getStringExtra("username");
        usernameValue.setText(username);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        toForgetPasswordPage = findViewById(R.id.toForgetPasswordPageBtn);
        main_layout = findViewById(R.id.main_layout);
        errorMessUserName = findViewById(R.id.errorMessageUsername);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                final User user = new User();
                user.setLast_name(account.getDisplayName());
                user.setImage_path("https://lh5.googleusercontent.com"+account.getPhotoUrl().getPath());
                user.setEmail(account.getEmail());
                Log.e("user",new Gson().toJson(user));
                Log.e("id",account.getId()+"");
                Call<Login> call = mAPI.loginGoogle(user, account.getId());
                new LoginTo3S().execute(call);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

            }
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /////////////////////////////////////////////////////API////////////////////////////////////////////////////////////////
    private class LoginTo3S extends AsyncTask<Call, Void, Login> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingBar.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);
            loginFBBtn.setEnabled(false);
            loginGPBtn.setEnabled(false);
            loginBtn.setText("");
        }

        @Override
        protected void onPostExecute(Login result) {
            super.onPostExecute(result);
            if (result == null){
                errorMessage.setText("Không có kết nối. Vui lòng thử lại");
                loadingBar.setVisibility(View.INVISIBLE);
                loginBtn.setEnabled(true);
                loginFBBtn.setEnabled(true);
                loginGPBtn.setEnabled(true);
                loginBtn.setText("Đăng nhập");
                return;
            } errorMessage.setText("");
            if (result.getUser().getId() == 0 && result.getUser().getLast_name() == null) {
                errorMessage.setText("Tên tài khoản hoặc mật khẩu không đúng. Vui lòng đăng nhập lại");
                loadingBar.setVisibility(View.INVISIBLE);
                loginBtn.setEnabled(true);
                loginFBBtn.setEnabled(true);
                loginGPBtn.setEnabled(true);
                loginBtn.setText("Đăng nhập");
            }else {
                //Toast.makeText(LoginPage.this, LoginPage.login.getUser().toString(), Toast.LENGTH_LONG).show();
                User user = result.getUser();
                Store store = result.getStore();
                savingPreferences(user,store);
                final Intent toHomePage = new Intent(LoginPage.this, HomePage.class);
                Bundle bundle = new Bundle();
                bundle.putString("user",new Gson().toJson(user));
                bundle.putString("store",new Gson().toJson(store));
                toHomePage.putExtras(bundle);
                //Glide.get(LoginPage.this).clearDiskCache();
                final String android_id = Secure.getString(LoginPage.this.getContentResolver(),
                        Secure.ANDROID_ID);
                myRef = database.getReference().child("authentication").child(String.valueOf(user.getId())).child("device_id");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (!dataSnapshot.getValue(String.class).equalsIgnoreCase(android_id)) {
                                myRef.setValue(android_id);
                            }
                            loadingBar.setVisibility(View.INVISIBLE);
                            loginBtn.setEnabled(true);
                            loginFBBtn.setEnabled(true);
                            loginGPBtn.setEnabled(true);
                            loginBtn.setText("Đăng nhập");
                            startActivity(toHomePage);
                            finishAffinity();
                            finish();
                        } else{
                            myRef.setValue(android_id);
                            loadingBar.setVisibility(View.INVISIBLE);
                            loginBtn.setEnabled(true);
                            loginFBBtn.setEnabled(true);
                            loginGPBtn.setEnabled(true);
                            loginBtn.setText("Đăng nhập");
                            startActivity(toHomePage);
                            finishAffinity();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Login doInBackground(Call... calls) {
            Login result = null;
            try {
                Call<Login> call = calls[0];
                Response<Login> response = call.execute();
                result = response.body();
                //Glide.get(LoginPage.this).clearDiskCache();
            } catch (IOException e) {
            }

            return result;
        }
    }

    private void savingPreferences(User user , Store store){
            SharedPreferences pre = getSharedPreferences("authentication", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pre.edit();
            editor.putString("user", new Gson().toJson(user));
            editor.putString("store", new Gson().toJson(store));
            editor.commit();
    }

    private void configGoogleLogin() {
        // Configure sign-in to request the user_profile's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginGPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
