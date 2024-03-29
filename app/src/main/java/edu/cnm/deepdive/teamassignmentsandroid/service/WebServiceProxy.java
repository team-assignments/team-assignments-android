package edu.cnm.deepdive.teamassignmentsandroid.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.teamassignmentsandroid.BuildConfig;
import edu.cnm.deepdive.teamassignmentsandroid.model.pojo.Group;
import edu.cnm.deepdive.teamassignmentsandroid.model.pojo.Task;
import edu.cnm.deepdive.teamassignmentsandroid.model.pojo.User;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.Date;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interfaces application with database from the service.
 */
public interface WebServiceProxy {

  @GET("users/me")
  Single<User> getProfile(@Header("Authorization") String bearerToken);

  @GET("users")
  Single<List<User>> getUsers(@Header("Authorization") String bearerToken);

  @POST("groups")
  Single<Group> postGroup(@Body Group group, @Header("Authorization") String bearerToken);

  @PUT("groups/{groupId}/members/{userId}")
  @Headers({"Accept: text/plain", "Content-Type: text/plain"})
  Single<Boolean> putMembership(@Body boolean inGroup, @Path("userId") long userId,
      @Path("groupId") long groupId, @Header("Authorization") String bearerToken);

  @GET("groups/{groupId}/members/{userId}")
  @Headers({"Accept: text/plain"})
  Single<Boolean> getMembership(@Path("groupId") long groupId, @Path("groupId") long userId,
      @Header("Authorization") String bearerToken);

  @GET("groups/{id}")
  Single<Group> getGroup(@Path("id") long id, @Header("Authorization") String bearerToken);

  @DELETE("groups/{id}")
  Completable deleteGroup(@Path("id") long id, @Header("Authorization") String bearerToken);

  @GET("groups")
  Single<List<Group>> getGroups(@Header("Authorization") String bearerToken);

  @GET("groups")
  Single<List<Group>> getGroups(@Query("ownedOnly") boolean ownedOnly,
      @Header("Authorization") String bearerToken);

  @PUT("groups/{id}/name")
  @Headers({"Accept: text/plain", "Content-Type: text/plain"})
  Single<String> renameGroup(@Path("id") long id, @Body String name,
      @Header("Authorization") String bearerToken);

  @GET("groups/{id}/members")
  Single<List<User>> getMembers(@Path("id") long id, @Header("Authorization") String bearerToken);

  @POST("groups/{groupId}/tasks")
  Single<Task> postTask(@Body Task task, @Path("groupId") long groupId,
      @Header("Authorization") String bearerToken);


  @GET("groups/{groupId}/tasks/{taskId}")
  Single<Task> getTask(@Path("groupId") long groupId, @Path("taskId") long taskId,
      @Header("Authorization") String bearerToken);

  @PUT("groups/{groupId}/tasks/{taskId}")
  Single<Task> putTask(@Path("groupId") long groupId, @Path("taskId") long taskId, @Body Task task,
      @Header("Authorization") String bearerToken);

  @PUT("groups/{groupId}/tasks/{taskId}/members/{memberId}")
  Single<Boolean> putTask(@Body boolean assigned, @Path("groupId") long groupId,
      @Path("taskId") long taskId, @Path("memberId") long memberId,
      @Header("Authorization") String bearerToken);

  @GET("groups/{groupId}/tasks")
  Single<List<Task>> getTasks(@Path("groupId") long groupId,
      @Header("Authorization") String bearerToken);

  @DELETE("groups/{groupId}/tasks/{taskId}")
  Completable deleteTask(@Path("groupId") long groupId, @Path("taskId") long taskId,@Header("Authorization") String bearerToken);

  static WebServiceProxy getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final WebServiceProxy INSTANCE;


    static {
      Gson gson = new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
          .create();
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel((BuildConfig.DEBUG) ? Level.BODY : Level.NONE);
      OkHttpClient client = new OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build();

      Retrofit retrofit = new Retrofit.Builder()
          .addConverterFactory(ScalarsConverterFactory.create())
          .addConverterFactory(GsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(client)
          .baseUrl(BuildConfig.BASE_URL)
          .build();
      INSTANCE = retrofit.create(WebServiceProxy.class);
    }
  }

}
