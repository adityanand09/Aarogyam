package com.busi.adi.aarogyam.health;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.busi.adi.aarogyam.CustomToast;
import com.busi.adi.aarogyam.MainActivity;
import com.busi.adi.aarogyam.R;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisFragment extends Fragment {
    private static final String TITLE = "TITLE";
    private static final String HEALTH_ITEMS = "HEALTH_ITEMS";
    private static final String CHOICE_MODE = "CHOICE_MODE";
    private static final String ID = "ID";

    private static final String TAG = "DFRAG";

    private  int itemId;
    private  int mChoiceMode;
    private  String mTitle;
    private  List<?> mList = null;
    private  ListAdapter adapter = null;
    public DiagnosisFragment(){}
    public DiagnosisFragment(List<?> list, int id, int choiceMode, String title){
        Log.d(TAG, "DiagnosisFragment: " + (list!=null));
        mList = list;
        Log.d(TAG, "DiagnosisFragment: ");
        itemId = id;
        mChoiceMode = choiceMode;
        mTitle = title;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mList = savedInstanceState.getParcelableArrayList(HEALTH_ITEMS);
            mTitle = savedInstanceState.getString(TITLE);
            itemId = savedInstanceState.getInt(ID);
            mChoiceMode = savedInstanceState.getInt(CHOICE_MODE);
        }
        Log.d(TAG, "onCreate: ");
        if(adapter == null)
        adapter = (ListAdapter) new HealthAdapter(mList, getContext()).load(itemId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(mTitle);
        return inflater.inflate(R.layout.diagnosis_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lv = view.findViewById(R.id.subLocationsSymptomsList);
        List<Object> obj = new ArrayList<>();
        lv.setAdapter(adapter);
        lv.setChoiceMode(mChoiceMode);
        Button subLocationsSymptoms_submit = view.findViewById(R.id.subLocationsSymptoms_submit);
        if(mChoiceMode==0){
            lv.setSelector(android.R.color.transparent);
            subLocationsSymptoms_submit.setVisibility(View.GONE);
            subLocationsSymptoms_submit.setEnabled(false);
        }
        subLocationsSymptoms_submit.setOnClickListener(view1 -> {
            for(int i=0; i<lv.getAdapter().getCount(); i++){
                if(lv.isItemChecked(i)){
                    obj.add(mList.get(i));
                }
            }
            if(!obj.isEmpty()) {
                subLocationsSymptoms_submit.setEnabled(false);
                ((MainActivity) getActivity()).ItemClick(obj);
            }
            else{
                new CustomToast(getContext(), "Please select an item").show();
            }
        });

    }
}
