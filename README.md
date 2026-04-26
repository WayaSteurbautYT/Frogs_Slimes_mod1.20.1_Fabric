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

## 🎮 Crafting Recipes (Minecraft GUI)

### Evolution Stone
<div style="display: inline-block; background: #c6c6c6; border: 4px solid #555; border-radius: 4px; padding: 8px; margin: 10px;">
  <div style="display: grid; grid-template-columns: repeat(3, 40px); gap: 2px;">
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737;"></div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center; position: relative;">
      <span style="font-size: 24px;">💎</span>
      <div style="position: absolute; top: -30px; left: 50%; transform: translateX(-50%); background: #100010; border: 2px solid #500050; padding: 5px 8px; border-radius: 3px; white-space: nowrap; opacity: 0; transition: opacity 0.2s; pointer-events: none; z-index: 10;" onmouseover="this.style.opacity=1" onmouseout="this.style.opacity=0">
        <div style="color: #ffff55; font-weight: bold;">Diamond</div>
        <div style="color: #aaaaaa; font-size: 11px;">Crafting Material</div>
        <div style="color: #ff5555; font-size: 10px;">Durability: 1561</div>
      </div>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737;"></div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">💎</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">💎</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737;"></div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">💎</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737;"></div>
  </div>
  <div style="display: flex; align-items: center; justify-content: center; margin: 8px 0;">
    <span style="color: #fff; font-size: 20px;">➡️</span>
  </div>
  <div style="display: flex; align-items: center; justify-content: center; gap: 10px;">
    <div style="width: 50px; height: 50px; background: #8b8b8b; border: 3px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 30px;">✨</span>
    </div>
    <div style="color: #fff; font-size: 12px;">Evolution Stone</div>
  </div>
</div>

### Dream Sword
<div style="display: inline-block; background: #c6c6c6; border: 4px solid #555; border-radius: 4px; padding: 8px; margin: 10px;">
  <div style="display: grid; grid-template-columns: repeat(3, 40px); gap: 2px;">
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737;"></div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🟨</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737;"></div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🟨</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">⚔️</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🟨</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737;"></div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🟨</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737;"></div>
  </div>
  <div style="display: flex; align-items: center; justify-content: center; margin: 8px 0;">
    <span style="color: #fff; font-size: 20px;">➡️</span>
  </div>
  <div style="display: flex; align-items: center; justify-content: center; gap: 10px;">
    <div style="width: 50px; height: 50px; background: #8b8b8b; border: 3px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 30px;">⚔️</span>
    </div>
    <div style="color: #fff; font-size: 12px;">Dream Sword</div>
  </div>
</div>

### Slime Food
<div style="display: inline-block; background: #c6c6c6; border: 4px solid #555; border-radius: 4px; padding: 8px; margin: 10px;">
  <div style="display: grid; grid-template-columns: repeat(3, 40px); gap: 2px;">
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🐰</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
    <div style="width: 40px; height: 40px; background: #8b8b8b; border: 2px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 24px;">🫧</span>
    </div>
  </div>
  <div style="display: flex; align-items: center; justify-content: center; margin: 8px 0;">
    <span style="color: #fff; font-size: 20px;">➡️</span>
  </div>
  <div style="display: flex; align-items: center; justify-content: center; gap: 10px;">
    <div style="width: 50px; height: 50px; background: #8b8b8b; border: 3px solid #373737; display: flex; align-items: center; justify-content: center;">
      <span style="font-size: 30px;">🍖</span>
    </div>
    <div style="color: #fff; font-size: 12px;">Slime Food x8</div>
  </div>
</div>

## 🏆 Achievements

