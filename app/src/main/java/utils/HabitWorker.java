package utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class HabitWorker extends Worker {
    public HabitWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        HabitRealmManager.storeHabitListInfo();
        return Result.success(); //FIXME: ricavare l'esito da storeHabitListInfo e restituirlo al worker
    }
}
