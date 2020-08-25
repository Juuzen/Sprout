package com.hcifedii.sprout.enumerations;

/**
 * Tree Lifecycle
 * <p>
 * Sprout ------------> Small ------------> Medium ------------> Full Mature
 * ^                     | ^                   | ^                    | ^
 * |                     | |                   | |                    | |
 * |                     V |                   V |                    V |
 * |                   Small (drying)       Medium (drying)      Full Mature (drying)
 * |                     |                     |                      |
 * |                     |                     |                      |
 * |                     V                     V                      V
 * |                   Small (dead)         Medium (dead)        Full Mature (dead)
 * |                     |                     |                      |
 * +---------------------+---------------------+----------------------+
 */

public enum TreeStatus {

    SPROUT,
    SMALL,
    MEDIUM,
    FULL_MATURE,

    SMALL_DRYING,
    SMALL_DEAD,

    MEDIUM_DRYING,
    MEDIUM_DEAD,

    FULL_MATURE_DRYING,
    FULL_MATURE_DEAD

}
