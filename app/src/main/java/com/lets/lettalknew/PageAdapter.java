package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;


    public class PageAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragmentList = new ArrayList<> (  );

        private final ArrayList<String> fragmentTitle = new ArrayList<> (  );

        Fragment fragment;

        public PageAdapter(@NonNull FragmentManager fm) {
            super ( fm );

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get ( position );

        }

        @Override
        public int getCount() {
            return fragmentList.size ();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get ( position );
        }

        public void addFragment(Fragment fragment , String title) {
            fragmentList.add ( fragment );
            fragmentTitle.add ( title );
        }
}
