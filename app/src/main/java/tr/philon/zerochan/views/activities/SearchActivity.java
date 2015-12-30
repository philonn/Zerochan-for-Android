package tr.philon.zerochan.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.Service;
import tr.philon.zerochan.views.fragments.GalleryFragment;

public class SearchActivity extends AppCompatActivity {
    public static final String ARG_TAGS = "arg_tags";
    public static final String ARG_IS_SINGLE_TAG = "arg_is_single_tag";
    public static final String ARG_USER = "arg_user";
    public static final String ARG_MAIN_PAGE = "arg_main_page";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    GalleryFragment mFragment;

    public static Intent newInstance(Context context, String tags){
        return newInstance(context, tags, false);
    }

    public static Intent newInstance(Context context, String tags, boolean isUser){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(SearchActivity.ARG_TAGS, tags);
        intent.putExtra(SearchActivity.ARG_USER, isUser);
        return intent;
    }

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

        if (savedInstanceState != null) {
            mFragment = (GalleryFragment) getSupportFragmentManager().findFragmentByTag(GalleryFragment.TAG);
        } else {
            mFragment = GalleryFragment.newInstance(tags, isUser);
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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}