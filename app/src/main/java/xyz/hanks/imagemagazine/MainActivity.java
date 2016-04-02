package xyz.hanks.imagemagazine;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String SUBSCRIBEMODEL = "subscribeModel";
    private static final int IMAGE_SIZE = 3;

    SubscribeModel model;

    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.loading) View mLoading;


    List<String> mImagePathList = new ArrayList<>();
    List<ImageView> mImageList = new ArrayList<>();

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
        mAdapter = new MagazineAdapter();
        mViewPager.setAdapter(mAdapter);

        for (int i = 0; i < IMAGE_SIZE; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mImageList.add(imageView);
        }


        if (getIntent().hasExtra(SUBSCRIBEMODEL)) {
            model = getIntent().getParcelableExtra(SUBSCRIBEMODEL);
            initData();
        } else {
            mLoading.setVisibility(View.GONE);
            SubscribeActivity.start(this);
            finish();
        }
    }

    private void initData() {

        String magazineIds = "2";
        String size = "200";
        RestfulClient.getInstance().listSubscribe(magazineIds, size)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<ZipResultModel, Observable<ZipModel>>() {
                    @Override public Observable<ZipModel> call(ZipResultModel zipResultModel) {
                        return Observable.from(zipResultModel.data);
                    }
                })
                .map(new Func1<ZipModel, String>() {
                    @Override public String call(ZipModel zipModel) {
                        try {
                            return RestfulClient.getInstance().downLoadZip(zipModel);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override public Boolean call(String s) {
                        return s != null && !"".equals(s);
                    }
                })
                .map(new Func1<String, Integer>() {
                    @Override public Integer call(String dirPath) {
                        File file = new File(dirPath);
                        if (file.exists()) {
                            for (File file1 : file.listFiles()) {
                                if (file1.getName().endsWith(".jpg")) {
                                    mImagePathList.add(file1.getAbsolutePath());
                                }
                            }
                        }
                        return 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override public void call(Integer dirPath) {
                        mLoading.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                        Log.e("hhhhh", "success........................");
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

    class MagazineAdapter extends PagerAdapter {

        @Override public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = null;
            try {
                imageView = mImageList.get(position % IMAGE_SIZE);
                container.addView(imageView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(mImagePathList.get(position % mImagePathList.size())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageView;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageView = mImageList.get(position % IMAGE_SIZE);
            container.removeView(imageView);
        }
    }
}
