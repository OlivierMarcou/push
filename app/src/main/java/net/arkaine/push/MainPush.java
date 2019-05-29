package net.arkaine.push;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainPush extends AppCompatActivity {

    public TextView txtView;
    private NotificationReceiver nReceiver;

    /**     * L'AtomicBoolean qui gère la destruction de la Thread de background     */
    AtomicBoolean isRunning = new AtomicBoolean(false);
    /**     * L'AtomicBoolean qui gère la mise en pause de la Thread de background     */
    AtomicBoolean isPausing = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_push);
        txtView = (TextView) findViewById(R.id.txtView);
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("net.arkaine.push.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);onStart();
    }

    public void onStart() {
        super.onStart();
        Thread background = new Thread(new Runnable() {
            // Surcharge de la méthode run
            public void run() {
                try {
                    // Si isRunning est à false, la méthode run doit s'arrêter
                    while ( isRunning.get()) {
                        // Si l'activité est en pause mais pas morte
                        while (isPausing.get() && (isRunning.get())) {
                            // Faire une pause ou un truc qui soulage le CPU (dépend du traitement)
                            Thread.sleep(2000);
                        }
                        Thread.sleep(1000);
                        ((TextView)findViewById(R.id.txtView)).setText(temp);
                    }
                } catch (Throwable t) {
                    // gérer l'exception et arrêter le traitement
                }
            }
        });
        //Initialisation des AtomicBooleans
        isRunning.set(true);
        isPausing.set(false);
        //Lancement de la Thread
        background.start();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    public void buttonClicked(View v){
        if(v.getId() == R.id.goBtn){
            NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
            ncomp.setContentTitle("My Notification");
            ncomp.setContentText("Notification Listener Service Example");
            ncomp.setTicker("Notification Listener Service Example");
            ncomp.setSmallIcon(R.drawable.ic_launcher_background);
            ncomp.setAutoCancel(true);
            nManager.notify((int)System.currentTimeMillis(),ncomp.build());
        }
        else if(v.getId() == R.id.clearBtn){
            Intent i = new Intent("net.arkaine.push.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command","clearall");
            sendBroadcast(i);
        }
        else if(v.getId() == R.id.listBtn){
            Intent i = new Intent("net.arkaine.push.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command","list");
            sendBroadcast(i);
        }
    }
    static String temp ="....";
    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        //    String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText("ici");
        }
    }
}
