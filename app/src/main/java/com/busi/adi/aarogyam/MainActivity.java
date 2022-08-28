package com.busi.adi.aarogyam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.busi.adi.aarogyam.Model.HealthDiagnosis;
import com.busi.adi.aarogyam.Model.HealthIssueInfo;
import com.busi.adi.aarogyam.Model.HealthItem;
import com.busi.adi.aarogyam.Model.HealthSymptomSelector;
import com.busi.adi.aarogyam.Model.IssueInfo;
import com.busi.adi.aarogyam.Model.Pair;
import com.busi.adi.aarogyam.Token.FetchAccessToken;
import com.busi.adi.aarogyam.account.Account;
import com.busi.adi.aarogyam.account.DeleteAccount;
import com.busi.adi.aarogyam.account.Profile;
import com.busi.adi.aarogyam.account.callback.OnCompleteAccountActivity;
import com.busi.adi.aarogyam.health.DiagnosisFragment;
import com.busi.adi.aarogyam.listeners.OnComplete;
import com.busi.adi.aarogyam.service.LoadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnComplete, OnCompleteAccountActivity {

    private static MainActivity activity;
    private static final String VISIB = "VISIB";
    private static final String ENBLD = "ENBLD";
    private static final String P_VISIB = "P_VISIB";
    private static boolean fbtnEnbld = true;

    private static int btnVisibility = View.VISIBLE;
    private static boolean btnEnabled = true;
    public static int progresVisibility = View.GONE;

    private static final int NOTHING_LOADED = -1;
    private static final int USER_INFO_LOADED = 0;
    private static final int BODY_LOCATIONS_LOADED = 1;
    private static final int BODY_SUB_LOCATIONS_LOADED = 2;
    private static final int SUB_LOCATION_SYMPTOMS_LOADED = 3;
    private static final int DIAGNOSIS_DATA_LOADED = 4;
    private static final int ISSUE_INFO_LOADED = 5;
    private static int LAST_LOADED_DATA = NOTHING_LOADED;

    private static HealthDiagnosis HD;
    private static List<Integer> selectedSymptomsIds;

    private static LinearLayout btn;
    public static ProgressBar progressBar;
    private static DrawerLayout drawerLayout;
    private static NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FragmentManager fManager;
    private static TextView user_display_name;
    private ImageView sdi;
    private FloatingActionButton fbtn;

    private List<IssueInfo> issueInfoSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.startDiagnosis);
        fbtn = findViewById(R.id.add_curr_ailment);
        progressBar = findViewById(R.id.progress);
        sdi = findViewById(R.id.start_diag_img);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        sdi.setAnimation(rotate);
        if(savedInstanceState != null){
            btn.setVisibility(savedInstanceState.getInt(VISIB));
            btn.setEnabled(savedInstanceState.getBoolean(ENBLD));
            progressBar.setVisibility(savedInstanceState.getInt(P_VISIB));
        }

        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.activity_main_drawer);
        navigationView = findViewById(R.id.activity_main_navigation_view);
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        user_display_name = navigationView.getHeaderView(0).findViewById(R.id.user_display_name);
        fManager = getSupportFragmentManager();
        activity = this;
        btn.setOnClickListener(view -> {
            btn.setEnabled(false);
            if(App.accessToken == null) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null && activeNetwork.isConnected()) {
                    progresVisibility = View.VISIBLE;
                    progressBar.setVisibility(View.VISIBLE);
                    new FetchAccessToken(this, this).execute(getString(R.string.priaid_authservice_url), getString(R.string.usrname), getString(R.string.password));
                }else{
                    btn.setEnabled(true);
                    new CustomToast(MainActivity.this, "App not connected to internet").show();
                }
            }
            else{
                start();
                btn.setEnabled(true);
            }
        });
        fbtn.setOnClickListener(view -> SaveToHistory());
        mAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if(firebaseUser != null){
                toggleMenuItemVisibility(false);
                user_display_name.setText(firebaseUser.getDisplayName());
            }else{
                toggleMenuItemVisibility(true);
                user_display_name.setText(App.ANONYMOUS);
            }
        };
    }

    public void start(){
        LAST_LOADED_DATA = USER_INFO_LOADED;
        btnGone();
        loadUserInfo();
    }

    private void loadUserInfo() {
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .add(R.id.content_main, new UserInfoFragment())
                .addToBackStack(null)
                .commit();
        hideFbtn();
    }

    private void SaveToHistory() {
        if(LAST_LOADED_DATA == NOTHING_LOADED){
            AddNewMedicalAilment();
        }else{
            AddSearchedMedicalAilment();
        }
    }

    private void AddNewMedicalAilment() {
        if(mAuth != null && mAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, LocallyAddedHistory.class);
            startActivity(intent);
        }else{
            new CustomToast(MainActivity.this, "Please Login to Add").show();
        }
    }

    private void AddSearchedMedicalAilment() {
        if(mAuth != null){
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if(firebaseUser != null){
                SaveDialog();
            }else{
                new CustomToast(this, "Please Login to save").show();
            }
        }
    }

    public void SaveDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.save_name_dialog);

        TextView mTitle = (TextView) dialog.findViewById(R.id.title);
        final EditText mFileName = (EditText) dialog.findViewById(R.id.file_name);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        mTitle.setText("Enter file name");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("adf", "onClick: save");
                String file_base_name = mFileName.getText().toString();
                if (file_base_name.isEmpty())
                    new CustomToast(MainActivity.this, "File name can't be empty").show();
                else{
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                    reference.child("history").child("searched").child(file_base_name).setValue(issueInfoSave).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                new CustomToast(MainActivity.this, "Saved").show();
                            }else{
                                new CustomToast(MainActivity.this, "Error occured").show();
                            }
                        }
                    });
                    dialog.dismiss();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void btnVisible(){
        btnVisibility = View.VISIBLE;
        btnEnabled = true;
        btn.setVisibility(btnVisibility);
        btn.setEnabled(btnEnabled);
    }

    public void btnGone(){
        btnVisibility = View.GONE;
        btnEnabled = false;
        btn.setVisibility(btnVisibility);
        btn.setEnabled(btnEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                GoHome();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void GoHome(){
        if(btn.getVisibility() == View.GONE) {
            FrameLayout dPage = findViewById(R.id.content_main);
            dPage.removeAllViewsInLayout();
            LAST_LOADED_DATA = NOTHING_LOADED;
            for(int i=0; i<activity.getSupportFragmentManager().getBackStackEntryCount(); i++){
                activity.getSupportFragmentManager().popBackStack();
            }
            btnVisible();
            setTitle(R.string.app_name);
        }
        fbtn.setVisibility(View.VISIBLE);
        fbtn.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(activity.getSupportFragmentManager().getBackStackEntryCount() > 0){
            LAST_LOADED_DATA--;
            activity.getSupportFragmentManager().popBackStack();
            if(LAST_LOADED_DATA == NOTHING_LOADED){
                btnVisible();
                showFbtn();
            }else if(LAST_LOADED_DATA == DIAGNOSIS_DATA_LOADED){
                hideFbtn();
            }
        }else {
            super.onBackPressed();
        }
    }

    public void toggleMenuItemVisibility(boolean b){
        Menu menu = navigationView.getMenu();
        menu.getItem(0).setVisible(b);
        menu.getItem(0).setEnabled(b);
        menu.getItem(1).setVisible(!b);
        menu.getItem(2).setVisible(!b);
        menu.getItem(3).setVisible(!b);
        menu.getItem(4).setVisible(!b);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_sign_in:
                Intent account = new Intent(MainActivity.this, Account.class);
                startActivity(account);
                break;
            case R.id.user_profile:
                Intent profile = new Intent(MainActivity.this, Profile.class);
                startActivity(profile);
                break;
            case R.id.user_history:
                Intent history = new Intent(MainActivity.this, History.class);
                startActivity(history);
                break;
            case R.id.delete_account:
                new AlertDialog.Builder(this).
                        setTitle("Are you sure ?").
                        setMessage("All account data with the history section will be deleted.").
                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteAccount deleteAccount = new DeleteAccount(MainActivity.this);
                                deleteAccount.deleteAccount();
                                toggleMenuItemVisibility(true);
                            }
                        }).
                        setNegativeButton("Cancel", null).
                        setCancelable(false).
                        show();

                break;
            case R.id.user_sign_out:
                mAuth.signOut();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(VISIB, btnVisibility);
        outState.putBoolean(ENBLD, btnEnabled);
        outState.putInt(P_VISIB, progresVisibility);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    public void loadBodyLocations(){
        load("body/locations", new TypeReference<List<HealthItem>>() {});
    }

    public void loadBodySubLocations(Object list){
        load("body/locations/" + ((List<HealthItem>)list).get(0).ID, new TypeReference<List<HealthItem>>() {});
    }

    public void loadSubLocationSymptoms(Object list){
        load("symptoms/" + ((List<HealthItem>)list).get(0).ID + "/" + App.GROUP, new TypeReference<List<HealthSymptomSelector>>() {});
    }

    public void loadDiagnosisData(Object list){
        selectedSymptomsIds = new ArrayList<>();
        for(HealthSymptomSelector symptom : (List<HealthSymptomSelector>)list){
            selectedSymptomsIds.add(symptom.ID);
        }
        try {
            String serializedSymptoms = new ObjectMapper().writeValueAsString(selectedSymptomsIds);
            String action = "diagnosis?symptoms=" + serializedSymptoms + "&gender=" + App.GENDER + "&year_of_birth=" + App.YOB;
            load(action, new TypeReference<List<HealthDiagnosis>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void loadIssueInfo(Object list){
        HD = ((List<HealthDiagnosis>) list).get(0);
        String action = "issues/" + HD.Issue.ID + "/info";
        load(action, new TypeReference<HealthIssueInfo>() {});
    }

    public void ItemClick(Object list) {
        switch (LAST_LOADED_DATA){
            case USER_INFO_LOADED:
                loadBodyLocations();
                break;
            case BODY_LOCATIONS_LOADED:
                loadBodySubLocations(list);
                break;
            case BODY_SUB_LOCATIONS_LOADED:
                loadSubLocationSymptoms(list);
                break;
            case SUB_LOCATION_SYMPTOMS_LOADED:
                loadDiagnosisData(list);
                break;
            case DIAGNOSIS_DATA_LOADED:
                loadIssueInfo(list);
                break;
        }
    }

    public void load(String action, TypeReference<?> valueTypeRef){
        String extraArgs = "token=" + App.accessToken.Token + "&format=json&language=" + getString(R.string.priaid_language);
        String url = new StringBuilder(getString(R.string.priaid_healthservice_url))
                .append("/")
                .append(action)
                .append(action.contains("?") ? "&" : "?")
                .append(extraArgs).toString();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnected()) {
            progresVisibility = View.VISIBLE;
            progressBar.setVisibility(View.VISIBLE);
            new LoadService(this, this).execute(new Pair(url, valueTypeRef));
        }else{
            new CustomToast(MainActivity.this, "App not connected to internet").show();
        }
    }

    public void displayBodyLocations(Object object){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.content_main, new DiagnosisFragment((List<?>) object,  App.BODY_LOCATION_SELECTOR, ListView.CHOICE_MODE_SINGLE, "Select Body Location"))
                .addToBackStack(null)
                .commit();
    }

    public void displayBodySubLocations(Object object){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.content_main, new DiagnosisFragment((List<?>) object, App.BODY_SUB_LOCATION_SELECTOR, ListView.CHOICE_MODE_SINGLE, "Select Sub Location"))
                .addToBackStack(null)
                .commit();
    }

    public void displayBodySubLocationsSymptoms(Object object){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.content_main, new DiagnosisFragment((List<?>) object, App.HEALTH_SYMPTOM_SELECTOR, ListView.CHOICE_MODE_MULTIPLE, "Select symptoms"))
                .addToBackStack(null)
                .commit();
    }

    public void displayDiagnosisData(Object object){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.content_main, new DiagnosisFragment((List<?>) object, App.LOAD_DIAGNOSIS_DATA, ListView.CHOICE_MODE_SINGLE, "Diagnosed Data"))
                .addToBackStack(null)
                .commit();
    }

    public void displayDetailedIssueInfo(Object object){
        HealthIssueInfo info = (HealthIssueInfo) object;
        List<IssueInfo> issueInfo = new ArrayList<>();
        String name = HD.Issue.Name;
        String profName = HD.Issue.ProfName;
        String accuracy = String.valueOf(HD.Issue.Accuracy);
        String ICD = HD.Issue.Icd;
        String icdName = HD.Issue.IcdName;
        String symps = info.PossibleSymptoms;
        String synms = info.Synonyms;
        String medCnd = info.MedicalCondition;
        String shrtDes = info.DescriptionShort;
        String des = info.Description;
        String treatDes = info.TreatmentDescription;
        if(name!=null)issueInfo.add(new IssueInfo("Name", name));
        if(profName!=null)issueInfo.add(new IssueInfo("ProfName", profName));
        if(accuracy!=null)issueInfo.add(new IssueInfo("Accuracy", accuracy + " %"));
        if(ICD!=null)issueInfo.add(new IssueInfo("ICD", ICD.replace(';', ',')));
        if(icdName!=null)issueInfo.add(new IssueInfo("ICD Name", icdName));
        if(symps!=null)issueInfo.add(new IssueInfo("Possible symptoms", "---> " + symps.replace(",", "\n---> ")));
        if(synms!=null)issueInfo.add(new IssueInfo("Synonyms", synms.replace(',', '\n')));
        if(medCnd!=null)issueInfo.add(new IssueInfo("Medical condition", medCnd.replace(". ", ".\n\n")));
        if(shrtDes!=null)issueInfo.add(new IssueInfo("Short description", shrtDes.replace(". ", ".\n\n")));
        if(des!=null)issueInfo.add(new IssueInfo("Description", des.replace(". ", ".\n\n")));
        if(treatDes!=null)issueInfo.add(new IssueInfo("Treatment description", treatDes.replace(". ", ".\n\n")));
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.content_main, new DiagnosisFragment(issueInfo, App.HEALTH_ISSUE_INFO, ListView.CHOICE_MODE_NONE, "Detailed Info"))
                .addToBackStack(null)
                .commit();
        Log.d("TAG", "displayDetailedIssueInfo: " + issueInfo);
        issueInfoSave = issueInfo;
        showFbtn();
    }

    private void showFbtn() {
        fbtnEnbld = true;
        fbtn.setVisibility(View.VISIBLE);
        fbtn.setEnabled(true);
    }

    private void hideFbtn(){
        fbtnEnbld = false;
        fbtn.setVisibility(View.GONE);
        fbtn.setEnabled(false);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(fbtnEnbld){
            showFbtn();
        }else{
            hideFbtn();
        }
    }

    private void startNewFragment(Object object) {
        switch (LAST_LOADED_DATA){
            case BODY_LOCATIONS_LOADED:
                //object -> list of body locations;
                displayBodyLocations(object);
                break;
            case BODY_SUB_LOCATIONS_LOADED:
                //object -> list of body sub-locations based on selected body locations; (List<HealthItem>)
                displayBodySubLocations(object);
                break;
            case SUB_LOCATION_SYMPTOMS_LOADED:
                //object -> list of symptoms based on selected body sub-locations; (List<HealthItem>)
                displayBodySubLocationsSymptoms(object);
                break;
            case DIAGNOSIS_DATA_LOADED:
                //object -> list of diagnosed data based on selected symptoms; (List<HealthDiagnosis>)
                displayDiagnosisData(object);
                break;
            case ISSUE_INFO_LOADED:
                //object -> issue information for selected diagnosis item; (HealthIssueInfo)
                displayDetailedIssueInfo(object);
                break;

        }
    }


    @Override
    public void OnComplete(String message, Object object) {
        if (message.equalsIgnoreCase("access_token")){
            progresVisibility = View.GONE;
            progressBar.setVisibility(View.GONE);
            start();
        }else if(message.equalsIgnoreCase("service")){
            progresVisibility = View.GONE;
            progressBar.setVisibility(View.GONE);
            LAST_LOADED_DATA++;
            if((object instanceof List<?> && ((List<?>)object).isEmpty()) || (object instanceof HealthIssueInfo && (HealthIssueInfo)object == null)){
                GoHome();
                new CustomToast(MainActivity.this, "No result present for selected item").show();
                return;
            }
            startNewFragment(object);
        }
    }

    @Override
    public void OnComplete(String message) {
        new CustomToast(this, message).show();
    }
}
