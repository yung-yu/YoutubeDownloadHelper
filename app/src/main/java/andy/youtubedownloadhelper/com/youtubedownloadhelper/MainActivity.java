package andy.youtubedownloadhelper.com.youtubedownloadhelper;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.andylibrary.utils.Log;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.db.DB;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Youtube;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.list.YoutubeListFragment;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.AndroidUtils;
import io.realm.Realm;


public class MainActivity extends FragmentActivity implements TabLayout.OnTabSelectedListener{
   TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.addTab(tabLayout.newTab().setText("列表"), true);
        AndroidUtils.startFragment(getSupportFragmentManager(), new YoutubeListFragment(), null, false);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:
                    AndroidUtils.startFragment(getSupportFragmentManager(),new YoutubeListFragment(),null,false);
                    break;
            }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
