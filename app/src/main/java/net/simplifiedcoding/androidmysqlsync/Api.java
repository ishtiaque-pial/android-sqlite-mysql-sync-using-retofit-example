package net.simplifiedcoding.androidmysqlsync;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Pial on 30-Apr-17.
 */

public interface Api {

    @POST("saveName.php")
    @FormUrlEncoded
    Call<Demo> getAllResponse(@Field("name") String name);
}
