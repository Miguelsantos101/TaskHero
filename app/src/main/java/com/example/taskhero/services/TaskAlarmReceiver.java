package com.example.taskhero.services;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.taskhero.R;

public class TaskAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "TaskAlarmReceiver";

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        int taskId = intent.getIntExtra("TASK_ID", 0);
        String taskTitle = intent.getStringExtra("TASK_TITLE");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Log.d(TAG, "Alarm RECEIVED for task ID: " + taskId);

        Notification notification = new NotificationCompat.Builder(context, "TASK_REMINDERS")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text, taskTitle))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(taskId, notification);
    }
}