package xyz.hanks.imagemagazine;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by hanks on 16/4/2.
 */
public interface ApiService {

    @GET("magazine/api/magazine/list") Observable<Result> getList();


    @GET("magazine/api/magazine/extraData") Observable<Result> getExtraData();


    @FormUrlEncoded
    @POST("magazine/api/periodical/listSubscribe") Observable<Result> listSubscribe(@Field(
            "magazineIds") String magazineIds, @Field("size") String size);

//    http://appstatic.lenovomm.com/static/magazine/20160330/8746cf0bbe24411598125f37f49fbac1

}

