package tr.philon.zerochan.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.views.fragments.GalleryFragment;
import tr.philon.zerochan.views.fragments.PopularTagsFragment;

public class TagsActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    ActionBar mActionbar;
    PopularTagsFragment mFragment;

    public static Intent newInstance(Context context) {
        return new Intent(context, TagsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mFragment = (PopularTagsFragment) getSupportFragmentManager().findFragmentByTag(PopularTagsFragment.TAG);
        } else {
            mFragment = PopularTagsFragment.newInstance();
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