<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 20px 0;">
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #4ade80; border-radius: 8px; padding: 15px; text-align: center;">
    <div style="font-size: 40px; margin-bottom: 10px;">🐸</div>
    <div style="color: #4ade80; font-weight: bold; margin-bottom: 5px;">First Helper</div>
    <div style="color: #aaa; font-size: 12px;">Tame your first frog or slime</div>
  </div>
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #4ade80; border-radius: 8px; padding: 15px; text-align: center;">
    <div style="font-size: 40px; margin-bottom: 10px;">⭐</div>
    <div style="color: #4ade80; font-weight: bold; margin-bottom: 5px;">Evolution Beginner</div>
    <div style="color: #aaa; font-size: 12px;">Evolve a helper to stage 1</div>
  </div>
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #4ade80; border-radius: 8px; padding: 15px; text-align: center;">
    <div style="font-size: 40px; margin-bottom: 10px;">⭐⭐</div>
    <div style="color: #4ade80; font-weight: bold; margin-bottom: 5px;">Evolution Expert</div>
    <div style="color: #aaa; font-size: 12px;">Evolve a helper to stage 2</div>
  </div>
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #555; border-radius: 8px; padding: 15px; text-align: center;">
    <div style="font-size: 40px; margin-bottom: 10px;">⭐⭐⭐</div>
    <div style="color: #555; font-weight: bold; margin-bottom: 5px;">Evolution Master</div>
    <div style="color: #555; font-size: 12px;">Evolve a helper to stage 3</div>
  </div>
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #555; border-radius: 8px; padding: 15px; text-align: center;">
    <div style="font-size: 40px; margin-bottom: 10px;">👑</div>
    <div style="color: #555; font-weight: bold; margin-bottom: 5px;">Final Form</div>
    <div style="color: #555; font-size: 12px;">Unlock the final slime evolution</div>
  </div>
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #555; border-radius: 8px; padding: 15px; text-align: center;">
    <div style="font-size: 40px; margin-bottom: 10px;">👹</div>
    <div style="color: #555; font-weight: bold; margin-bottom: 5px;">Boss Slayer</div>
    <div style="color: #555; font-size: 12px;">Defeat the Giant Slime Boss</div>
  </div>
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #555; border-radius: 8px; padding: 15px; text-align: center;">
    <div style="font-size: 40px; margin-bottom: 10px;">📋</div>
    <div style="color: #555; font-weight: bold; margin-bottom: 5px;">Task Master</div>
    <div style="color: #555; font-size: 12px;">Complete all 10 challenges</div>
  </div>
</div>

## 📦 Item Collection with Stats

<div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(100px, 1fr)); gap: 10px; margin: 20px 0;">
  <div style="background: #2d2d2d; border: 2px solid #555; border-radius: 8px; padding: 10px; text-align: center; position: relative;">
    <div style="font-size: 40px;">✨</div>
    <div style="color: #ffff55; font-size: 11px; font-weight: bold;">Evolution Stone</div>
    <div style="color: #4ade80; font-size: 10px;">✨ Epic</div>
    <div style="color: #ff5555; font-size: 9px;">Instant Evolution</div>
  </div>
  <div style="background: #2d2d2d; border: 2px solid #ffaa00; border-radius: 8px; padding: 10px; text-align: center; position: relative;">
    <div style="font-size: 40px;">⚔️</div>
    <div style="color: #ffff55; font-size: 11px; font-weight: bold;">Dream Sword</div>
    <div style="color: #ffaa00; font-size: 10px;">🌟 Legendary</div>
    <div style="color: #ff5555; font-size: 9px;">Attack: 12 | Speed: +0.5</div>
  </div>
  <div style="background: #2d2d2d; border: 2px solid #ffaa00; border-radius: 8px; padding: 10px; text-align: center; position: relative;">
    <div style="font-size: 40px;">🗡️</div>
    <div style="color: #ffff55; font-size: 11px; font-weight: bold;">Techno Sword</div>
    <div style="color: #ffaa00; font-size: 10px;">🌟 Legendary</div>
    <div style="color: #ff5555; font-size: 9px;">Attack: 14 | Knockback: +2</div>
  </div>
  <div style="background: #2d2d2d; border: 2px solid #555; border-radius: 8px; padding: 10px; text-align: center; position: relative;">
    <div style="font-size: 40px;">🍖</div>
    <div style="color: #ffff55; font-size: 11px; font-weight: bold;">Slime Food</div>
    <div style="color: #4ade80; font-size: 10px;">🟢 Common</div>
    <div style="color: #ff5555; font-size: 9px;">Health: +5 | Damage: +1</div>
  </div>
  <div style="background: #2d2d2d; border: 2px solid #555; border-radius: 8px; padding: 10px; text-align: center; position: relative;">
    <div style="font-size: 40px;">🪰</div>
    <div style="color: #ffff55; font-size: 11px; font-weight: bold;">Frog Food</div>
    <div style="color: #4ade80; font-size: 10px;">🟢 Common</div>
    <div style="color: #ff5555; font-size: 9px;">Health: +3 | Jump: +1</div>
  </div>
  <div style="background: #2d2d2d; border: 2px solid #5555ff; border-radius: 8px; padding: 10px; text-align: center; position: relative;">
    <div style="font-size: 40px;">📖</div>
    <div style="color: #ffff55; font-size: 11px; font-weight: bold;">Task Book</div>
    <div style="color: #5555ff; font-size: 10px;">🔵 Rare</div>
    <div style="color: #ff5555; font-size: 9px;">Task Tracking</div>
  </div>
