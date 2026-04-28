package com.wayacreate.frogslimegamemode.network;

import com.wayacreate.frogslimegamemode.FrogSlimeGamemode;
import com.wayacreate.frogslimegamemode.abilities.PlayerAbilityManager;
import com.wayacreate.frogslimegamemode.achievements.Achievement;
import com.wayacreate.frogslimegamemode.eating.MobAbility;
import com.wayacreate.frogslimegamemode.gamemode.GamemodeManager;
import com.wayacreate.frogslimegamemode.gamemode.PlayerLevel;
import com.wayacreate.frogslimegamemode.item.ModItems;
import com.wayacreate.frogslimegamemode.progression.ProgressionUnlock;
import com.wayacreate.frogslimegamemode.tasks.TaskManager;
import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetworking::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToServer(UseAbilityPayload.ID, UseAbilityPayload.CODEC, ModNetworking::handleUseAbility);
        registrar.playToServer(SwitchAbilityPayload.ID, SwitchAbilityPayload.CODEC, ModNetworking::handleSwitchAbility);
        registrar.playToServer(ConsumeAbilityItemPayload.ID, ConsumeAbilityItemPayload.CODEC, ModNetworking::handleConsumeAbilityItem);
        registrar.playToServer(HunterTrackPayload.ID, HunterTrackPayload.CODEC, ModNetworking::handleHunterTrack);
        registrar.playToServer(HunterBlockPayload.ID, HunterBlockPayload.CODEC, ModNetworking::handleHunterBlock);
        registrar.playToServer(HunterSlowPayload.ID, HunterSlowPayload.CODEC, ModNetworking::handleHunterSlow);
        registrar.playToServer(SpeedrunnerEscapePayload.ID, SpeedrunnerEscapePayload.CODEC, ModNetworking::handleSpeedrunnerEscape);
        registrar.playToServer(SpeedrunnerSpeedPayload.ID, SpeedrunnerSpeedPayload.CODEC, ModNetworking::handleSpeedrunnerSpeed);
        registrar.playToServer(SpeedrunnerInvisPayload.ID, SpeedrunnerInvisPayload.CODEC, ModNetworking::handleSpeedrunnerInvis);
        registrar.playToServer(RequestProgressSnapshotPayload.ID, RequestProgressSnapshotPayload.CODEC, ModNetworking::handleRequestProgressSnapshot);
    }

    public static void syncGamemodeStatus(ServerPlayer player, boolean active) {
        send(player, new GamemodeStatusPayload(active));
    }

    public static void openTasksScreen(ServerPlayer player) {
        sendProgressSnapshot(player);
        send(player, OpenTasksScreenPayload.INSTANCE);
    }

    public static void showTitle(ServerPlayer player, String title, String subtitle, ChatFormatting color) {
        int colorIndex = color.getColorIndex();
        send(player, new ShowTitlePayload(title, subtitle, colorIndex < 0 ? 15 : colorIndex));
    }

    public static void sendAchievementToast(ServerPlayer player, Achievement achievement) {
        int colorIndex = achievement.getColor().getColorIndex();
        send(player, new AchievementToastPayload(
            achievement.getName(),
            achievement.getDescription(),
            new ItemStack(getAchievementIcon(achievement.getId())),
            colorIndex < 0 ? 15 : colorIndex
        ));
    }

    public static void sendTotemAnimation(ServerPlayer player, String title, String subtitle, ChatFormatting color, Item item) {
        int colorIndex = color.getColorIndex();
        send(player, new TotemAnimationPayload(
            title,
            subtitle,
            colorIndex < 0 ? 15 : colorIndex,
            new ItemStack(item)
        ));
    }

    public static void sendTotemAnimation(ServerPlayer player, String title, String subtitle, ChatFormatting color) {
        sendTotemAnimation(player, title, subtitle, color, Items.TOTEM_OF_UNDYING);
    }

    public static void sendPlayerTongueAnimation(ServerPlayer player, int targetEntityId) {
        send(player, new PlayerTongueAnimationPayload(targetEntityId));
    }

    public static void sendManhuntHudUpdate(ServerPlayer player, String elapsedTime, int deathCount) {
        sendManhuntHudUpdate(player, false, "", elapsedTime, deathCount, "", 0, "Track", "Reveal your target.", 0, 0, 0, 0, 0, 0);
    }

    public static void sendManhuntHudUpdate(
        ServerPlayer player,
        boolean active,
        String role,
        String elapsedTime,
        int deathCount,
        String targetName,
        int selectedIndex,
        String selectedAbilityName,
        String selectedAbilityDescription,
        int hunterTrackCd,
        int hunterBlockCd,
        int hunterSlowCd,
        int speedrunnerEscapeCd,
        int speedrunnerSpeedCd,
        int speedrunnerInvisCd
    ) {
        send(player, new ManhuntHudUpdatePayload(
            active,
            role,
            elapsedTime,
            deathCount,
            targetName,
            selectedIndex,
            selectedAbilityName,
            selectedAbilityDescription,
            hunterTrackCd,
            hunterBlockCd,
            hunterSlowCd,
            speedrunnerEscapeCd,
            speedrunnerSpeedCd,
            speedrunnerInvisCd
        ));
    }

    public static void clearManhuntHud(ServerPlayer player) {
        sendManhuntHudUpdate(player, false, "", "0:00", 0, "", 0, "Track", "Reveal your target.", 0, 0, 0, 0, 0, 0);
    }

    public static void sendProgressSnapshot(ServerPlayer player) {
        boolean active = GamemodeManager.isInGamemode(player);
        if (!active) {
            send(player, new ProgressionSnapshotPayload(
                false,
                1,
                0.0,
                100.0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                "No Ability",
                "Enable the gamemode to begin.",
                buildTaskSnapshot(player),
                List.of(),
                List.of()
            ));
            return;
        }

        int level = PlayerLevel.getLevel(player);
        double xp = PlayerLevel.getXP(player);
        double xpToNext = PlayerLevel.getXPToNextLevel(player);
        int highestEvolutionStage = TaskManager.getHighestHelperEvolution(player);
        MobAbility currentAbility = PlayerAbilityManager.getCurrentAbility(player);
        var data = GamemodeManager.getData(player);

        List<ProgressionUnlock.Unlock> unlocks = ProgressionUnlock.getUnlocksForLevel(level);
        List<ProgressionUnlock.Unlock> upcoming = new ArrayList<>();
        for (ProgressionUnlock.Unlock unlock : unlocks) {
            if (!ProgressionUnlock.isUnlocked(unlock.getId(), level - 1, highestEvolutionStage)) {
                upcoming.add(unlock);
            }
        }

        int writeCount = Math.min(3, upcoming.size());
        List<String> unlockNames = new ArrayList<>(writeCount);
        List<String> unlockDescriptions = new ArrayList<>(writeCount);
        for (int i = 0; i < writeCount; i++) {
            ProgressionUnlock.Unlock unlock = upcoming.get(i);
            unlockNames.add(unlock.getName());
            unlockDescriptions.add(unlock.getDescription());
        }

        send(player, new ProgressionSnapshotPayload(
            true,
            level,
            xp,
            xpToNext,
            data.getHelpersSpawned(),
            data.getMobsEaten(),
            data.getItemsCollected(),
            data.getDeathCount(),
            data.getJumpCount(),
            data.getPlayerAbilities().size(),
            highestEvolutionStage,
            com.wayacreate.frogslimegamemode.achievements.AchievementManager.getPlayerAchievements(player.getUuid()).size(),
            currentAbility != null ? currentAbility.getName() : "Tongue Grab",
            currentAbility != null ? currentAbility.getDescription() : "Quick strikes with your frog tongue.",
            buildTaskSnapshot(player),
            unlockNames,
            unlockDescriptions
        ));
    }

    private static EnumMap<TaskType, Integer> buildTaskSnapshot(ServerPlayer player) {
        EnumMap<TaskType, Integer> taskProgress = new EnumMap<>(TaskType.class);
        for (TaskType task : TaskType.values()) {
            int progress = GamemodeManager.isInGamemode(player)
                ? GamemodeManager.getData(player).getTaskProgress(task)
                : 0;
            taskProgress.put(task, progress);
        }
        return taskProgress;
    }

    private static Item getAchievementIcon(String achievementId) {
        return switch (achievementId) {
            case "journey_started" -> Items.BOOK;
            case "first_helper" -> ModItems.FROG_HELPER_SPAWN_EGG;
            case "helper_commander" -> ModItems.COMBAT_ROLE;
            case "first_evolution", "elite_helper", "master_helper" -> ModItems.EVOLUTION_STONE;
            case "mob_smith" -> ModItems.MOB_ABILITY;
            case "final_form", "boss_killer", "dragon_slayer" -> ModItems.FINAL_EVOLUTION_CRYSTAL;
            case "first_trade", "merchant", "trade_tycoon" -> Items.EMERALD;
            case "first_contract", "contract_master", "contract_legend" -> Items.PAPER;
            case "ability_unlock", "ability_master" -> ModItems.ABILITY_DROP;
            default -> ModItems.TASK_BOOK != null ? ModItems.TASK_BOOK : Items.BOOK;
        };
    }

    private static void send(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    private static ServerPlayer getPlayer(IPayloadContext context) {
        return (ServerPlayer) context.player();
    }

    private static void handleUseAbility(UseAbilityPayload payload, IPayloadContext context) {
        ServerPlayer player = getPlayer(context);
        if (!com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useContextualAbility(player)) {
            PlayerAbilityManager.useCurrentAbility(player);
        }
    }

    private static void handleSwitchAbility(SwitchAbilityPayload payload, IPayloadContext context) {
        ServerPlayer player = getPlayer(context);
        if (!com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.cycleContextualAbility(player)) {
            PlayerAbilityManager.switchToNextAbility(player);
        }
    }

    private static void handleConsumeAbilityItem(ConsumeAbilityItemPayload payload, IPayloadContext context) {
        com.wayacreate.frogslimegamemode.item.MobAbilityItem.consumeHeldAbilityItem(getPlayer(context));
    }

    private static void handleHunterTrack(HunterTrackPayload payload, IPayloadContext context) {
        com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useHunterTrackAbility(getPlayer(context));
    }

    private static void handleHunterBlock(HunterBlockPayload payload, IPayloadContext context) {
        com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useHunterBlockAbility(getPlayer(context));
    }

    private static void handleHunterSlow(HunterSlowPayload payload, IPayloadContext context) {
        com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useHunterSlowAbility(getPlayer(context));
    }

    private static void handleSpeedrunnerEscape(SpeedrunnerEscapePayload payload, IPayloadContext context) {
        com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useSpeedrunnerEscapeAbility(getPlayer(context));
    }

    private static void handleSpeedrunnerSpeed(SpeedrunnerSpeedPayload payload, IPayloadContext context) {
        com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useSpeedrunnerSpeedAbility(getPlayer(context));
    }

    private static void handleSpeedrunnerInvis(SpeedrunnerInvisPayload payload, IPayloadContext context) {
        com.wayacreate.frogslimegamemode.gamemode.ManhuntManager.useSpeedrunnerInvisAbility(getPlayer(context));
    }

    private static void handleRequestProgressSnapshot(RequestProgressSnapshotPayload payload, IPayloadContext context) {
        sendProgressSnapshot(getPlayer(context));
    }

    public record GamemodeStatusPayload(boolean active) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<GamemodeStatusPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "gamemode_status"));
        public static final StreamCodec<RegistryFriendlyByteBuf, GamemodeStatusPayload> CODEC = StreamCodec.of(GamemodeStatusPayload::write, GamemodeStatusPayload::new);

        private GamemodeStatusPayload(RegistryFriendlyByteBuf buf) {
            this(buf.readBoolean());
        }

        private void write(RegistryFriendlyByteBuf buf) {
            buf.writeBoolean(active);
        }

        @Override
        public CustomPacketPayload.Type<GamemodeStatusPayload> type() {
            return ID;
        }
    }

    public record OpenTasksScreenPayload() implements CustomPacketPayload {
        public static final OpenTasksScreenPayload INSTANCE = new OpenTasksScreenPayload();
        public static final CustomPacketPayload.Type<OpenTasksScreenPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "open_tasks_screen"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenTasksScreenPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<OpenTasksScreenPayload> type() {
            return ID;
        }
    }

    public record ShowTitlePayload(String title, String subtitle, int color) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ShowTitlePayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "show_title"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ShowTitlePayload> CODEC = StreamCodec.of(ShowTitlePayload::write, ShowTitlePayload::new);

        private ShowTitlePayload(RegistryFriendlyByteBuf buf) {
            this(buf.readString(), buf.readString(), buf.readInt());
        }

        private void write(RegistryFriendlyByteBuf buf) {
            buf.writeString(title);
            buf.writeString(subtitle);
            buf.writeInt(color);
        }

        @Override
        public CustomPacketPayload.Type<ShowTitlePayload> type() {
            return ID;
        }
    }

    public record AchievementToastPayload(String name, String description, ItemStack iconStack, int color) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<AchievementToastPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "achievement_toast"));
        public static final StreamCodec<RegistryFriendlyByteBuf, AchievementToastPayload> CODEC = StreamCodec.of(AchievementToastPayload::write, AchievementToastPayload::new);

        private AchievementToastPayload(RegistryFriendlyByteBuf buf) {
            this(buf.readString(), buf.readString(), ItemStack.PACKET_CODEC.decode(buf), buf.readInt());
        }

        private void write(RegistryFriendlyByteBuf buf) {
            buf.writeString(name);
            buf.writeString(description);
            ItemStack.PACKET_CODEC.encode(buf, iconStack);
            buf.writeInt(color);
        }

        @Override
        public CustomPacketPayload.Type<AchievementToastPayload> type() {
            return ID;
        }
    }

    public record TotemAnimationPayload(String title, String subtitle, int color, ItemStack itemStack) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<TotemAnimationPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "totem_animation"));
        public static final StreamCodec<RegistryFriendlyByteBuf, TotemAnimationPayload> CODEC = StreamCodec.of(TotemAnimationPayload::write, TotemAnimationPayload::new);

        private TotemAnimationPayload(RegistryFriendlyByteBuf buf) {
            this(buf.readString(), buf.readString(), buf.readInt(), ItemStack.PACKET_CODEC.decode(buf));
        }

        private void write(RegistryFriendlyByteBuf buf) {
            buf.writeString(title);
            buf.writeString(subtitle);
            buf.writeInt(color);
            ItemStack.PACKET_CODEC.encode(buf, itemStack);
        }

        @Override
        public CustomPacketPayload.Type<TotemAnimationPayload> type() {
            return ID;
        }
    }

    public record PlayerTongueAnimationPayload(int targetEntityId) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<PlayerTongueAnimationPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "player_tongue_animation"));
        public static final StreamCodec<RegistryFriendlyByteBuf, PlayerTongueAnimationPayload> CODEC = StreamCodec.of(PlayerTongueAnimationPayload::write, PlayerTongueAnimationPayload::new);

        private PlayerTongueAnimationPayload(RegistryFriendlyByteBuf buf) {
            this(buf.readInt());
        }

        private void write(RegistryFriendlyByteBuf buf) {
            buf.writeInt(targetEntityId);
        }

        @Override
        public CustomPacketPayload.Type<PlayerTongueAnimationPayload> type() {
            return ID;
        }
    }

    public record ManhuntHudUpdatePayload(
        boolean active,
        String role,
        String elapsedTime,
        int deathCount,
        String targetName,
        int selectedIndex,
        String selectedAbilityName,
        String selectedAbilityDescription,
        int hunterTrackCd,
        int hunterBlockCd,
        int hunterSlowCd,
        int speedrunnerEscapeCd,
        int speedrunnerSpeedCd,
        int speedrunnerInvisCd
    ) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ManhuntHudUpdatePayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "manhunt_hud_update"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ManhuntHudUpdatePayload> CODEC = StreamCodec.of(ManhuntHudUpdatePayload::write, ManhuntHudUpdatePayload::new);

        private ManhuntHudUpdatePayload(RegistryFriendlyByteBuf buf) {
            this(
                buf.readBoolean(),
                buf.readString(),
                buf.readString(),
                buf.readInt(),
                buf.readString(),
                buf.readInt(),
                buf.readString(),
                buf.readString(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
            );
        }

        private void write(RegistryFriendlyByteBuf buf) {
            buf.writeBoolean(active);
            buf.writeString(role);
            buf.writeString(elapsedTime);
            buf.writeInt(deathCount);
            buf.writeString(targetName);
            buf.writeInt(selectedIndex);
            buf.writeString(selectedAbilityName);
            buf.writeString(selectedAbilityDescription);
            buf.writeInt(hunterTrackCd);
            buf.writeInt(hunterBlockCd);
            buf.writeInt(hunterSlowCd);
            buf.writeInt(speedrunnerEscapeCd);
            buf.writeInt(speedrunnerSpeedCd);
            buf.writeInt(speedrunnerInvisCd);
        }

        @Override
        public CustomPacketPayload.Type<ManhuntHudUpdatePayload> type() {
            return ID;
        }
    }

    public record ProgressionSnapshotPayload(
        boolean active,
        int level,
        double xp,
        double xpToNext,
        int helpersSpawned,
        int mobsEaten,
        int itemsCollected,
        int deaths,
        int jumps,
        int abilityCount,
        int highestEvolutionStage,
        int achievementCount,
        String selectedAbilityName,
        String selectedAbilityDescription,
        EnumMap<TaskType, Integer> taskProgress,
        List<String> unlockNames,
        List<String> unlockDescriptions
    ) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ProgressionSnapshotPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "progression_snapshot"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ProgressionSnapshotPayload> CODEC = StreamCodec.of(ProgressionSnapshotPayload::write, ProgressionSnapshotPayload::new);

        private ProgressionSnapshotPayload(RegistryFriendlyByteBuf buf) {
            this(
                buf.readBoolean(),
                buf.readInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readString(),
                buf.readString(),
                readTaskProgress(buf),
                readStringList(buf),
                readStringList(buf)
            );
        }

        private void write(RegistryFriendlyByteBuf buf) {
            buf.writeBoolean(active);
            buf.writeInt(level);
            buf.writeDouble(xp);
            buf.writeDouble(xpToNext);
            buf.writeInt(helpersSpawned);
            buf.writeInt(mobsEaten);
            buf.writeInt(itemsCollected);
            buf.writeInt(deaths);
            buf.writeInt(jumps);
            buf.writeInt(abilityCount);
            buf.writeInt(highestEvolutionStage);
            buf.writeInt(achievementCount);
            buf.writeString(selectedAbilityName);
            buf.writeString(selectedAbilityDescription);

            buf.writeInt(taskProgress.size());
            for (var entry : taskProgress.entrySet()) {
                buf.writeString(entry.getKey().name());
                buf.writeInt(entry.getValue());
            }

            writeStringList(buf, unlockNames);
            writeStringList(buf, unlockDescriptions);
        }

        @Override
        public CustomPacketPayload.Type<ProgressionSnapshotPayload> type() {
            return ID;
        }

        private static EnumMap<TaskType, Integer> readTaskProgress(RegistryFriendlyByteBuf buf) {
            int taskCount = buf.readInt();
            EnumMap<TaskType, Integer> taskProgress = new EnumMap<>(TaskType.class);
            for (int i = 0; i < taskCount; i++) {
                taskProgress.put(TaskType.valueOf(buf.readString()), buf.readInt());
            }
            return taskProgress;
        }

        private static List<String> readStringList(RegistryFriendlyByteBuf buf) {
            int count = buf.readInt();
            List<String> values = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                values.add(buf.readString());
            }
            return values;
        }

        private static void writeStringList(RegistryFriendlyByteBuf buf, List<String> values) {
            buf.writeInt(values.size());
            for (String value : values) {
                buf.writeString(value);
            }
        }
    }

    public record UseAbilityPayload() implements CustomPacketPayload {
        public static final UseAbilityPayload INSTANCE = new UseAbilityPayload();
        public static final CustomPacketPayload.Type<UseAbilityPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "use_ability"));
        public static final StreamCodec<RegistryFriendlyByteBuf, UseAbilityPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<UseAbilityPayload> type() {
            return ID;
        }
    }

    public record SwitchAbilityPayload() implements CustomPacketPayload {
        public static final SwitchAbilityPayload INSTANCE = new SwitchAbilityPayload();
        public static final CustomPacketPayload.Type<SwitchAbilityPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "switch_ability"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SwitchAbilityPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<SwitchAbilityPayload> type() {
            return ID;
        }
    }

    public record ConsumeAbilityItemPayload() implements CustomPacketPayload {
        public static final ConsumeAbilityItemPayload INSTANCE = new ConsumeAbilityItemPayload();
        public static final CustomPacketPayload.Type<ConsumeAbilityItemPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "consume_ability_item"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ConsumeAbilityItemPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<ConsumeAbilityItemPayload> type() {
            return ID;
        }
    }

    public record HunterTrackPayload() implements CustomPacketPayload {
        public static final HunterTrackPayload INSTANCE = new HunterTrackPayload();
        public static final CustomPacketPayload.Type<HunterTrackPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "hunter_track"));
        public static final StreamCodec<RegistryFriendlyByteBuf, HunterTrackPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<HunterTrackPayload> type() {
            return ID;
        }
    }

    public record HunterBlockPayload() implements CustomPacketPayload {
        public static final HunterBlockPayload INSTANCE = new HunterBlockPayload();
        public static final CustomPacketPayload.Type<HunterBlockPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "hunter_block"));
        public static final StreamCodec<RegistryFriendlyByteBuf, HunterBlockPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<HunterBlockPayload> type() {
            return ID;
        }
    }

    public record HunterSlowPayload() implements CustomPacketPayload {
        public static final HunterSlowPayload INSTANCE = new HunterSlowPayload();
        public static final CustomPacketPayload.Type<HunterSlowPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "hunter_slow"));
        public static final StreamCodec<RegistryFriendlyByteBuf, HunterSlowPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<HunterSlowPayload> type() {
            return ID;
        }
    }

    public record SpeedrunnerEscapePayload() implements CustomPacketPayload {
        public static final SpeedrunnerEscapePayload INSTANCE = new SpeedrunnerEscapePayload();
        public static final CustomPacketPayload.Type<SpeedrunnerEscapePayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "speedrunner_escape"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SpeedrunnerEscapePayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<SpeedrunnerEscapePayload> type() {
            return ID;
        }
    }

    public record SpeedrunnerSpeedPayload() implements CustomPacketPayload {
        public static final SpeedrunnerSpeedPayload INSTANCE = new SpeedrunnerSpeedPayload();
        public static final CustomPacketPayload.Type<SpeedrunnerSpeedPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "speedrunner_speed"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SpeedrunnerSpeedPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<SpeedrunnerSpeedPayload> type() {
            return ID;
        }
    }

    public record SpeedrunnerInvisPayload() implements CustomPacketPayload {
        public static final SpeedrunnerInvisPayload INSTANCE = new SpeedrunnerInvisPayload();
        public static final CustomPacketPayload.Type<SpeedrunnerInvisPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "speedrunner_invis"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SpeedrunnerInvisPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<SpeedrunnerInvisPayload> type() {
            return ID;
        }
    }

    public record RequestProgressSnapshotPayload() implements CustomPacketPayload {
        public static final RequestProgressSnapshotPayload INSTANCE = new RequestProgressSnapshotPayload();
        public static final CustomPacketPayload.Type<RequestProgressSnapshotPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FrogSlimeGamemode.MOD_ID, "request_progress_snapshot"));
        public static final StreamCodec<RegistryFriendlyByteBuf, RequestProgressSnapshotPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public CustomPacketPayload.Type<RequestProgressSnapshotPayload> type() {
            return ID;
        }
    }
}
