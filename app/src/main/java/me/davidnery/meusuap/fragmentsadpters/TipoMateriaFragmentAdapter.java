package me.davidnery.meusuap.fragmentsadpters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 17/11/2018.
 */

public class TipoMateriaFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<String> titulos;

    public TipoMateriaFragmentAdapter(FragmentManager fm) {
        super(fm);

        this.fragments = new ArrayList<>();
        this.titulos = new ArrayList<>();
    }

    public void add(Fragment fragment, String title) {
        this.fragments.add(fragment);
        this.titulos.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.titulos.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
