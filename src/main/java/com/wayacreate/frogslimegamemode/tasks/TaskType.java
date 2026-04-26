package com.wayacreate.frogslimegamemode.tasks;

public enum TaskType {
    KILL_MOBS(50),
    COLLECT_ITEMS(100),
    REACH_NETHER(1),
    FIND_DIAMONDS(10),
    KILL_BOSS(1);
    
    private final int requiredAmount;
    
    TaskType(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }
    
    public int getRequiredAmount() {
        return requiredAmount;
    }
}
