package net.simplifiedcoding.androidmysqlsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Belal on 1/27/2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;
    Api api;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveName(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
                        );
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    /*
    * method taking two arguments
    * name that is to be saved and id of the name from SQLite
    * if the name is successfully sent
    * we will update the status as synced in SQLite
    * */
    private void saveName(final int id, final String name) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api=retrofit.create(Api.class);

        Call<Demo> demoCall=api.getAllResponse(name);
        demoCall.enqueue(new Callback<Demo>() {
            @Override
            public void onResponse(Call<Demo> call, retrofit2.Response<Demo> response) {
                if (response.isSuccessful())
                {
                    db.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER);
                    Log.e("error","OK0");
                    Toast.makeText(context.getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();

                    //sending the broadcast to refresh the list
                    context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                }
            }

            @Override
            public void onFailure(Call<Demo> call, Throwable t) {

            }
        });

    }

}
