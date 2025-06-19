package com.example.taskhero.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.taskhero.R;
import com.example.taskhero.data.model.Task;
import com.example.taskhero.receivers.TaskAlarmReceiver;

import java.util.Date;

public class NotificationScheduler {

    private static final String CHANNEL_ID = "TASK_REMINDERS";
    private static final String ACTION_TASK_REMINDER = "com.example.taskhero.ACTION_TASK_REMINDER";
    private static final String TAG = "NotificationScheduler";

    public static void createNotificationChannel(@NonNull Context context) {
        CharSequence name = context.getString(R.string.notification_channel_name);
        String description = context.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void scheduleTaskReminder(Context context, @NonNull Task task) {
        if (task.getDueDate() <= System.currentTimeMillis()) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, TaskAlarmReceiver.class);
        intent.setAction(ACTION_TASK_REMINDER);
        intent.setData(Uri.parse("taskhero://task/" + task.getId()));
        intent.putExtra("TASK_ID", task.getId());
        intent.putExtra("TASK_TITLE", task.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getDueDate(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDueDate(), pendingIntent);
        }

        Log.d(TAG, "Alarm SCHEDULED for task ID: " + task.getId() + " at " + new Date(task.getDueDate()));
    }

    public static void cancelTaskReminder(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, TaskAlarmReceiver.class);
        intent.setAction(ACTION_TASK_REMINDER);
        intent.setData(Uri.parse("taskhero://task/" + task.getId()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.getId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();

            Log.d(TAG, "Alarm CANCELED for task ID: " + task.getId());
        }
    }
}