package xyz.hanks.imagemagazine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String SUBSCRIBEMODEL = "subscribeModel";

    SubscribeModel model;

    @Bind(R.id.viewpager) ViewPager mViewPager;

    private MagazineAdapter mAdapter;

    public static void start(Context context, SubscribeModel model) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra(SUBSCRIBEMODEL, model);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideSystemUI();
        mAdapter = new MagazineAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);


        if (getIntent().hasExtra(SUBSCRIBEMODEL)) {
            model = getIntent().getParcelableExtra(SUBSCRIBEMODEL);
            initData();
        } else {
            SubscribeActivity.start(this);
        }
    }

    private void initData() {

        String magazineIds = "3,5,6,7,8,9,11,13,15,17,19,21,22,23,24,27,28,29,30,31,33,34,35,36,38";
        String size = "200";
        RestfulClient.getInstance().listSubscribe(magazineIds, size)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<ZipResultModel, Observable<ZipModel>>() {
                    @Override public Observable<ZipModel> call(ZipResultModel zipResultModel) {
                        return Observable.from(zipResultModel.data);
                    }
                })
                .doOnNext(new Action1<ZipModel>() {
                    @Override public void call(ZipModel zipModel) {
                        try {
                            RestfulClient.getInstance().downLoadZip(zipModel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ZipModel>() {
                    @Override public void call(ZipModel zipModel) {
                        Log.e("hhhhh","success........................");
                    }
                });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    static class MagazineAdapter extends FragmentPagerAdapter {

        public MagazineAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(int position) {
            return null;
        }

        @Override public int getCount() {
            return 0;
        }
    }
}
