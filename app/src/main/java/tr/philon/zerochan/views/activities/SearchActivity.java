package tr.philon.zerochan.views.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.views.fragments.GalleryFragment;

public class SearchActivity extends AppCompatActivity {
    public static final String ARG_TAGS = "arg_tags";
    public static final String ARG_IS_SINGLE_TAG = "arg_is_single_tag";
    public static final String ARG_USER = "arg_user";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String tags = getIntent().getStringExtra(ARG_TAGS);
        boolean isUser = getIntent().getBooleanExtra(ARG_USER, false);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(isUser ? "user : " + tags : tags);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, GalleryFragment.newInstance(tags, isUser))
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}