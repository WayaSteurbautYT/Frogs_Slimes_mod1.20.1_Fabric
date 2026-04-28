package com.wayacreate.frogslimegamemode.screen;

import com.wayacreate.frogslimegamemode.guild.Guild;
import com.wayacreate.frogslimegamemode.guild.GuildManager;
import com.wayacreate.frogslimegamemode.guild.GuildMission;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildMissionScreenHandler extends AbstractContainerMenu {
    private static final int ROWS = 6;
    private static final int TOP_SIZE = ROWS * 9;

    private final ServerPlayer player;
    private final SimpleContainer inventory;
    private final Map<Integer, GuildMission> displayedMissions = new HashMap<>();

    public GuildMissionScreenHandler(int syncId, Inventory playerInventory, ServerPlayer player) {
        super(MenuType.GENERIC_9X6, syncId);
        this.player = player;
        this.inventory = new SimpleContainer(TOP_SIZE);

        buildSlots(playerInventory);
        refreshMissions();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, ClickType actionType, Player player) {
        if (slotIndex >= 0 && slotIndex < TOP_SIZE) {
            if (player instanceof ServerPlayer serverPlayer) {
                GuildMission mission = displayedMissions.get(slotIndex);
                if (mission != null) {
                    GuildManager.contributeToMissionFromInventory(serverPlayer, mission.getId());
                }
                refreshMissions();
                sendContentUpdates();
            }
            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    private void buildSlots(Inventory playerInventory) {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < 9; column++) {
                int slotIndex = column + row * 9;
                this.addSlot(new DisplaySlot(inventory, slotIndex, 8 + column * 18, 18 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9,
                    8 + column * 18, 140 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 198));
        }
    }

    private void refreshMissions() {
        displayedMissions.clear();
        for (int i = 0; i < TOP_SIZE; i++) {
            inventory.setStack(i, ItemStack.EMPTY);
        }

        Guild guild = GuildManager.getPlayerGuild(player);
        if (guild == null) {
            return;
        }

        List<GuildMission> missions = guild.getMissions().stream()
            .filter(GuildMission::isActive)
            .toList();

        int limit = Math.min(missions.size(), TOP_SIZE);
        for (int i = 0; i < limit; i++) {
            GuildMission mission = missions.get(i);
            ItemStack icon = mission.getRequiredItems().isEmpty()
                ? new ItemStack(Items.PAPER)
                : mission.getRequiredItems().get(0);

            ChatFormatting color = mission.isCompletedBy(player.getUuid()) ? ChatFormatting.GREEN : ChatFormatting.GOLD;
            String rewardText = mission.getCoinReward() > 0 ? " - " + mission.getCoinReward() + "c" : "";
            icon.setCustomName(Component.literal(mission.getName() + rewardText)
                .formatted(color, ChatFormatting.BOLD));

            inventory.setStack(i, icon);
            displayedMissions.put(i, mission);
        }
    }

    private static final class DisplaySlot extends Slot {
        private DisplaySlot(SimpleContainer inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeItems(Player playerEntity) {
            return false;
        }
    }
}
