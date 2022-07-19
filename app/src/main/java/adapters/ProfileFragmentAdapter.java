package adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fragments.profileFragments.MyGroupsFragment;
import fragments.profileFragments.MyPostsFragment;

public class ProfileFragmentAdapter extends FragmentStateAdapter {
    private static final int GROUP_FRAGMENT_SELECTED = 1;
    final static int NUMBER_OF_TABS = 2;

    public ProfileFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case GROUP_FRAGMENT_SELECTED:
                return new MyGroupsFragment();
            default: return new MyPostsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUMBER_OF_TABS;
    }
}
