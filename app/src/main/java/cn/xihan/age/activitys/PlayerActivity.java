package cn.xihan.age.activitys;

import android.app.PictureInPictureParams;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;



import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import cn.xihan.age.R;
import cn.xihan.age.base.BaseActivity;
import cn.xihan.age.custom.JZExoPlayer;
import cn.xihan.age.custom.JZPlayer;
import cn.xihan.age.utils.SharedPreferencesUtils;
import cn.xihan.age.utils.Utils;

public class PlayerActivity extends BaseActivity implements JZPlayer.CompleteListener, JZPlayer.TouchListener {

    @BindView(R.id.player)
    JZPlayer    player;
    @BindView(R.id.videoContainer)
    FrameLayout videoContainer;
    private String witchTitle, url, diliUrl;
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;

    @BindView(R.id.nav_view)
    LinearLayout   linearLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout   drawerLayout;
    @BindView(R.id.anime_title)
    TextView       titleView;
    @BindView(R.id.pic_config)
    RelativeLayout picConfig;

    @BindView(R.id.pic_config3)
    RelativeLayout pic_config3;


    //播放网址
    private String  webUrl;
    private boolean isPip = false;

    @BindView(R.id.nav_config_view)
    LinearLayout navConfigView;
    @BindView(R.id.speed)
    TextView     speedTextView;

    private final String[] speeds    = Utils.getArray(R.array.speed_item);
    private       int      userSpeed = 2;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        hideGap();
        initUserConfig();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
            witchTitle = bundle.getString("title");
            init(url, witchTitle);
        }


    }

    private void init(String url, String witchTitle) {

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    JzvdStd.goOnPlayOnPause();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                    JzvdStd.goOnPlayOnResume();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        player.config.setOnClickListener(v -> {
            if (!Utils.isFastClick()) return;
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else drawerLayout.openDrawer(GravityCompat.START);
        });
        player.setListener(this, this, this);
        player.backButton.setOnClickListener(v -> finish());
        if (gtSdk23()) {
            player.tvSpeed.setVisibility(View.VISIBLE);
        } else {
            player.tvSpeed.setVisibility(View.GONE);
        }
        player.setUp(url, witchTitle, Jzvd.SCREEN_FULLSCREEN, JZExoPlayer.class);
        player.fullscreenButton.setOnClickListener(view -> {
            if (!Utils.isFastClick()) return;
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            else drawerLayout.openDrawer(GravityCompat.END);
        });
        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        player.startButton.performClick();
        player.startVideo();
    }


    private void initUserConfig() {
        switch ((Integer) SharedPreferencesUtils.getParam(this, "user_speed", 15)) {
            case 5:
                setUserSpeedConfig(speeds[0], 0);
                break;
            case 10:
                setUserSpeedConfig(speeds[1], 1);
                break;
            case 15:
                setUserSpeedConfig(speeds[2], 2);
                break;
            case 30:
                setUserSpeedConfig(speeds[3], 3);
                break;
        }
    }

    private void setUserSpeedConfig(String text, int speed) {
        speedTextView.setText(text);
        userSpeed = speed;
    }

    private void setDefaultSpeed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Utils.getString(R.string.set_user_speed));
        builder.setSingleChoiceItems(speeds, userSpeed, (dialog, which) -> {
            switch (which) {
                case 0:
                    SharedPreferencesUtils.setParam(getApplicationContext(), "user_speed", 5);
                    setUserSpeedConfig(speeds[0], which);
                    break;
                case 1:
                    SharedPreferencesUtils.setParam(getApplicationContext(), "user_speed", 10);
                    setUserSpeedConfig(speeds[1], which);
                    break;
                case 2:
                    SharedPreferencesUtils.setParam(getApplicationContext(), "user_speed", 15);
                    setUserSpeedConfig(speeds[2], which);
                    break;
                case 3:
                    SharedPreferencesUtils.setParam(getApplicationContext(), "user_speed", 30);
                    setUserSpeedConfig(speeds[3], which);
                    break;
            }
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void doOnBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.closeDrawer(GravityCompat.END);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!inMultiWindow()) JzvdStd.goOnPlayOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavBar();
        if (!inMultiWindow()) JzvdStd.goOnPlayOnResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPip) finish();
    }

    /**
     * 是否为分屏模式
     *
     * @return
     */
    public boolean inMultiWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return this.isInMultiWindowMode();
        else return false;
    }

    /**
     * Android 8.0 画中画
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPicInPic() {
//        PictureInPictureParams.Builder builder = new PictureInPictureParams.Builder();
        // 设置宽高比例值，第一个参数表示分子，第二个参数表示分母
        // 下面的10/5=2，表示画中画窗口的宽度是高度的两倍
//        Rational aspectRatio = new Rational(10,5);
        // 设置画中画窗口的宽高比例
//        builder.setAspectRatio(aspectRatio);
        // 进入画中画模式，注意enterPictureInPictureMode是Android8.0之后新增的方法
//        enterPictureInPictureMode(builder.build());
        PictureInPictureParams builder = new PictureInPictureParams.Builder().build();
        enterPictureInPictureMode(builder);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            player.startPIP();
            isPip = true;
        } else isPip = false;
    }


    @Override
    public void complete() {
        Toast.makeText(this, "播放完毕", Toast.LENGTH_SHORT).show();
        if (!drawerLayout.isDrawerOpen(GravityCompat.END))
            drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void touch() {
        hideNavBar();
    }

    @OnClick({R.id.speed_config, R.id.pic_config, R.id.player_config, R.id.browser_config, R.id.pic_config3})
    public void configBtnClick(RelativeLayout relativeLayout) {
        switch (relativeLayout.getId()) {
            case R.id.speed_config:
                setDefaultSpeed();
                break;
            case R.id.pic_config:
                if (gtSdk26()) startPic();
                break;
            case R.id.player_config:
                Utils.selectVideoPlayer(this, url);
                break;
            case R.id.browser_config:
                Utils.goBrowser(url);
                break;
            case R.id.pic_config3:
                break;
        }
    }


    public void startPic() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        JzvdStd.goOnPlayOnResume();
        new Handler().postDelayed(this::enterPicInPic, 500);
    }



}