package tr.philon.zerochan.views.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.Service;
import tr.philon.zerochan.views.fragments.GalleryFragment;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    ActionBar mActionbar;
    GalleryFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();

        if (savedInstanceState != null) {
            mFragment = (GalleryFragment) getSupportFragmentManager().findFragmentByTag(GalleryFragment.TAG);
        } else {
            mFragment = GalleryFragment.newInstance(Service.TAG_EVERYTHING, false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(mFragment, GalleryFragment.TAG)
                    .commit();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, mFragment)
                .commit();
    }
}