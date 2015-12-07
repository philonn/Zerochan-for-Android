package tr.philon.zerochan.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.ui.fragments.GalleryFragment;
import tr.philon.zerochan.ui.fragments.TagsFragment;

public class MainActivity extends AppCompatActivity {
    public static final String ARG_SECTION = "arg_section";
    public static final int ID_EVERYTHING = 1;
    public static final int ID_POPULAR = 2;
    public static final int ID_TAGS = 3;
    private static final int ID_ABOUT = 4;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    ActionBar mActionbar;
    Drawer mDrawer;

    int lastSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mActionbar = getSupportActionBar();

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withDrawerWidthDp(280)
                .withToolbar(mToolbar)
                .withHeader(R.layout.view_drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Everything").withIdentifier(ID_EVERYTHING)
                                .withIcon(R.drawable.ic_image).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withName("Popular Today").withIdentifier(ID_POPULAR)
                                .withIcon(R.drawable.ic_today).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withName("Tags").withIdentifier(ID_TAGS)
                                .withIcon(R.drawable.ic_tag).withIconTintingEnabled(true),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("About").withIdentifier(ID_ABOUT).withSelectable(false))
                .withOnDrawerItemClickListener(drawerItemClickListener)
                .build();

        if (savedInstanceState != null)
            lastSelection = savedInstanceState.getInt("lastSelection");
        if (lastSelection == 0)
            lastSelection = ID_EVERYTHING;
        replaceFragment(lastSelection);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastSelection", lastSelection);
    }

    public void replaceFragment(int id) {
        Fragment fragment = new GalleryFragment();
        Bundle bundle = new Bundle();
        String title = "";

        switch (id) {
            case ID_EVERYTHING:
                title = "Everything";
                break;
            case ID_POPULAR:
                title = "Popular Today";
                break;
            case ID_TAGS:
                title = "Tags";
                fragment = new TagsFragment();
                break;
        }

        lastSelection = id;
        mDrawer.setSelection(id, false);
        mActionbar.setTitle(title);

        bundle.putInt(ARG_SECTION, id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    private Drawer.OnDrawerItemClickListener drawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            int id = drawerItem.getIdentifier();
            if (id <= ID_TAGS) replaceFragment(id);
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
