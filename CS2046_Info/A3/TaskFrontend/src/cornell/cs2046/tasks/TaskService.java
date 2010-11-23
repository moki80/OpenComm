package cornell.cs2046.tasks;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TaskService extends Service {
    Queue<Integer> q = new ConcurrentLinkedQueue<Integer>();

    @Override
    public void onStart(Intent intent, int startId) {
        q.add(startId);
        if (Tasks.ACTION_DELETE_OLD.equals(intent.getAction())) {
            new Thread() {
                @Override
                public void run() {
                    String where = Tasks.COMPLETED + "=1 and datetime(" + Tasks.DATE_MODIFIED +
                            "/1000, 'unixepoch') < datetime('now', '-7 days')";
                    getContentResolver().delete(Tasks.CONTENT_URI, where, null);
                    stopSelf(q.poll());
                }
            }.start();

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
