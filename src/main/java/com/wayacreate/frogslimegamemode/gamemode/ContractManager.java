package com.wayacreate.frogslimegamemode.gamemode;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.achievements.AchievementManager;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ContractManager {
    private static final Map<UUID, List<Contract>> activeContracts = new HashMap<>();
    private static final Map<UUID, Integer> completedContracts = new HashMap<>();
    private static final List<ContractType> contractTypes = new ArrayList<>();
    
    static {
        // Initialize contract types
        contractTypes.add(new ContractType("kill_zombies", "Kill Zombies", "Kill 10 zombies", 10, 50, 100));
        contractTypes.add(new ContractType("kill_skeletons", "Kill Skeletons", "Kill 10 skeletons", 10, 50, 100));
        contractTypes.add(new ContractType("kill_spiders", "Kill Spiders", "Kill 10 spiders", 10, 50, 100));
        contractTypes.add(new ContractType("kill_creepers", "Kill Creepers", "Kill 5 creepers", 5, 75, 150));
        contractTypes.add(new ContractType("mine_diamonds", "Mine Diamonds", "Mine 5 diamonds", 5, 100, 200));
        contractTypes.add(new ContractType("mine_iron", "Mine Iron", "Mine 32 iron ore", 32, 30, 60));
        contractTypes.add(new ContractType("collect_netherite", "Collect Netherite", "Collect 1 netherite ingot", 1, 200, 400));
        contractTypes.add(new ContractType("kill_endermen", "Kill Endermen", "Kill 5 endermen", 5, 80, 160));
        contractTypes.add(new ContractType("kill_blazes", "Kill Blazes", "Kill 10 blazes", 10, 60, 120));
        contractTypes.add(new ContractType("kill_ghasts", "Kill Ghasts", "Kill 3 ghasts", 3, 100, 200));
    }
    
    public static class ContractType {
        private final String id;
        private final String name;
        private final String description;
        private final int targetAmount;
        private final int baseReward;
        private final int maxReward;
        
        public ContractType(String id, String name, String description, int targetAmount, int baseReward, int maxReward) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.targetAmount = targetAmount;
            this.baseReward = baseReward;
            this.maxReward = maxReward;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getTargetAmount() { return targetAmount; }
        public int getBaseReward() { return baseReward; }
        public int getMaxReward() { return maxReward; }
    }
    
    public static class Contract {
        private final ContractType type;
        private final int progress;
        private final int reward;
        private final long expiryTime;
        
        public Contract(ContractType type, int progress, int reward, long expiryTime) {
            this.type = type;
            this.progress = progress;
            this.reward = reward;
            this.expiryTime = expiryTime;
        }
        
        public ContractType getType() { return type; }
        public int getProgress() { return progress; }
        public int getReward() { return reward; }
        public long getExpiryTime() { return expiryTime; }
        
        public boolean isComplete() {
            return progress >= type.getTargetAmount();
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
    
    public static void acceptContract(ServerPlayerEntity player, String contractTypeId) {
        UUID uuid = player.getUuid();
        List<Contract> contracts = activeContracts.computeIfAbsent(uuid, k -> new ArrayList<>());
        
        // Check if player already has this contract type
        for (Contract contract : contracts) {
            if (contract.getType().getId().equals(contractTypeId) && !contract.isComplete()) {
                player.sendMessage(Text.literal("You already have this active contract!")
                    .formatted(Formatting.RED), false);
                return;
            }
        }
        
        // Find contract type
        ContractType type = contractTypes.stream()
            .filter(t -> t.getId().equals(contractTypeId))
            .findFirst()
            .orElse(null);
        
        if (type == null) {
            player.sendMessage(Text.literal("Contract type not found!")
                .formatted(Formatting.RED), false);
            return;
        }
        
        // Calculate random reward between base and max
        int reward = ThreadLocalRandom.current().nextInt(type.getBaseReward(), type.getMaxReward() + 1);
        long expiryTime = System.currentTimeMillis() + (30 * 60 * 1000); // 30 minutes
        
        Contract contract = new Contract(type, 0, reward, expiryTime);
        contracts.add(contract);
        
        player.sendMessage(Text.literal("Contract Accepted: ")
            .formatted(Formatting.GREEN, Formatting.BOLD)
            .append(Text.literal(type.getName())
                .formatted(Formatting.YELLOW)), false);
        player.sendMessage(Text.literal("Goal: " + type.getDescription())
            .formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Reward: " + reward + " coins")
            .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Expires in 30 minutes")
            .formatted(Formatting.GRAY), false);
        
        // Unlock achievement
        int totalCompleted = completedContracts.getOrDefault(uuid, 0);
        if (totalCompleted == 0) {
            AchievementManager.unlockAchievement(player, "first_contract");
        }
    }
    
    public static void updateProgress(ServerPlayerEntity player, String contractTypeId, int amount) {
        UUID uuid = player.getUuid();
        List<Contract> contracts = activeContracts.get(uuid);
        
        if (contracts == null) return;
        
        for (int i = 0; i < contracts.size(); i++) {
            Contract contract = contracts.get(i);
            if (contract.getType().getId().equals(contractTypeId) && !contract.isComplete()) {
                int newProgress = Math.min(contract.getProgress() + amount, contract.getType().getTargetAmount());
                Contract updatedContract = new Contract(
                    contract.getType(),
                    newProgress,
                    contract.getReward(),
                    contract.getExpiryTime()
                );
                contracts.set(i, updatedContract);
                
                if (updatedContract.isComplete()) {
                    completeContract(player, i);
                }
                break;
            }
        }
    }
    
    private static void completeContract(ServerPlayerEntity player, int contractIndex) {
        UUID uuid = player.getUuid();
        List<Contract> contracts = activeContracts.get(uuid);
        
        if (contracts == null || contractIndex >= contracts.size()) return;
        
        Contract contract = contracts.get(contractIndex);
        contracts.remove(contractIndex);
        
        // Add reward to player's balance
        com.wayacreate.frogslimegamemode.economy.EconomyManager.addBalance(player, contract.getReward());
        
        player.sendMessage(Text.literal("Contract Completed: ")
            .formatted(Formatting.GREEN, Formatting.BOLD)
            .append(Text.literal(contract.getType().getName())
                .formatted(Formatting.YELLOW)), false);
        player.sendMessage(Text.literal("Reward: " + contract.getReward() + " coins")
            .formatted(Formatting.GOLD), false);

        TaskManager.completeTask(player, TaskType.COMPLETE_CONTRACT);
        
        // Update completed count
        int totalCompleted = completedContracts.getOrDefault(uuid, 0) + 1;
        completedContracts.put(uuid, totalCompleted);
        
        // Unlock achievements
        if (totalCompleted >= 10) {
            AchievementManager.unlockAchievement(player, "contract_master");
        }
        if (totalCompleted >= 50) {
            AchievementManager.unlockAchievement(player, "contract_legend");
        }
        
        FrogSlimeGamemode.LOGGER.info("Player " + player.getName().getString() + " completed contract: " + contract.getType().getId());
    }
    
    public static void listContracts(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        List<Contract> contracts = activeContracts.get(uuid);
        
        if (contracts == null || contracts.isEmpty()) {
            player.sendMessage(Text.literal("You have no active contracts.")
                .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("Use /frogslime contract accept <type> to accept a contract.")
                .formatted(Formatting.YELLOW), false);
            return;
        }
        
        player.sendMessage(Text.literal("Active Contracts:").formatted(Formatting.BOLD), false);
        for (Contract contract : contracts) {
            if (contract.isExpired()) {
                player.sendMessage(Text.literal("- " + contract.getType().getName() + " (EXPIRED)")
                    .formatted(Formatting.RED, Formatting.STRIKETHROUGH), false);
            } else if (contract.isComplete()) {
                player.sendMessage(Text.literal("- " + contract.getType().getName() + " (COMPLETE - " + contract.getReward() + " coins)")
                    .formatted(Formatting.GREEN), false);
            } else {
                long minutesLeft = (contract.getExpiryTime() - System.currentTimeMillis()) / 60000;
                player.sendMessage(Text.literal("- " + contract.getType().getName() + " (" + contract.getProgress() + "/" + contract.getType().getTargetAmount() + ") - " + minutesLeft + " min left")
                    .formatted(Formatting.YELLOW), false);
            }
        }
    }
    
    public static void listAvailableContracts(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("Available Contracts:").formatted(Formatting.GOLD, Formatting.BOLD), false);
        for (ContractType type : contractTypes) {
            player.sendMessage(Text.literal("- " + type.getName() + " [" + type.getId() + "]")
                .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("  " + type.getDescription())
                .formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("  Reward: " + type.getBaseReward() + "-" + type.getMaxReward() + " coins")
                .formatted(Formatting.GOLD), false);
        }
        player.sendMessage(Text.literal("Use /frogslime contract accept <id> to accept a contract.")
            .formatted(Formatting.YELLOW), false);
    }
    
    public static void clearExpiredContracts(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        List<Contract> contracts = activeContracts.get(uuid);
        
        if (contracts == null) return;
        
        contracts.removeIf(Contract::isExpired);
    }
    
    public static int getCompletedContracts(UUID uuid) {
        return completedContracts.getOrDefault(uuid, 0);
    }
}
