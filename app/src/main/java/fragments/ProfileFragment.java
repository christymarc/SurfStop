package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.surfstop.R;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseFile;
import com.parse.ParseUser;

import adapters.ProfileFragmentAdapter;
import utils.InternetUtil;
import utils.PostImage;

public class ProfileFragment extends Fragment {
    TextView tvName;
    ImageView ivProfile;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfileFragmentAdapter profileFragmentAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvName);
        ivProfile = view.findViewById(R.id.ivProfile);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager2);

        profileFragmentAdapter = new ProfileFragmentAdapter(this);
        viewPager2.setAdapter(profileFragmentAdapter);

        ParseUser currentUser = ParseUser.getCurrentUser();

        tvName.setText(currentUser.getUsername());
        ParseFile profilePhoto = currentUser.getParseFile("profilePhoto");
        if (profilePhoto != null) {
            PostImage.loadPfpIntoView(getContext(), profilePhoto.getUrl(), ivProfile);
        }

        // Listener for tab selection to switch between fragments
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Ensures that when view is swiped on, tabLayout will update properly
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }
}