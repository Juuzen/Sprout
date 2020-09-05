package model;

import io.realm.RealmObject;
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

    @PrimaryKey
    public int id;
    public String growth;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String health;
    public int experience;
    //TODO: inverse relationship habit <- tree

    public Growth getGrowth() {
        return Growth.valueOf(this.growth);
    }

    public void setGrowth(Growth val) {
        this.growth = val.toString();
    }

    public Health getHealth() {
        return Health.valueOf(this.health);
    }

    public void setHealth(Health val) {
        this.health = val.toString();
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
