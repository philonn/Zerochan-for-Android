package tr.philon.zerochan.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.ui.fragments.GalleryFragment;

public class SearchActivity extends AppCompatActivity {
    public static final String ARG_TAGS = "arg_tags";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    String mTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mTags = getIntent().getStringExtra(ARG_TAGS);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTags);

        initFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    public void initFragment() {
        Fragment fragment = new GalleryFragment();
        Bundle bundle = new Bundle();

        bundle.putString(ARG_TAGS, mTags);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }
}