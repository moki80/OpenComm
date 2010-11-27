package cornell.cs2046.taskbackup;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;

public class TaskBackupService extends Service {
    private Queue<Integer> startIds = new ConcurrentLinkedQueue<Integer>();

    @Override
    public void onStart(Intent intent, int startId) {
        startIds.add(startId);

        if (Tasks.ACTION_BACKUP_TASKS.equals(intent.getAction())) {
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(this, TaskBackupReceiver.class);
            i.setAction(Tasks.BROADCAST_PERIODIC_BACKUP);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            long alarmTime = System.currentTimeMillis() + 24*60*60*1000;
            mgr.set(AlarmManager.RTC, alarmTime, pi);

            SharedPreferences prefs = getSharedPreferences("tbservice", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putLong("alarmTime", alarmTime);
            ed.commit();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... arg0) {
                    String[] projection = new String[] {
                            Tasks.TITLE,
                            Tasks.DETAILS,
                            Tasks.COMPLETED,
                            Tasks.DATE_MODIFIED
                    };
                    Cursor c = getContentResolver().query(Tasks.CONTENT_URI, projection, null, null, null);
                    JSONObject obj = new JSONObject();
                    JSONArray tasks = new JSONArray();
                    try {
                        while (c.moveToNext()) {
                            JSONObject task = new JSONObject();
                            task.put(Tasks.JSON_TITLE, c.getString(0));
                            task.put(Tasks.JSON_DETAILS, c.getString(1));
                            task.put(Tasks.JSON_COMPLETED, c.getInt(2) == 1 ? true : false);
                            task.put(Tasks.JSON_DATE_MODIFIED, c.getLong(3));
                            tasks.put(task);
                        }
                        obj.put(Tasks.JSON_TASK_ARRAY, tasks);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    c.close();

                    List<BasicNameValuePair> args = new LinkedList<BasicNameValuePair>();
                    args.add(new BasicNameValuePair(Tasks.REQUEST_NETID, "jpd236"));
                    args.add(new BasicNameValuePair(Tasks.REQUEST_TASKS, obj.toString()));

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(Tasks.BACKUP_API_URL);
                    try {
                        post.setEntity(new UrlEncodedFormEntity(args));
                        client.execute(post);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    stopSelf(startIds.poll());
                }
            }.execute();

        } else if (Tasks.ACTION_RESTORE_TASKS.equals(intent.getAction())) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    getContentResolver().delete(Tasks.CONTENT_URI, null, null);

                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(Tasks.BACKUP_API_URL + "?"
                            + Tasks.REQUEST_NETID + "=jpd236");
                    try {
                        String ret = EntityUtils.toString(client.execute(get).getEntity());
                        JSONObject obj = (JSONObject) new JSONTokener(ret).nextValue();
                        JSONArray tasks = obj.getJSONArray(Tasks.JSON_TASK_ARRAY);
                        for (int i = 0; i < tasks.length(); i++) {
                            JSONObject task = tasks.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put(Tasks.TITLE, task.getString(Tasks.JSON_TITLE));
                            values.put(Tasks.DETAILS, task.getString(Tasks.JSON_DETAILS));
                            values.put(Tasks.COMPLETED, task.getBoolean(Tasks.JSON_COMPLETED) ? 1 : 0);
                            values.put(Tasks.DATE_MODIFIED, task.getLong(Tasks.JSON_DATE_MODIFIED));
                            getContentResolver().insert(Tasks.CONTENT_URI, values);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    stopSelf(startIds.poll());
                }
            }.execute();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
