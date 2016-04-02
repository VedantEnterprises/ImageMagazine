package xyz.hanks.imagemagazine;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    public Observable<ZipResultModel> listSubscribe(String magazineIds, String size) {
        return service.listSubscribe(magazineIds, size);
    }

    public Observable<Result> getExtraData() {
        return service.getExtraData();
    }


    public String downLoadZip(ZipModel zipModel) throws IOException {

        String dir = getSDPath(zipModel.fileUrl);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://appstatic.lenovomm.com/static/" + zipModel.fileUrl).build();
        Response response = okHttpClient.newCall(request).execute();
        InputStream inputStream = response.body().byteStream();

        ZipInputStream zin = new ZipInputStream(inputStream);
        ZipEntry entry;
        String sdPath = null;
        // while there are entries I process them
        while ((entry = zin.getNextEntry()) != null) {
            System.out.println("Unzipping " + entry.getName());
            String path = dir + "/" + entry.getName();
            if(new File(path).exists()){
                continue;
            }
            FileOutputStream fout = new FileOutputStream(path);
            for (int c = zin.read(); c != -1; c = zin.read()) {
                fout.write(c);
            }
            zin.closeEntry();
            fout.close();
        }
        zin.close();
        return dir;
    }

    private String getSDPath(String fileUrl) {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/magazine/"+fileUrl;
        File file = new File(absolutePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
}
