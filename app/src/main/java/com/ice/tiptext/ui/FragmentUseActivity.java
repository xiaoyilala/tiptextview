package com.ice.tiptext.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ice.tiptext.R;
import com.ice.tiptext.ui.fragment.OneFragment;
import com.ice.tiptext.ui.fragment.SecFragment;
import com.ice.tiptext.ui.fragment.ThrFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentUseActivity extends BaseActivity{

    public static final String One_Fragment = "OneFragment";

    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_use);

        ViewPager2 viewPager2 = findViewById(R.id.vp2);

        OneFragment oneFragment1 = OneFragment.newInstance(null, null);
        OneFragment oneFragment = OneFragment.newInstance(null, null);
        SecFragment secFragment = SecFragment.newInstance(null, null);
        ThrFragment thrFragment = ThrFragment.newInstance(null, null);

        fragments.clear();
        fragments.add(oneFragment);
        fragments.add(secFragment);
        fragments.add(thrFragment);

        //fragment的懒加载
        viewPager2.setAdapter(new MyFragmentStateAdapter(this, fragments));
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);


//        //重影是因为activity重新执行了这段代码，且同时在activity保存了fragment的状态但没有保存fragment的view
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fl, oneFragment)
//                .commit();

        //解决重影
        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(One_Fragment);
        if(fragmentByTag==null){
            getSupportFragmentManager().beginTransaction()
                .add(R.id.fl, oneFragment1, One_Fragment)
                .commit();
        }

        Log.d("FragmentUseActivity", "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("FragmentUseActivity", "onDestroy");
    }

    private static class MyFragmentStateAdapter extends FragmentStateAdapter{

        private List<Fragment> fragments;

        public MyFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}
