package cn.xihan.age.activitys;

import static cn.xihan.age.Config.*;
import static cn.xihan.age.utils.Utils.goBrowser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.tencent.bugly.beta.Beta;
import com.xihan.age.paylibrary.MiniPayUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xihan.age.Config;
import cn.xihan.age.R;
import cn.xihan.age.base.BaseActivity;
import cn.xihan.age.utils.SharedPreferencesUtils;

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2021/3/19 23:39
 * @介绍 :
 */
public class AboutActivity extends BaseActivity {


    @BindView(R.id.iv_tx)
    ImageView         ivTx;
    @BindView(R.id.version)
    TextView          version;
    @BindView(R.id.about_list)
    QMUIGroupListView aboutList;
    @BindView(R.id.copyright)
    TextView          copyright;
    @BindView(R.id.topbar)
    QMUITopBarLayout  topbar;

    Context ctx;


    boolean isOpen;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        ctx = getApplicationContext();
        initTopBar();
        initView();

        version.setText("当前版本: " + QMUIPackageHelper.getAppVersion(getApplicationContext()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        copyright.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));

    }

    private void initView() {
        RequestOptions requestOptions = RequestOptions.bitmapTransform(new RoundedCorners(360));
        Glide.with(this).load(R.drawable.mytx).apply(requestOptions).into(ivTx);

        QMUICommonListItemView about_item_homepage = aboutList.createItemView(getResources().getString(R.string.about_item_homepage));
        about_item_homepage.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView about_item_github = aboutList.createItemView(getResources().getString(R.string.about_item_github));
        about_item_github.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView about_item_ds = aboutList.createItemView(getResources().getString(R.string.about_item_ds));
        about_item_ds.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView about_item_x5setting = aboutList.createItemView(getResources().getString(R.string.about_item_x5setting));
        about_item_x5setting.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView about_item_update = aboutList.createItemView(getResources().getString(R.string.about_item_update));
        about_item_update.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView about_item_x5AutoPlayer = aboutList.createItemView(getResources().getString(R.string.about_item_x5AutoPlayer));
        about_item_x5AutoPlayer.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        about_item_x5AutoPlayer.getSwitch().setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!(Boolean) SharedPreferencesUtils.getParam(ctx, "X5State", false)) {
                Snackbar.make(aboutList, "x5内核加载失败,此开关无效啦~", Snackbar.LENGTH_LONG).show();
                SharedPreferencesUtils.setParam(ctx, "自动全屏播放", false);
            } else {
                SharedPreferencesUtils.setParam(ctx, "自动全屏播放", isChecked);
            }
        });
        about_item_x5AutoPlayer.getSwitch().setChecked((Boolean) SharedPreferencesUtils.getParam(ctx, "自动全屏播放", false));

        QMUICommonListItemView about_item_diyApi = aboutList.createItemView(getResources().getString(R.string.about_item_diyApi));
        about_item_diyApi.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        about_item_diyApi.getSwitch().setOnCheckedChangeListener((buttonView, isChecked) -> SharedPreferencesUtils.setParam(ctx, "自定义API", isChecked));
        about_item_diyApi.getSwitch().setChecked((Boolean) SharedPreferencesUtils.getParam(ctx, "自定义API", false));

        QMUICommonListItemView about_item_autodelete = aboutList.createItemView(getResources().getString(R.string.about_item_autodelete));
        about_item_autodelete.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        about_item_autodelete.getSwitch().setOnCheckedChangeListener((buttonView, isChecked) -> SharedPreferencesUtils.setParam(ctx, "自动删除视频缓存", isChecked));
        about_item_autodelete.getSwitch().setChecked((Boolean) SharedPreferencesUtils.getParam(ctx, "自动删除视频缓存", false));


        int size = QMUIDisplayHelper.dp2px(getApplicationContext(), 20);

        View.OnClickListener onClickListener = v -> {
            if (v instanceof QMUICommonListItemView) {
                String text = (String) ((QMUICommonListItemView) v).getText();
                switch(text){
                    case "访问官网":
                        goBrowser(OfficialWebsite);
                        break;
                    case "GitHub":
                        goBrowser(GithubWebsite);
                        break;
                    case "打赏":
                        MiniPayUtils.setupPay(this, new com.xihan.age.paylibrary.Config.Builder(Config.payCode, R.drawable.zfb2, R.drawable.wx2).build());
                        break;
                    case "内核设置":
                        Bundle bundle = new Bundle();
                        bundle.putString("url","http://debugtbs.qq.com");
                        startActivity(new Intent(this,WebActivity.class).putExtras(bundle));
                        break;
                    case "检查更新":
                        showUpdateDialog();
                        break;
                }

                if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                    ((QMUICommonListItemView) v).getSwitch().toggle();
                }
            }
        };

        QMUIGroupListView.newSection(this)
                .addItemView(about_item_homepage, onClickListener)
                .addItemView(about_item_github, onClickListener)
                .addItemView(about_item_ds, onClickListener)
                .addItemView(about_item_x5setting, onClickListener)
                .addItemView(about_item_update, onClickListener)
                .addItemView(about_item_x5AutoPlayer, onClickListener)
                .addItemView(about_item_autodelete,onClickListener)
                .addItemView(about_item_diyApi, onClickListener)

                .addTo(aboutList);

    }


    String isUpdate;
    private final int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    /**
     * 展示更新
     */
    private void showUpdateDialog() {
        int bdversionCode = 0;
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        String versionName = sharedPreferences.getString("versionName", null);
        String GGRZ = sharedPreferences.getString("GGRZ", null);
        String DownloadApkUrl = sharedPreferences.getString("DownloadApkUrl", null);
        int zxversioncode = Integer.parseInt(sharedPreferences.getString("versionCode", "0"));
        try {
            bdversionCode = ctx.getApplicationContext().getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.d("xihan", "最新:" + zxversioncode + "("+ zxversioncode+")" + "\n当前:" + bdversionCode);
        if (zxversioncode > bdversionCode) {
            isUpdate = "有更新! 最新版为:" + versionName + "("+ zxversioncode+")";
        } else {
            isUpdate = "无更新! 最新版为:" + versionName + "("+ bdversionCode+")";
        }
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle(isUpdate)
                .setMessage(GGRZ)
                .setSkinManager(QMUISkinManager.defaultInstance(this))
                .addAction("蓝奏云", (dialog, index) -> {
                    dialog.dismiss();
                    Uri uri = Uri.parse(UpdateUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    Toast.makeText(getApplicationContext(), "密码:xihan", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                })
                .addAction("直接下载", (dialog, index) -> {
                    dialog.dismiss();
                    Uri uri = Uri.parse(DownloadApkUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                })
                .addAction(0, "Bugly更新", QMUIDialogAction.ACTION_PROP_POSITIVE, (dialog, index) -> {
                    dialog.dismiss();
                    Beta.checkUpgrade();
                })

                .create(mCurrentDialogStyle).show();
    }

    private void initTopBar() {
        topbar.addLeftBackImageButton().setOnClickListener(v -> {
            startActivity(new Intent(AboutActivity.this, MainActivity.class));
            finish();
        });

        topbar.setTitle(getResources().getString(R.string.about_title));
    }

}
