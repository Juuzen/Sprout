package utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hcifedii.sprout.BuildConfig;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Task;
import model.Tree;

public class TreeManager {
    private static final String TAG = "TreeManager";

    public static Tree getTree(int treeId) {
        Tree result = null;
        if (treeId >= 0) {
            try (Realm realm = Realm.getDefaultInstance()) {
                Tree check = realm.where(Tree.class).equalTo("id", treeId).findFirst();
                if (check != null) {
                    result = realm.copyFromRealm(check);
                }
            }
        }
        return result;
    }

    public static void insertTree(@NonNull Tree tree) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(
                    realmInstance -> realmInstance.insertOrUpdate(tree)
            );
        }
    }

    public static ArrayList<Tree> getAllTrees() {
        Realm realm = null;
        ArrayList<Tree> list = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Tree> trees = realm.where(Tree.class).findAll();
            list = new ArrayList<>(trees);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }

    public static void setTreeGrowth(Tree tree, Tree.Growth growth) {
        if (tree != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransactionAsync(
                        realm1 -> {
                            tree.setGrowth(growth);
                            realm1.insertOrUpdate(tree);
                        },
                        () -> Log.i(TAG, "Growth level set! - ID: " + tree.getId()),
                        error -> Log.i(TAG, "Transaction error! - ID: " + tree.getId() + "\n" + error.getMessage()));
            }
        }
    }

    public static void setTreeHealth(Tree tree, Tree.Health health) {
        if (tree != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransactionAsync(
                        realm1 -> {
                            tree.setHealth(health);
                            realm1.insertOrUpdate(tree);
                        },
                        () -> Log.i(TAG, "Health level set! - ID: " + tree.getId()),
                        error -> Log.i(TAG, "Transaction error! - ID: " + tree.getId() + "\n" + error.getMessage()));
            }
        }
    }

    public static void setTreeExperience(Tree tree, int experience) {
        if (tree != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransactionAsync(
                        realm1 -> {
                            tree.setExperience(experience);
                            realm1.insertOrUpdate(tree);
                        },
                        () -> Log.i(TAG, "Experience set! - ID: " + tree.getId()),
                        error -> Log.i(TAG, "Transaction error! - ID: " + tree.getId() + "\n" + error.getMessage()));
            }
        }
    }

    public static int getNextId() {
        try (Realm realm = Realm.getDefaultInstance()) {
            Number newId = realm.where(Tree.class).max("id");
            if (newId != null)
                return newId.intValue() + 1;
            return 0;
        }

    }

    public static int getRequiredExperience(Tree.Growth growth) {
        int result;
        switch (growth) {
            case SPROUT:
                result = 1;
                break;
            case SMALL:
                result = 3;
                break;
            case MEDIUM:
                result = 5;
                break;
            case MATURE:
                result = 2;
                break;
            default:
                result = -1;
        }
        return result;
    }

    public static Tree.Growth getNextGrowthStep(Tree.Growth growth) {
        Tree.Growth result;
        switch (growth) {
            case SPROUT:
                result = Tree.Growth.SMALL;
                break;
            case SMALL:
                result = Tree.Growth.MEDIUM;
                break;
            case MEDIUM:
                result = Tree.Growth.MATURE;
                break;
            case MATURE:
                result = Tree.Growth.SPARKLING;
                break;
            default:
                result = growth;
        }
        return result;
    }
}
