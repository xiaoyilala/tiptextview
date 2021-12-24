package com.ice.tiptext.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ice.tiptext.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SecFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecFragment newInstance(String param1, String param2) {
        SecFragment fragment = new SecFragment();
        if(param1==null && param2==null) {
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        TextView tv = view.findViewById(R.id.tv);
        tv.setText("OneFragment: "+System.currentTimeMillis());
        Log.d("IceFragment", "2onCreateView");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("IceFragment", "2onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("IceFragment", "2onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("IceFragment", "2onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("IceFragment", "2onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("IceFragment", "2onDestroyView");
    }
}