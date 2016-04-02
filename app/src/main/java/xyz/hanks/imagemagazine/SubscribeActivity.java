package xyz.hanks.imagemagazine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SubscribeActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, SubscribeActivity.class);
        context.startActivity(starter);
    }

    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    List<SubscribeModel> mDataList = new ArrayList<>();
    private SubscribeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        ButterKnife.bind(this);
        mAdapter = new SubscribeAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);


        RestfulClient.getInstance().getList()
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<Result, Integer>() {
                    @Override public Integer call(Result result) {
                        mDataList.clear();
                        mDataList.addAll(result.data);
                        return 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override public void call(Integer integer) {
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    static class SubscribeViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_name) TextView tv_name;
        @Bind(R.id.tv_supplyDesc) TextView tv_supplyDesc;
        @Bind(R.id.tv_description) TextView tv_description;

        public SubscribeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class SubscribeAdapter extends RecyclerView.Adapter<SubscribeViewHolder> {

        @Override
        public SubscribeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscribe, parent, false);
            return new SubscribeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SubscribeViewHolder holder, int position) {
            SubscribeModel subscribeModel = mDataList.get(position);
            holder.tv_name.setText(subscribeModel.name);
            holder.tv_supplyDesc.setText(subscribeModel.supplyDesc);
            holder.tv_description.setText(subscribeModel.description);
        }

        @Override public int getItemCount() {
            return mDataList.size();
        }
    }
}
