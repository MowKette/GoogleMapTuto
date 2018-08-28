package map.example.ben.googlemaptuto;

/**
 * Created by Ben on 05/05/2017.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class ProximityActivity extends Activity {

    String notificationTitle;
    String notificationContent;
    String tickerMessage;
    String id = "channel1";
    NotificationCompat.Builder builder;
    PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        boolean proximity_entering = getIntent().getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        Toast.makeText(getBaseContext(), "Test", Toast.LENGTH_LONG).show();
        System.out.print("Test blabla \n");

        double lat = getIntent().getDoubleExtra("lat", 0);

        double lng = getIntent().getDoubleExtra("lng", 0);

        String strLocation = Double.toString(lat)+","+Double.toString(lng);

        if(proximity_entering){
            Toast.makeText(getBaseContext(), "Entering the region", Toast.LENGTH_LONG).show();
            notificationTitle = "Proximity - Entry";
            notificationContent = "Entered the region:" + strLocation;
            tickerMessage = "Entered the region:" + strLocation;
        }else{
            Toast.makeText(getBaseContext(), "Exiting the region", Toast.LENGTH_LONG).show();
            notificationTitle = "Proximity - Exit";
            notificationContent = "Exited the region:" + strLocation;
            tickerMessage = "Exited the region:" + strLocation;
        }

        Intent notificationIntent = new Intent(getApplicationContext(),NotificationView.class);

        /** Adding content to the notificationIntent, which will be displayed on
         * viewing the notification
         */
        notificationIntent.putExtra("content", notificationContent );

        /** This is needed to make this intent different from its previous intents */
        notificationIntent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        /** Creating different tasks for each notification. See the flag Intent.FLAG_ACTIVITY_NEW_TASK */
        //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        /** Getting the System service NotificationManager */
        NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationManagerCompat nManager = NotificationManagerCompat.from(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, "My Notifications", importance);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, notificationTitle, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                nManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);
            //NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);
            //intent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            builder.setContentTitle(notificationTitle)  // required
                    .setSmallIcon(R.drawable.ic_launcher) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(notificationTitle);
        } else {
            builder = new NotificationCompat.Builder(this, id);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            builder.setContentTitle(notificationTitle)                           // required
                    .setSmallIcon(R.drawable.ic_launcher) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(notificationTitle)
                    .setPriority(Notification.PRIORITY_HIGH);
        }

        Notification notification = builder.build();
        nManager.notify(1, notification);

        finish();
    }
}