</div>

## ⭐ Evolution Skill Tree

<div style="display: flex; flex-direction: column; gap: 15px; margin: 20px 0;">
  <div style="background: linear-gradient(135deg, #1a3a1a 0%, #0d2d0d 100%); border: 2px solid #4ade80; border-radius: 10px; padding: 15px; display: flex; align-items: center; gap: 15px;">
    <div style="width: 50px; height: 50px; background: #333; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px;">⚔️</div>
    <div>
      <div style="color: #4ade80; font-weight: bold;">Basic Attack</div>
      <div style="color: #aaa; font-size: 12px;">Your helper can attack nearby mobs</div>
      <div style="color: #4ade80; font-size: 11px;">✓ Unlocked</div>
    </div>
  </div>
  <div style="background: linear-gradient(135deg, #1a3a1a 0%, #0d2d0d 100%); border: 2px solid #4ade80; border-radius: 10px; padding: 15px; display: flex; align-items: center; gap: 15px;">
    <div style="width: 50px; height: 50px; background: #333; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px;">📦</div>
    <div>
      <div style="color: #4ade80; font-weight: bold;">Mob Collection</div>
      <div style="color: #aaa; font-size: 12px;">Helper automatically collects drops</div>
      <div style="color: #4ade80; font-size: 11px;">✓ Unlocked</div>
    </div>
  </div>
  <div style="background: linear-gradient(135deg, #1a3a1a 0%, #0d2d0d 100%); border: 2px solid #4ade80; border-radius: 10px; padding: 15px; display: flex; align-items: center; gap: 15px;">
    <div style="width: 50px; height: 50px; background: #333; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px;">⭐</div>
    <div>
      <div style="color: #4ade80; font-weight: bold;">Evolution Stage 1</div>
      <div style="color: #aaa; font-size: 12px;">Unlock first evolution stage</div>
      <div style="color: #4ade80; font-size: 11px;">✓ Unlocked (Kill 10 mobs)</div>
    </div>
  </div>
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #555; border-radius: 10px; padding: 15px; display: flex; align-items: center; gap: 15px;">
    <div style="width: 50px; height: 50px; background: #333; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px;">⭐⭐</div>
    <div>
      <div style="color: #555; font-weight: bold;">Evolution Stage 2</div>
      <div style="color: #aaa; font-size: 12px;">Unlock second evolution stage</div>
      <div style="color: #ff5555; font-size: 11px;">🔒 Locked (Kill 25 mobs)</div>
    </div>
  </div>
  <div style="background: linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%); border: 2px solid #555; border-radius: 10px; padding: 15px; display: flex; align-items: center; gap: 15px;">
    <div style="width: 50px; height: 50px; background: #333; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px;">👑</div>
    <div>
      <div style="color: #555; font-weight: bold;">Final Evolution</div>
      <div style="color: #aaa; font-size: 12px;">Unlock ultimate slime form</div>
      <div style="color: #ff5555; font-size: 11px;">🔒 Locked (Defeat Giant Slime Boss)</div>
    </div>
  </div>
</div>

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