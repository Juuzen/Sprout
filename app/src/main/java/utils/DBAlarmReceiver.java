package utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcifedii.sprout.MainActivity;
import com.hcifedii.sprout.R;
import com.hcifedii.sprout.adapter.HabitCardAdapter;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Habit;

public class DBAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            /*
             * 1. Richiamare storeHabitListInfo()
             * X. Aggiornare il contenuto dell'adapter (non qui)
             * 2. Ri-settare l'alarm manager per il giorno successivo
             */


            HabitRealmManager.test(context);

        }
    }
}
