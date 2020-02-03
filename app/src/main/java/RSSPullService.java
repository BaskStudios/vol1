import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import edu.itu.csc.quakenweather.R;
import edu.itu.csc.quakenweather.activities.MainActivity;
import edu.itu.csc.quakenweather.adapters.QuakeAdapter;
import edu.itu.csc.quakenweather.models.Quake;

public class RSSPullService extends IntentService {

    private static ArrayAdapter<Quake> quakeAdapter = null;

    public RSSPullService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(RSSPullService.this);
        builder.setContentText("Bildirim");
        builder.setSmallIcon(R.drawable.bell);
        builder.setAutoCancel(true);
        builder.setTicker("Bildirim Geldi");




    }
}
