package me.davidnery.meusuap;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import me.davidnery.meusuap.fragments.FragmentAnual;
import me.davidnery.meusuap.fragments.FragmentSemestral;
import me.davidnery.meusuap.fragmentsadpters.TipoMateriaFragmentAdapter;

public class CalcularNota extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcular_nota);

        Bundle bundle = getIntent().getExtras();

        TipoMateriaFragmentAdapter adapter = new TipoMateriaFragmentAdapter(getSupportFragmentManager());
        FragmentAnual fragmentAnual = new FragmentAnual();
        fragmentAnual.setArguments(bundle);
        FragmentSemestral fragmentSemestral = new FragmentSemestral();
        fragmentSemestral.setArguments(bundle);
        adapter.add(fragmentAnual, "ANUAL");
        adapter.add(fragmentSemestral, "SEMESTRAL");

        ViewPager viewPager = (ViewPager) findViewById(R.id.tipomateriavp);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tipomateria);
        tabLayout.setupWithViewPager(viewPager);

        if (bundle != null && bundle.containsKey("n1_sem"))
            viewPager.setCurrentItem(1, true);
    }
}
