package utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hcifedii.sprout.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import model.Tree;

public class TreeRealmManager {
    private static final String TAG = "TreeManager";

    public static Tree getTree(int treeId) {

        if (treeId >= 0) {
            try (Realm realm = Realm.getDefaultInstance()) {
                Tree check = realm.where(Tree.class).equalTo("id", treeId).findFirst();
                if (check != null) {
                    return realm.copyFromRealm(check);
                }
            }
        }
        return null;
    }

    public static ArrayList<Tree> getAllTrees() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Tree> trees = realm.where(Tree.class).findAll();
            return new ArrayList<>(trees);
        }
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

    public static void insertTree(@NonNull Tree tree) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(realmInstance -> realmInstance.insertOrUpdate(tree));
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


    public static int getTreeImageResourceId(@NonNull Tree tree) {

        Tree.Health health = tree.getHealth();

        switch (tree.getGrowth()) {
            default:
                return R.drawable.svg_tree_sprout;
            case SMALL:
                if (health == Tree.Health.HEALTHY)
                    return R.drawable.svg_tree_small;
                else if (health == Tree.Health.WITHERED)
                    return R.drawable.svg_tree_small_withered;
                else
                    return R.drawable.svg_tree_small_drying;

            case MEDIUM:
                if (health == Tree.Health.HEALTHY)
                    return R.drawable.svg_tree_medium;
                else if (health == Tree.Health.WITHERED)
                    return R.drawable.svg_tree_medium_withered;
                else
                    return R.drawable.svg_tree_medium_drying;

            case MATURE:
                if (health == Tree.Health.HEALTHY)
                    return R.drawable.svg_tree_full_mature;
                else if (health == Tree.Health.WITHERED)
                    return R.drawable.svg_tree_full_mature_withered;
                else
                    return R.drawable.svg_tree_full_mature_drying;

            case SPARKLING:
                return R.drawable.svg_tree_full_mature_sparkle;

        }
    }

    public static int getSkyResourceId(@NonNull Tree.Health health) {
        if (health == Tree.Health.HEALTHY)
            return R.drawable.sky;
        else if (health == Tree.Health.WITHERED)
            return R.drawable.sky_withered;
        else
            return R.drawable.sky_drying;
    }

}
