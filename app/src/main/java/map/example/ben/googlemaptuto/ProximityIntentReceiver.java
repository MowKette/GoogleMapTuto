package map.example.ben.googlemaptuto;

/**
 * Created by Ben on 19/05/2017.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class ProximityIntentReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;
    NotificationManager notificationManager;
    Notification myNotification;


    @Override
    public void onReceive(Context context, Intent intent) {

        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);

        /*Notification notification = createNotification();
        notification.setLatestEventInfo(context, "Proximity Alert!", "You are near your point of interest.", pendingIntent);


        notificationManager.notify(NOTIFICATION_ID, notification);*/

        Notification.Builder builder = new Notification.Builder(context);

        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("WhatsApp Notification");
        builder.setContentText("You have a new message");
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();

        myNotification = builder.getNotification();
        notificationManager.notify(11, myNotification);

    }

    /*private Notification createNotification() {
        Notification notification = new Notification();

        notification.icon = R.drawable.ic_menu_notifications;
        notification.when = System.currentTimeMillis();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notification.ledARGB = Color.WHITE;
        notification.ledOnMS = 1500;
        notification.ledOffMS = 1500;

        return notification;
    }*/

}

