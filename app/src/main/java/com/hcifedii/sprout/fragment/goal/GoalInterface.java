package com.hcifedii.sprout.fragment.goal;

/**
 * Classes that implements this interface are required to override at least one of these methods.
 */
public interface GoalInterface {

    default int getInt(){
        throw new UnsupportedOperationException();
    }

    default String getString(){
        throw new UnsupportedOperationException();
    }

}
