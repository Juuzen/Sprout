package model;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Tree extends RealmObject {
    public enum Growth {
        SPROUT,
        SMALL,
        MEDIUM,
        MATURE,
        SPARKLING
    }

    public enum Health {
        HEALTHY,
        DRYING,
        WITHERED
    }

    public Tree() {
        this.growth = Growth.SPROUT.name();
        this.health = Health.HEALTHY.name();
        this.experience = 0;
    }

    @PrimaryKey
    private int id;
    private String growth;
    private String health;
    private int experience;

    // Inverse relationship (to retrieve the habit that holds the tree instance)
    //@LinkingObjects("tree")
    //private final RealmResults<Habit> habit = null;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Growth getGrowth() {
        return Growth.valueOf(this.growth);
    }

    public void setGrowth(Growth val) {
        this.growth = val.name();
    }

    public Health getHealth() {
        return Health.valueOf(this.health);
    }

    public void setHealth(Health val) {
        this.health = val.name();
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    //public RealmResults<Habit> getHabit() { return habit; }
}
