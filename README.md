# 🐸 Frog & Slime Gamemode 🫧

<div align="center">

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen)
![Fabric](https://img.shields.io/badge/Fabric-Loader-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

**A unique Minecraft Fabric mod where frog and slime helpers beat the game for you!**

[Features](#features) • [Installation](#installation) • [How to Play](#how-to-play) • [Gallery](#gallery) • [Commands](#commands)

</div>

---

## What is this?

A custom gamemode where you tame frog and slime helpers that fight mobs and evolve as they kill. Inspired by content creators like Craftee, xxNestorio, DonnieBobes, Skeppy and more, but with an unexpected twist ending like in Groxs videos.

## Features

### 🎮 Core Gameplay
- **Custom Gamemode System**: Activate with `/frogslime start` or in the game menu
- **Helper Mobs**: Tame frog and slime helpers that fight for you
- **Evolution System**: Helpers evolve through 5 stages by defeating mobs
  - Basic → Advanced → Elite → Master → *FINAL FORM*
- **Automatic Collection**: Helpers collect drops from killed mobs

### ✨ Visual Effects
- **Custom Particles**: Helpers spawn particles when evolving, being pet, and during idle
- **Dynamic Name Tags**: Helpers display their evolution stage with color-coded names
- **Animated Entities**: Smooth animations and visual effects
- **Custom Titles**: Dramatic on-screen titles during key moments

### 🎯 Challenges & Progression
- **Task & Challenge System**: Complete 10 insane challenges with rewards
- **Custom GUI**: Open tasks menu with Task Book or `/frogslime tasks`
- **Achievement System**: Unlock 7 custom advancements
- **Player Transformations**: Eat frog/slime food to gain their abilities

### 👹 Boss Fight
- **Giant Slime Boss**: The Ender Dragon has been replaced... with a GIANT SLIME?!
- **Unexpected Ending**: Defeat the boss to trigger the final evolution... but something goes wrong

### 🎁 Custom Items
- **Evolution Stones**: Instantly evolve your helpers
- **Special Foods**: Feed your helpers to strengthen them
- **YouTuber Swords**: Dream, Technoblade, Grian, and Mumbo Jumbo themed weapons
- **Funny Armor**: Mustard Helmet, Orphan Shield, Prankster Chestplate
- **Task Book**: View and track your challenges
- **Final Evolution Crystal**: Unlock the ultimate slime form

## 🎮 Interactive GUI

**[📖 Open Interactive Minecraft GUI](docs/interactive-gui.html)**

Experience the mod's items, crafting, and collections with an authentic Minecraft-style interface!

**Features:**
- **📦 Collections**: Browse all items with category filters
- **⚒️ Crafting**: Interactive crafting table with recipe selector
- **🧪 Potions**: Brewing stand interface
- **🏆 Achievements**: Track your progress
- **⭐ Skills**: Evolution skill tree

**Hover over items** to see their NBT tags, stats, and abilities - just like in-game!

## 📚 Item Collections

### Evolution Items
| Item | Texture | Stats | Abilities | NBT Data |
|------|---------|-------|-----------|----------|
| **Evolution Stone** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/evolution_stone.png" width="32"> | Max Stack: 16 | Instantly evolves helpers to next stage | `{EvolutionPower: 1}` |
| **Final Evolution Crystal** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/final_evolution_crystal.png" width="32"> | Max Stack: 1 | Unlocks ultimate slime form after boss | `{FinalForm: true, BossDefeated: true}` |

### Food Items
| Item | Texture | Hunger | Saturation | Effects |
|------|---------|--------|------------|---------|
| **Slime Food** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/slime_food.png" width="32"> | 4 | 0.6 | Jump Boost II (30s), Resistance I (30s) |
| **Frog Food** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/frog_food.png" width="32"> | 3 | 0.5 | Speed I (30s), Water Breathing (30s) |

### YouTuber Swords
| Item | Texture | Material | Attack Damage | Attack Speed | Special Ability |
|------|---------|----------|---------------|--------------|-----------------|
| **Dream's Blade** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/dream_sword.png" width="32"> | Netherite | +3 | -2.4 | Speed III (10s) on use |
| **Technoblade's Blade** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/technoblade_sword.png" width="32"> | Netherite | +5 | -2.2 | Strength II (10s), Resistance II (10s) |
| **Grian's Blade** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/grian_sword.png" width="32"> | Diamond | +3 | -2.4 | Invisibility (5s) on use |
| **Mumbo Jumbo's Blade** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/mumbo_jumbo_sword.png" width="32"> | Diamond | +3 | -2.4 | Haste III (10s) on use |

### Funny Armor
| Item | Texture | Material | Protection | Special |
|------|---------|----------|------------|---------|
| **Mustard Helmet** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/mustard_helmet.png" width="32"> | Gold | 2 | Has enchantment glint |
| **Orphan Shield** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/orphan_shield.png" width="32"> | Iron | - | Custom NBT: `{OrphanShield: true}` |
| **Prankster Chestplate** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/prankster_chestplate.png" width="32"> | Leather | 3 | Purple variant |

### Utility Items
| Item | Texture | Max Stack | Function |
|------|---------|-----------|----------|
| **Task Book** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/task_book.png" width="32"> | 1 | Shows gamemode progress and objectives |
| **Manhunt Compass** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/manhunt_compass.png" width="32"> | 1 | Tracks nearest player |
| **Ability Drop** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/ability_drop.png" width="32"> | 64 | Dropped by mobs, grants abilities |

### Role Items
| Item | Texture | Function |
|------|---------|----------|
| **Miner Role** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/miner_role.png" width="32"> | Assigns mining AI to helper |
| **Lumberjack Role** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/lumberjack_role.png" width="32"> | Assigns woodcutting AI to helper |
| **Combat Role** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/combat_role.png" width="32"> | Assigns combat AI to helper |

### Spawn Eggs
| Item | Texture | Entity |
|------|---------|--------|
| **Frog Helper Spawn Egg** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/frog_helper_spawn_egg.png" width="32"> | Frog Helper |
| **Slime Helper Spawn Egg** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/slime_helper_spawn_egg.png" width="32"> | Slime Helper |

## 🏆 Achievements

| Achievement | Icon | Description | Requirements |
|-------------|------|-------------|---------------|
| **First Helper** | 🐸 | Tame your first frog or slime | Tame any helper |
| **Evolution Beginner** | ⭐ | Evolve a helper to stage 1 | Helper kills 10 mobs |
| **Evolution Expert** | ⭐⭐ | Evolve a helper to stage 2 | Helper kills 25 mobs |
| **Evolution Master** | ⭐⭐⭐ | Evolve a helper to stage 3 | Helper kills 50 mobs |
| **Final Form** | 👑 | Unlock the final slime evolution | Defeat boss + use crystal |
| **Boss Slayer** | 👹 | Defeat the Giant Slime Boss | Reach The End and win |
| **Task Master** | 📋 | Complete all 10 challenges | Finish all tasks |

## ⭐ Evolution Skill Tree

| Skill | Icon | Status | Requirements | Effect |
|-------|------|--------|--------------|--------|
| **Basic Attack** | ⚔️ | ✅ Unlocked | None | Helper attacks nearby mobs |
| **Mob Collection** | 📦 | ✅ Unlocked | Basic Attack | Helper auto-collects drops |
| **Evolution Stage 1** | ⭐ | ✅ Unlocked | Kill 10 mobs | +10 Health, +2 Damage |
| **Evolution Stage 2** | ⭐⭐ | 🔒 Locked | Kill 25 mobs | +20 Health, +4 Damage |
| **Evolution Stage 3** | ⭐⭐⭐ | 🔒 Locked | Kill 50 mobs | +30 Health, +6 Damage |
| **Final Evolution** | 👑 | 🔒 Locked | Defeat Giant Slime Boss | 200 Health, 30 Damage, 100% KB Resistance |

### Quick Recipe Reference
<table>
  <tr>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/entity/frog_helper.png" alt="Frog Helper" width="128"><br><b>Frog Helper</b></td>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/entity/slime_helper.png" alt="Slime Helper" width="128"><br><b>Slime Helper</b></td>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/entity/giant_slime_boss.png" alt="Giant Slime Boss" width="128"><br><b>Giant Slime Boss</b></td>
  </tr>
  <tr>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/entity/slime_helper_final.png" alt="Final Form Slime" width="128"><br><b>Final Form Slime</b></td>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/entity/frog_king.png" alt="Frog King" width="128"><br><b>Frog King</b></td>
    <td align="center"></td>
  </tr>
</table>

### Items
<table>
  <tr>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/item/evolution_stone.png" alt="Evolution Stone" width="64"><br><b>Evolution Stone</b></td>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/item/final_evolution_crystal.png" alt="Final Evolution Crystal" width="64"><br><b>Final Evolution Crystal</b></td>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/item/task_book.png" alt="Task Book" width="64"><br><b>Task Book</b></td>
  </tr>
  <tr>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/item/dream_sword.png" alt="Dream Sword" width="64"><br><b>Dream Sword</b></td>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/item/technoblade_sword.png" alt="Technoblade Sword" width="64"><br><b>Technoblade Sword</b></td>
    <td align="center"><img src="src/main/resources/assets/frogslimegamemode/textures/item/grian_sword.png" alt="Grian Sword" width="64"><br><b>Grian Sword</b></td>
  </tr>
</table>

## Installation

### Requirements
- Minecraft 1.20.1
- Fabric Loader
- Fabric API

### Steps
1. Install [Fabric Loader](https://fabricmc.net/)
2. Download the latest [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
3. Download the latest release of Frog & Slime Gamemode
4. Place both `.jar` files in your `mods` folder
5. Launch the game!

## How to Play

1. **Start the gamemode**: `/frogslime start` or open the game menu
2. **Spawn helpers** using spawn eggs (creative mode or craft them)
3. **Right-click** to tame frogs and slimes in the wild
4. **Let them fight** mobs and evolve automatically
5. **Use Evolution Stones** to speed up evolution
6. **Open Task Book** with right-click to see challenges
7. **Complete tasks** to unlock rewards
8. **Journey to The End** to face the GIANT SLIME BOSS
9. **Beat the boss** for the shocking finale... or will you?

### Evolution System

**Frog Helper Evolution:**
- Stage 0 → Stage 1: Kill 10 mobs
- Stage 1 → Stage 2: Kill 25 mobs
- Stage 2 → Stage 3: Kill 50 mobs

**Slime Helper Evolution:**
- Stage 0 → Stage 1: Kill 15 mobs
- Stage 1 → Stage 2: Kill 35 mobs
- Stage 2 → Stage 3: Kill 60 mobs

Each evolution increases:
- Health (+10 for frogs, +15 for slimes per stage)
- Attack Damage (+2 for frogs, +3 for slimes per stage)
- New visual effects at higher stages

### Final Evolution (Slime Only)
After defeating the Giant Slime Boss, use the **Final Evolution Crystal** on your slime helper to unlock its ultimate form:
- 200 Health
- 30 Attack Damage
- 100% Knockback Resistance

## Commands

| Command | Description |
|---------|-------------|
| `/frogslime start` | Begin the gamemode |
| `/frogslime stop` | Stop the gamemode |
| `/frogslime info` | Show help information |
| `/frogslime tasks` | Open tasks & challenges menu |

## Tasks & Challenges

| Challenge | Description |
|-----------|-------------|
| 🟤 Eat 64 Dirt Blocks | "Why? Because we can!" |
| 🐸 Jump 100 Times Near Frogs | "They love it when you dance!" |
| 🫧 Have 5 Slime Helpers | "It's a slime party!" |
| ⭐ Evolve to Master | "Peak performance achieved!" |
| 💀 Helpers Eat 100 Mobs | "Nom nom nom..." |
| 💀 Die 100 Times | "Pain is temporary, glory is forever" |
| 🍎 Eat Golden Apple Transformed | "Maximum power!" |
| 💎 Craft 100 Evolution Stones | "Stonks!" |
| 🚪 Reach The End | "The final challenge awaits..." |
| 👹 Defeat Giant Slime Boss | "Wait... where's the dragon?" |

## The Twist

When you defeat the Giant Slime Boss (yeah, we replaced the Ender Dragon with a GIANT SLIME) with your slime helper nearby, it will absorb the boss's power and unlock its FINAL FORM. Dramatic particles explode everywhere, custom titles appear on screen, and your slime becomes an unstoppable force of nature.

But be warned... your slime may become too powerful. What have you created?

**TO BE CONTINUED...**

## Why This Exists

This mod feels like it was made by a modded YouTuber with too much free time (and it kind of was). Inspired by the chaotic energy of Craftee, xxNestorio, DonnieBobes, Skeppy, and the unexpected plot twists of Groxs videos.

## Contributing

Contributions are welcome! Feel free to submit issues, feature requests, or pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Created by **WayaCreate** (Waya Steurbaut)

[YouTube](https://youtube.com/@wayacreate) • [GitHub](https://github.com/wayacreate)

</div>