# 🐸 Frog & Slime Gamemode 🫧

<div align="center">

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.143-orange)
![Java](https://img.shields.io/badge/Java-21-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)
![Version](https://img.shields.io/badge/Version-2.6.0--NeoForge-blueviolet)

**A unique Minecraft NeoForge mod where frog and slime helpers fight for you!**

> ⚠️ **Port Notice**: This version has been ported from Fabric 1.20.1 to NeoForge 1.21.1 with Mojang mappings.

[Features](#features) • [Commands](#-all-commands) • [Installation](#installation) • [How to Play](#how-to-play) • [Gallery](#gallery)

</div>

## Route Revamp

This build focuses on making the mod actually playable as a guided route instead of a pile of disconnected systems.

### What changed

- Progression menus now use synced multiplayer-safe data instead of fake client placeholders.
- The main route is tracked through 10 concrete objectives from activation to final boss kill.
- Task completion now triggers objective popups, achievement toasts, and progress snapshots.
- The guide book, task book, starter kit, and chat commands now explain how to progress on SMP.
- Ability forging is clearer: the Ability Crafting Table can now forge matching mob drops directly into mob ability items.
- Manhunt now supports TAB cycling plus R use for both hunters and speedrunners while holding the correct control item.
- Speedrunner deaths, hunter wins, dragon wins, and giant slime boss wins now properly end the match.

## Quick Start

1. Run `/frogslime enable`.
2. Spawn and tame your frog and slime helpers.
3. Assign a helper role.
4. Unlock and forge abilities.
5. Reach the Nether, then the End.
6. Defeat the Ender Dragon or Giant Slime Boss to finish the run.

## Core Commands

- `/frogslime help` - overview of the route, controls, and side systems
- `/frogslime progress` - current tasks, completion, and upcoming unlocks
- `/frogslime abilities` - unlocked player abilities and selected slot
- `/frogslime recipes` - main crafting loop and forge flow
- `/frogslime guide` - restore the guide book and task book
- `/frogslime tasks` - open the progression board
- `/frogslime contract list`
- `/frogslime contract accept <id>`
- `/frogslime contract my`

## Manhunt Controls

- Hunters: hold a `Hunter Tracker` or `Manhunt Compass`, press `TAB` to cycle `Track`, `Blockade`, `Snare`, then press `R`.
- Speedrunners: hold a `Clock`, press `TAB` to cycle `Escape`, `Burst`, `Veil`, then press `R`.
- Use `/frogslime manhunt status` to inspect the current match.
- The manhunt ends when all speedrunners are eliminated or when a speedrunner beats the run by killing the Ender Dragon or Giant Slime Boss.

## SMP Onboarding

- Once the server is already running Frog & Slime mode, newly joining players are auto-enabled and receive the starter route kit.
- The starter route includes a written guide, task book, helper eggs, role items, ability sticks, and early survival gear.
- Contracts are explained through `/frogslime help`, the guide book, and `/frogslime contract list`.
- Use `/frogslime progress` whenever you are unsure what to do next.

---

## � Yo, What's Good?! Welcome to the Chaos! 🐸🫧

**Subscribe or your frogs will evolve WITHOUT you!** (jk... unless? 😳)

What's up everybody, it's **WayaCreate** back with another INSANE Minecraft mod that will literally break your brain! 🧠💥 This isn't just a mod—this is a **COMPLETE MULTIPLAYER RPG EXPERIENCE** that I built because you guys asked for it! That's right, this is **community-driven content** at its finest!

### 🔥 Why You NEED This Mod (Trust Me Bro):
- **Tame frog & slime helpers** that EVOLVE into ABSOLUTE UNITS! 💪
- **Eat mobs to steal their abilities** (yes, you read that right, EAT THEM! 🍽️)
- **Guilds, Bounties, Manhunt, Economy**—basically Minecraft on CREATIVE MODE STEROIDS!
- **GIANT SLIME BOSS** replaces the Ender Dragon... and it gets WILD! 👹
- **20+ unlockable abilities** from Fireballs to Teleportation! ⚡

### 🎯 Who Made This Chaos?
Hey, I'm **Waya Steurbaut** (aka **WayaCreate**) and I make fan-requested content spanning gaming, design, and cutting-edge tech! From Minecraft mods to Blender tutorials, video editing tips to live streams—**YOUR ideas come to life here!** 🎮🎨🚀

📺 **Subscribe on YouTube:** [@wayacreate](https://www.youtube.com/@wayacreate)  
💻 **Check my other projects below!** ⬇️

### 🎮 What You'll Find on My Channel:
- Minecraft, Roblox, Fortnite & Destiny 2 gameplay/tutorials
- Blender 3D modeling & animation guides
- Video editing masterclasses
- AI tools & graphics design tips
- **LIVE STREAMS** where YOU control the content!

*New videos every week, powered by YOUR suggestions!* 🏆

---

## 🛠️ For The Developers (Why Use This Template?)

Yo modders! Looking for a **FULLY FEATURED RPG MOD TEMPLATE** that actually works? Here's why this repo is OP:

✅ **Complete Evolution System** - 5-stage helper progression with persistent saving  
✅ **Ability Unlock Framework** - Modular system for adding new mob abilities  
✅ **Economy & Trading** - Full player marketplace with coins, shops, guild banks  
✅ **Multiplayer Systems** - Teams, guilds, bounties, private messaging, manhunt  
✅ **Custom Dimensions** - Datapack-based dimension implementation  
✅ **AI Integration** - Baritone helper roles (Miner, Builder, Farmer, etc.)  
✅ **GUI Systems** - Task menus, progress bars, achievement toasts  
✅ **RPG Elements** - Contracts, missions, ranks, achievements, boss fights  

### ⚠️ What to Watch Out For:
- **Persistent Data** is saved per-world, not globally—plan your server resets!
- **Dimension System** requires proper datapack setup or falls back to vanilla End
- **Helper AI** needs Baritone API—make sure dependencies are loaded
- **Economy** uses server-side storage—back up your `frogslime/` data folder!

---

## 🔗 My Other Epic Projects

Want more chaos? Check these out:

| Project | Description | Link |
|---------|-------------|------|
| **🎡 Waya's Wheel** | Interactive streaming wheel for content creators | [GitHub](https://github.com/WayaSteurbaut/wayas-wheel) |
| **🎵 Anime MC Shonen Rap Cypher** | Music production & lyric writing project | Coming Soon |
| **🎨 Trailer Shaders** | Custom Minecraft shader pack for cinematic content | Coming Soon |

---

## � What is This Mod?

Frog & Slime Gamemode is a **complete multiplayer RPG experience** for Minecraft Fabric. Tame frog and slime helpers that evolve as they fight, unlock mob abilities by eating them, complete contracts, join guilds, trade with other players, and compete in bounty hunting and manhunt modes. All culminating in an epic boss fight with a shocking twist ending!

### 🌟 Key Features at a Glance
- **Evolution System**: Helpers evolve through 5 stages (Basic → Advanced → Elite → Master → Final Form)
- **Mob Abilities**: Eat mobs to unlock 20+ unique abilities (Fireball, Teleport, Poison Cloud, etc.)
- **Economy System**: Full player marketplace with coins, trading, bounties, and guild banks
- **Multiplayer Features**: Guilds, teams, manhunt mode, PvP with bounties, private messaging
- **RPG Elements**: Contracts, missions, ranks, achievements, and progression rewards
- **Custom Dimension**: Transformed End dimension for the final boss fight
- **Builder AI**: Helpers can build schematics using Baritone integration

## Features

### 🎮 Core Gameplay
- **Custom Gamemode System**: Activate with `/frogslime enable` or in the game menu
- **Helper Mobs**: Tame frog and slime helpers that fight for you
- **Evolution System**: Helpers evolve through 5 stages by defeating mobs
  - Basic → Advanced → Elite → Master → *FINAL FORM*
- **Automatic Collection**: Helpers collect drops from killed mobs
- **Persistent Saving**: Gamemode state and abilities persist across server restarts
- **Player Kill Rewards**: Kill other players to gain special abilities (WayaCreate, Derpy Derp, stolen abilities)

### ✨ Visual Effects
- **Custom Particles**: Helpers spawn particles when evolving, being pet, and during idle
- **Dynamic Name Tags**: Helpers display their evolution stage with color-coded names
- **Animated Entities**: Smooth animations and visual effects
- **Custom Titles**: Dramatic on-screen titles during key moments
- **Progress Bar HUD**: Visual progress bar showing gamemode completion percentage
- **Achievement Toasts**: Custom popup notifications for achievements with sound effects

### 🎯 Challenges & Progression
- **Task & Challenge System**: Complete 10 insane challenges with rewards
- **Custom GUI**: Open tasks menu with Task Book or `/frogslime tasks`
- **Achievement System**: Unlock 7 custom advancements
- **Player Transformations**: Eat frog/slime food to gain their abilities

### 👹 Boss Fight
- **Giant Slime Boss**: The Ender Dragon has been replaced... with a GIANT SLIME?!
- **Unexpected Ending**: Defeat the boss to trigger the final evolution... but something goes wrong

### 🌌 Custom Dimension
- **Transformed End**: A custom dimension variant of the End using datapack configuration
- **Teleportation Commands**: Easy access to dimension via `/frogslime dimension transformed_end`
- **Configurable**: JSON-based dimension settings for easy customization
- **Fallback System**: Automatically falls back to regular End if custom dimension unavailable

### � Custom Items
- **Evolution Stones**: Instantly evolve your helpers (craftable and upgradeable)
- **Special Foods**: Feed your helpers to strengthen them
- **YouTuber Swords**: Dream, Technoblade, Grian, and Mumbo Jumbo themed weapons
- **Funny Armor**: Mustard Helmet, Orphan Shield, Prankster Chestplate
- **Task Book**: View and track your challenges
- **Final Evolution Crystal**: Unlock the ultimate slime form
- **Custom Potions**: Frog Power, Slime Resilience, WayaCreate Blessing, Derpy Curse, Manhunt Tracker
- **Manhunt Compass**: Tracks assigned manhunt targets

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
| **Builder Role** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/builder_role.png" width="32"> | Assigns building AI to helper |
| **Farmer Role** | <img src="src/main/resources/assets/frogslimegamemode/textures/item/farmer_role.png" width="32"> | Assigns farming AI to helper |
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
- Minecraft 1.21.1
- NeoForge 21.1.143+
- Java 21+

### Steps
1. Install [NeoForge](https://neoforged.net/)
2. Download the latest release of Frog & Slime Gamemode for NeoForge
3. Place the `.jar` file in your `mods` folder
4. Launch the game!

## How to Play

### Getting Started

1. **Start the gamemode**: `/frogslime enable`
   - You'll receive a starter kit with iron gear, food, tools, and a **Guide Book**
   - Read the Guide Book for detailed instructions on all features
   - You start with the **Frog Ability** (Tongue Grab) unlocked

2. **Spawn helpers** using the spawn eggs from your starter kit
   - **Frog Helper**: Melee combat specialist
   - **Slime Helper**: Ranged attack specialist

3. **Let them fight** mobs and evolve automatically
   - Helpers gain XP from killing mobs
   - Each evolution stage increases health and damage
   - Helpers may drop **Ability Drops** when killing mobs (40% chance)

4. **Unlock Mob Abilities** by eating mobs
   - Right-click on mobs to eat them
   - Each mob gives a unique ability (Zombie = Fireball, Enderman = Teleport, etc.)
   - Switch abilities with **[TAB]** key
   - Abilities give passive bonuses and active powers

5. **Craft Mob Ability Items** in the Anvil
   - Kill mobs to get their drops (rotten flesh, bones, spider eyes, etc.)
   - Combine the mob drop with an **Ability Drop** in an **Anvil**
   - This creates a **Mob Ability Item** you can use to permanently unlock that ability
   - Example: Rotten Flesh + Ability Drop = Zombie Ability Item

6. **Use Evolution Stones** to speed up evolution
   - Craftable with vanilla materials
   - Instantly evolves helpers to the next stage

7. **Assign roles** to helpers with `/helper <role>` for specialized AI
   - Miner, Lumberjack, Combat, Builder, Farmer

8. **Open Task Book** with right-click to see challenges
   - Complete 10 insane challenges for rewards

9. **Brew custom potions** for special effects
   - Frog Power, Slime Resilience, WayaCreate Blessing, and more

10. **Journey to The End** to face the GIANT SLIME BOSS
    - The Ender Dragon has been replaced with a Giant Slime!
    - Defeat it with your slime helper nearby for the final evolution

11. **Beat the boss** for the shocking finale... or will you?

### Mob Abilities System

**How to Unlock Abilities:**
1. **Eat Mobs**: Right-click on mobs to eat them and unlock their abilities
2. **Craft in Anvil**: Combine mob drops with Ability Drops in an Anvil to create permanent ability items
3. **Use Ability Items**: Right-click the crafted ability item to permanently unlock it

**Common Mob Abilities:**
| Mob | Drop | Ability | Effect |
|-----|------|---------|--------|
| Zombie | Rotten Flesh | Fireball | Shoot fireballs at enemies |
| Skeleton | Bone | Poison Cloud | Creates poison gas around you |
| Spider | Spider Eye | Web Shot | Shoot webs to slow enemies |
| Creeper | Gunpowder | Explosion Resistance | Immune to explosions |
| Enderman | Ender Pearl | Teleport | Teleport on right-click |
| Witch | Potion | Healing | Regenerate health faster |
| Blaze | Blaze Rod | Fire Resistance | Immune to fire |
| Slime | Slime Ball | Bounce | Higher jump height |
| Chicken | Feather | Levitation | Float in the air |
| Rabbit | Rabbit Foot | Speed Boost | Move faster |
| Guardian | Prismarine Crystals | Water Breathing | Breathe underwater |
| Phantom | Phantom Membrane | Night Vision | See in the dark |
| Shulker | Shulker Shell | Thorns | Reflect damage |

**Anvil Crafting Examples:**
- Rotten Flesh + Ability Drop = Zombie Ability Item
- Bone + Ability Drop = Skeleton Ability Item
- Spider Eye + Ability Drop = Spider Ability Item
- Gunpowder + Ability Drop = Creeper Ability Item
- Ender Pearl + Ability Drop = Enderman Ability Item

**Using Abilities:**
- Press **[TAB]** to switch between unlocked abilities
- Some abilities are passive (always active)
- Some abilities are active (right-click to use)
- Check the Guide Book for full ability list

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

### Helper Roles
Assign roles to your helpers with `/helper <role>`:
- **Miner**: Automatically mines nearby ores
- **Lumberjack**: Automatically chops nearby trees
- **Combat**: Enhanced attack damage
- **Builder**: Places blocks to build structures
- **Farmer**: Harvests and bone meals crops

### Custom Potions
Brew special potions in the brewing stand:
- **Frog Power Potion**: Jump Boost II + Speed I (brew with slime ball)
- **Slime Resilience Potion**: Resistance I + Regeneration I (brew with golden carrot)
- **WayaCreate Blessing Potion**: Strength II + Speed II + Regeneration I (brew with golden apple)
- **Derpy Curse Potion**: Slowness I + Weakness (brew with fermented spider eye)
- **Manhunt Tracker Potion**: Night Vision + Speed I (brew with glowstone)

### Manhunt Mode
Play speedrun manhunt with friends:
1. `/frogslime manhunt speedrunner` - Set the target player
2. `/frogslime manhunt hunter` - Become a hunter
3. Use Manhunt Compass to track the speedrunner
4. Compass shows distance and direction to target

### Transformed End Dimension
Access the custom dimension:
1. `/frogslime dimension transformed_end` - Teleport to the dimension
2. `/frogslime dimension return` - Return to Overworld spawn
- Uses datapack-based configuration for easy customization
- Configured with End biome and generation settings
- Can be modified in `data/frogslimegamemode/dimension/` JSON files

### Final Evolution (Slime Only)
After defeating the Giant Slime Boss, use the **Final Evolution Crystal** on your slime helper to unlock its ultimate form:
- 200 Health
- 30 Attack Damage
- 100% Knockback Resistance

## ⌨️ All Commands

### 🎮 Core Gamemode Commands (`/frogslime`)

| Command | Description |
|---------|-------------|
| `/frogslime enable` | Begin the gamemode - receive starter kit with Guide Book |
| `/frogslime disable` | Stop the gamemode |
| `/frogslime info` | Show help information |
| `/frogslime tasks` | Open tasks & challenges menu |
| `/frogslime reset` | **⚠️ Reset all gamemode data** (abilities, progress, everything) |

### 🏃 Manhunt Commands (`/frogslime manhunt`)

| Command | Description |
|---------|-------------|
| `/frogslime manhunt auto` | Start auto-assigned manhunt (random speedrunner) |
| `/frogslime manhunt speedrunner` | Set yourself as speedrunner |
| `/frogslime manhunt solo` | Set yourself as solo speedrunner |
| `/frogslime manhunt hunter` | Become a hunter (targets nearest player) |
| `/frogslime manhunt team <speedrunner_team> <hunter_team>` | Start team-based manhunt |
| `/frogslime manhunt end` | End the manhunt game |

### 🌌 Dimension Commands (`/frogslime dimension`)

| Command | Description |
|---------|-------------|
| `/frogslime dimension transformed_end` | Teleport to the Transformed End dimension |
| `/frogslime dimension return` | Return to Overworld spawn |

### 👥 Team Commands (`/frogslime team`)

| Command | Description |
|---------|-------------|
| `/frogslime team create <name> <color>` | Create a new team |
| `/frogslime team join <name>` | Join an existing team |
| `/frogslime team leave` | Leave your current team |
| `/frogslime team list` | List all teams |
| `/frogslime team tp <player>` | Teleport to a team member |

### 🎖️ Rank Commands (`/frogslime rank`)

| Command | Description |
|---------|-------------|
| `/frogslime rank <player> <rank>` | Set a player's rank (requires permissions) |

### 📜 Contract Commands (`/frogslime contract`)

| Command | Description |
|---------|-------------|
| `/frogslime contract list` | List available contracts |
| `/frogslime contract accept <type>` | Accept a contract |
| `/frogslime contract my` | View your active contracts |

### 💰 Economy Commands (Standalone)

| Command | Description |
|---------|-------------|
| `/balance` | Check your coin balance |
| `/pay <player> <amount>` | Send coins to another player |
| `/sell <price>` | Sell item in your hand to marketplace |
| `/shop` | View marketplace listings |
| `/shop buy <index>` | Buy item from marketplace |
| `/shop cancel <index>` | Cancel your listing |
| `/shop mylistings` | View your active listings |
| `/trade <player>` | Send trade request to player |
| `/trade accept` | Accept incoming trade |
| `/trade decline` | Decline incoming trade |
| `/trade toggle` | Toggle trading availability |

### 💬 Social Commands

| Command | Description |
|---------|-------------|
| `/msg <player> <message>` | Send private message |

### 🎯 Bounty Commands (`/bounty`)

| Command | Description |
|---------|-------------|
| `/bounty add <player> <coins>` | Place a bounty on a player |
| `/bounty list` | List all active bounties |
| `/bounty check` | Check bounty on your own head |

### 🏰 Guild Commands (`/guild`)

| Command | Description |
|---------|-------------|
| `/guild create <name>` | Create a new guild |
| `/guild disband` | Disband your guild (owner only) |
| `/guild invite <player>` | Invite player to guild |
| `/guild join <name>` | Join a guild (via invite) |
| `/guild leave` | Leave your guild |
| `/guild info` | Show guild information |
| `/guild members` | List guild members |
| `/guild deposit <amount>` | Deposit coins to guild bank |
| `/guild missions` | List active guild missions |
| `/guild missions create <name>` | Create a new mission |
| `/guild missions complete <id>` | Complete a mission |

### 🛠️ Helper Commands (`/helper`)

| Command | Description |
|---------|-------------|
| `/helper giverole <role>` | Get role assignment item (Miner, Lumberjack, Combat Specialist, Farmer, Builder) |
| `/helper <helper> build <schematic>` | Set helper to build a schematic |
| `/helper <helper> stop` | Stop helper building |
| `/helper <helper> progress` | Check build progress |

### 🧪 Test Commands (`/frogslime test`)

| Command | Description |
|---------|-------------|
| `/frogslime test achievement <id>` | Test unlock an achievement |

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

This mod feels like it was made by a modded YouTuber with too much free time (and it kind of was). Inspired by the chaotic energy of **Craftee**, **GoldActual**, **Donibobes**, **Skeppy**, **xNestorio**, **Baablu**, **Henwyy**, **Bionic**, and the unexpected plot twists that make you go "WAIT WHAT?!" 😱

This is basically if all those YouTubers had a baby and that baby learned Java... yeah, that's this mod. The energy is UNMATCHED! 🔥

## 📜 Changelog

### v2.6.0 - NeoForge 1.21.1 Port (Current)
- 🔄 **Major Port**: Migrated from Fabric 1.20.1 to NeoForge 1.21.1
- 🔧 **API Migration**: Updated to Mojang mappings (Yarn → Mojang)
- ☕ **Java 21**: Now requires Java 21+
- 🔧 **Core API Changes**:
  - `getRotationVector()` → `getLookAngle()`
  - `getPos()` → `position()`
  - `addVelocity()` → `push()`
  - `spawnParticles()` → `sendParticles()`
  - `addStatusEffect()` → `addEffect()`
  - `sendMessage()` → `sendSystemMessage()`
  - `formatted()` → `withStyle()`
  - `getWorld()` → `level()`
  - `teleport()` → `teleportTo()`
  - `markDirty()` → `setDirty()` (SavedData)
  - `PlayerManager` → `PlayerList`
  - Effect renames: `SLOWNESS`→`MOVEMENT_SLOWDOWN`, `JUMP_BOOST`→`JUMP`, etc.
- ⚠️ **Note**: This is a WIP port - some features may not be fully functional yet

### v1.8.8 - Previous Release (Fabric 1.20.1)
- ✅ Full economy system with marketplace, trading, and player shops
- ✅ Guild system with missions, banks, and member management
- ✅ Bounty system for PvP rewards
- ✅ Complete helper role system with Baritone AI integration
- ✅ Team system with teleportation and color support
- ✅ Private messaging system
- ✅ Enhanced manhunt with team and auto modes
- ✅ Rank system with chat prefixes

### v1.5.0 - Dimension Implementation
- ✅ Implemented datapack-based custom dimension system
- ✅ Configured dimension JSON files for proper generation
- ✅ Updated teleporter with RegistryKey-based dimension access
- ✅ Added fallback system to regular End if custom dimension unavailable

### v1.4.0 - UI & Polish Update
- ✅ Added progress bar HUD showing gamemode completion
- ✅ Implemented custom achievement toast notifications
- ✅ Added achievement sound effects (level-up sound)
- ✅ Enhanced HUD rendering system

### v1.3.0 - Dimension Update
- ✅ Added custom dimension system foundation
- ✅ Implemented dimension teleportation commands

### v1.2.0 - Final Polish
- ✅ Added Builder and Farmer AI roles
- ✅ Integrated ManhuntCompass with ManhuntManager
- ✅ Changed potion recipes from crafting to brewing
- ✅ Implemented persistent saving system
- ✅ Added player kill reward system

## Contributing

Contributions are welcome! Feel free to submit issues, feature requests, or pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Created by **WayaCreate** (Waya Steurbaut)

[YouTube](https://youtube.com/@wayacreate) • [GitHub](https://github.com/wayacreate)

</div>
