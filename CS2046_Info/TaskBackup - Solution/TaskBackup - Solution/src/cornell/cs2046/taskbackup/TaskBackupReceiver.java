package cornell.cs2046.taskbackup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class TaskBackupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent i) {
        if (Tasks.BROADCAST_PERIODIC_BACKUP.equals(i.getAction())) {
            Intent in = new Intent();
            in.setAction(Tasks.ACTION_BACKUP_TASKS);
            c.startService(in);
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) {
            SharedPreferences prefs = c.getSharedPreferences("tbservice", Context.MODE_PRIVATE);
            long alarmTime = prefs.getLong("alarmTime", -1);
            if (alarmTime != -1) {
                AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                Intent in = new Intent(c, TaskBackupReceiver.class);
                in.setAction(Tasks.BROADCAST_PERIODIC_BACKUP);
                PendingIntent pi = PendingIntent.getBroadcast(c, 0, in, 0);
                mgr.set(AlarmManager.RTC, alarmTime, pi);
            }
        }
    }

}
