package utils;

import com.hcifedii.sprout.enumerations.TreeStatus;

import io.realm.RealmObject;

public class TreeStatusEnum extends RealmObject {
    private String treeStatus = TreeStatus.SPROUT.name();

    public void saveStatus(TreeStatus val) {
        this.treeStatus = val.toString();
    }

    public TreeStatus getEnum() {
        return TreeStatus.valueOf(treeStatus);
    }
}
