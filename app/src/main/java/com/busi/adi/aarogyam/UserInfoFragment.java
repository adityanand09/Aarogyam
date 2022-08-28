package com.busi.adi.aarogyam;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class UserInfoFragment extends Fragment {
    private static final String TAG = "USER_INFO_FRAG";
    private static List<String> group = new ArrayList<String>(){
        {
            add("Man");
            add("Woman");
            add("Boy");
            add("Girl");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_info_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText yob = view.findViewById(R.id.user_yob);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.health_item, group);
        ListView lv = view.findViewById(R.id.user_group);
        lv.setAdapter(adapter);
        Button submit = view.findViewById(R.id.userinfo_submit);
        submit.setOnClickListener(view1 -> {
            String YOB = yob.getText().toString().trim();
            int group = -1;
            for(int i=0; i<4; i++){
                if(lv.isItemChecked(i))
                    group = i;
            }
            if(YOB.isEmpty()){
                new CustomToast(getContext(), "Please enter your year of birth").show();
            }else if(group == -1){
                new CustomToast(getContext(), "Please select you group").show();
            }else{
                App.GROUP = group;
                App.GENDER = group==0||group==1?"male":"female";
                App.YOB = YOB;
                submit.setEnabled(false);
                ((MainActivity)getActivity()).ItemClick(null);
            }
        });
    }
}
