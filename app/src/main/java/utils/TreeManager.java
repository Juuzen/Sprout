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
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                result = realm.where(Tree.class).equalTo("id", treeId).findFirst();
            } finally {
                if (realm != null) realm.close();
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

    // adds 1 to the tree's experience
    public static void gainExperience(int treeId) {
        Tree tree = getTree(treeId);
        if (tree != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransactionAsync(
                        realm1 -> {
                            tree.setExperience(tree.getExperience() + 1);
                            realm1.insertOrUpdate(tree);
                        },
                        () -> Log.i(TAG, "Experience Gained! - ID: " + tree.getId()),
                        error -> Log.i(TAG, "Transaction unsuccessful! - ID: " + tree.getId() + "\n" + error.getMessage()));
            }
        }
    }

    public static void setTreeGrowth(int treeId, Tree.Growth growth) {
        Tree tree = getTree(treeId);
        if (tree != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransactionAsync(
                        realm1 -> {
                            tree.setGrowth(growth);
                            tree.setExperience(0);
                            realm1.insertOrUpdate(tree);
                        },
                        () -> Log.i(TAG, "Growth level set! - ID: " + tree.getId()),
                        error -> Log.i(TAG, "Transaction error! - ID: " + tree.getId() + "\n" + error.getMessage()));
            }
        }
    }

    public static void setTreeHealth(int treeId, Tree.Health health) {
        Tree tree = getTree(treeId);
        if (tree != null) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(
                        realm1 -> {
                            tree.setHealth(health);
                            tree.setExperience(0);
                            realm1.insertOrUpdate(tree); },
                        () -> Log.i(TAG, "Health level set! - ID: " + tree.getId()),
                        error -> Log.i(TAG, "Transaction error! - ID: " + tree.getId() + "\n" + error.getMessage()));
            } finally {
                if (realm != null)
                    realm.close();
            }
        }
    }

    private static int getNextId() {
        try (Realm realm = Realm.getDefaultInstance()) {
            Number newId = realm.where(Tree.class).max("id");
            if (newId != null)
                return newId.intValue() + 1;
            return 0;
        }

    }

    public static void grow(int treeId) {
        // Retrieving the tree instance
        Tree tree = getTree(treeId);

        // Retrieving the tree values
        Tree.Growth val = tree.getGrowth();
        int exp = tree.getExperience();

        // Setting up the growth switch case
        switch (val) {
            case SPROUT:
                if (exp >= 1) {
                    TreeManager.setTreeGrowth(tree.getId(), Tree.Growth.SMALL);
                }
                break;
            case SMALL:
                if (exp >= 3) {
                    TreeManager.setTreeGrowth(tree.getId(), Tree.Growth.MEDIUM);
                }
                break;
            case MEDIUM:
                if (exp >= 5) {
                    TreeManager.setTreeGrowth(tree.getId(), Tree.Growth.MATURE);
                }
                break;
            case MATURE:
                if (exp >= 2) {
                    TreeManager.setTreeGrowth(tree.getId(), Tree.Growth.SPARKLING);
                }
                break;
            default:
        }
    }

    public static void normalize(int treeId) {
        Tree tree = getTree(treeId);
        if (tree != null) {
            if (tree.getGrowth() == Tree.Growth.SPARKLING) {
                setTreeGrowth(treeId, Tree.Growth.MATURE);
            }
        }
    }


}
