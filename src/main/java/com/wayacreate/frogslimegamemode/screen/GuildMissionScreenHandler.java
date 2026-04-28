package com.wayacreate.frogslimegamemode.screen;

import com.wayacreate.frogslimegamemode.guild.Guild;
import com.wayacreate.frogslimegamemode.guild.GuildManager;
import com.wayacreate.frogslimegamemode.guild.GuildMission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildMissionScreenHandler extends ScreenHandler {
    private static final int ROWS = 6;
    private static final int TOP_SIZE = ROWS * 9;

    private final ServerPlayerEntity player;
    private final SimpleInventory inventory;
    private final Map<Integer, GuildMission> displayedMissions = new HashMap<>();

    public GuildMissionScreenHandler(int syncId, PlayerInventory playerInventory, ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, syncId);
        this.player = player;
        this.inventory = new SimpleInventory(TOP_SIZE);

        buildSlots(playerInventory);
        refreshMissions();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < TOP_SIZE) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
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
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    private void buildSlots(PlayerInventory playerInventory) {
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

            Formatting color = mission.isCompletedBy(player.getUuid()) ? Formatting.GREEN : Formatting.GOLD;
            String rewardText = mission.getCoinReward() > 0 ? " - " + mission.getCoinReward() + "c" : "";
            icon.setCustomName(Text.literal(mission.getName() + rewardText)
                .formatted(color, Formatting.BOLD));

            inventory.setStack(i, icon);
            displayedMissions.put(i, mission);
        }
    }

    private static final class DisplaySlot extends Slot {
        private DisplaySlot(SimpleInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return false;
        }
    }
}
