package xyz.hanks.imagemagazine;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by hanks on 16/4/2.
 */
public class RestfulClient {

    public static RestfulClient sInstance;
    private ApiService service;

    private RestfulClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://magazine.lenovomm.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);
    }

    public static RestfulClient getInstance() {

        if (sInstance == null) {
            sInstance = new RestfulClient();
        }

        return sInstance;
    }


    public Observable<Result> getList() {
        return service.getList();
    }

    public Observable<Result> getExtraData() {
        return service.getExtraData();
    }
}
