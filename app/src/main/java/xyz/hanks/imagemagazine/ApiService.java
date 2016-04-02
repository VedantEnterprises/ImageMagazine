package xyz.hanks.imagemagazine;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by hanks on 16/4/2.
 */
public interface ApiService {

    @GET("magazine/api/magazine/list")
    Observable<Result> getList();


    @GET("magazine/api/magazine/extraData")
    Observable<Result> getExtraData();
}
