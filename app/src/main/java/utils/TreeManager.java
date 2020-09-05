package utils;

import android.util.Log;

import io.realm.Realm;
import model.Tree;


enum Health {
    HEALTHY,
    DRYING,
    WITHERED
}

public class TreeManager {
    private static final String LOG_TAG = "DBTreeManager";

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

    // adds 1 to the tree's experience
    public static void gainExperience(int treeId) {
        Tree tree = getTree(treeId);
        if (tree != null) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(
                        realm1 -> {
                    tree.setExperience(tree.getExperience() + 1);
                    realm1.insertOrUpdate(tree); },

                        () -> Log.i(LOG_TAG, "Experience Gained! - ID: " + tree.getId()),

                        error -> Log.i(LOG_TAG, "Transaction unsuccessful! - ID: " + tree.getId() + "\n" + error.getMessage()));
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }
    }

    public static void setTreeGrowth(int treeId, Tree.Growth growth) {
        Tree tree = getTree(treeId);
        if (tree != null) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(
                        realm1 -> {
                    tree.setGrowth(growth);
                    tree.setExperience(0);
                    realm1.insertOrUpdate(tree); },

                        () -> Log.i(LOG_TAG, "Growth level set! - ID: " + tree.getId()),

                        error -> Log.i(LOG_TAG, "Transaction error! - ID: " + tree.getId() + "\n" + error.getMessage()));
            } finally {
                if (realm != null)
                    realm.close();
            }
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

    private static int getNextId(Realm realm) {
        Number newId = realm.where(Tree.class).max("id");
        if (newId != null)
            return newId.intValue() + 1;
        return 0;
    }
}
