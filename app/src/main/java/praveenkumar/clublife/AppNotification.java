package praveenkumar.clublife;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Praveen kumar on 14/08/2015.
 */
public class AppNotification {
    AppNotification(Context from,String title,String message,int id){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(from)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(title)
                        .setContentText(message);
// Creates an explicit intent for an Activity in your app
        //Intent resultIntent = new Intent(this, ResultActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        //stackBuilder.addParentStack(ResultActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        //stackBuilder.addNextIntent(resultIntent);
        //PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        //mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) from.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.

        mNotificationManager.notify(id, mBuilder.build());
    }
}
