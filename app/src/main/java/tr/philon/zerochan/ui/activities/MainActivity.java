package tr.philon.zerochan.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.Service;
import tr.philon.zerochan.ui.fragments.GalleryFragment;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    ActionBar mActionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,
                        GalleryFragment.newInstance(Service.TAG_EVERYTHING))
                .commit();
    }
}