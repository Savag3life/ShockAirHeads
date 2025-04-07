# Shock AirHeads
A simple but more creative solution to the typical need for an NPC/Citizen. Instead, you can add flare & personality to your features & guide players towards the "shiny" things you want them to interact with.

## Features
- Create unlimited unique AirHeads, put them everywhere!
- Configure each AirHeads animation, appearance, and behavior.
- Lightweight, no dependencies, and easy to use.
- [Hologram Bridge Support](https://github.com/Chubbyduck1/HologramBridge) - Use Holograms, HolographicDisplays, CMI, or DecentHolograms.
- Scale heads up to 10x normal size, and down to 5% of normal size.
- Sends messages, play sounds, dispatch console commands or execute commands as the player.
- Does __not require__ [SVCommonsLib](https://supremeventures.ca/discord)! But you should still check us out :p

![airheads](https://github.com/Savag3life/ShockAirHeads/blob/main/assets/animation.gif)
![airheads-1](https://github.com/Savag3life/ShockAirHeads/blob/main/assets/interact.gif)
![airheads-2](https://github.com/Savag3life/ShockAirHeads/blob/main/assets/scale.gif)

## Config Example
```yml
# Create unlimited AirHeads. Just repeat the following
# - location: down to rotation-per-ticks:
air-heads:
- location:
    world: minecraft:spawn # World must be a namespace key! `minecraft:<world-name>`
    x: 0.0
    y: 69
    z: 22
  # The text displayed on the hologram above the airhead.
  # Supports Decent Holograms, HolographicDisplays, CMI, and Holograms.
  # Leave empty: `hologram-text: []` to disable.
  hologram-text:
  - <gradient:#a403ff:#d5b7fd><bold>Server Discord
  - <gray>supremeventures.ca/discord
  # The base64 texture of the head skin used on the AirHead.
  # You can find skin textures, and their Base64 equivilent on 
  # https://minecraft-heads.com/custom-heads
  # Make sure to scroll down to "For Developers" and copy the `Value`
  head-texture: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM5ZWU3MTU0OTc5YjNmODc3MzVhMWM4YWMwODc4MTRiNzkyOGQwNTc2YTI2OTViYTAxZWQ2MTYzMTk0MjA0NSJ9fX0=
  # The commands executed by the interactor, when the AirHead is interacted.
  interact-commands:
  - discord
  # Should the airhead float up and down during its animation?
  do-float: true
  # The min/max distance the Airhead floats up/down.
  float-up-max: 0.5
  float-down-max: 0.5
  # The speed the AirHead floats up/down. Larger = Slower (smoother)
  float-cycle-duration-ticks: 80
  # Should the AirHead Spin during its animation?
  do-rotation: true
  # How fast the head should spin. Larger = faster (less smooth)
  rotation-per-tick: 5
  # The scale of the airhead. Must be between 0.05 & 10.0
  scale: 4.0
  # Message send when the Airhead is interacted
  # Supports MiniMessage <color> formatting only.
  interact-message: []
  # The sound to play when the AirHead is interacted.
  sound-settings:
    volume: 1.0
    pitch: 1.0
    sound: BLOCK_NOTE_BLOCK_HAT
    enabled: true
  # The distance above the head, to place the hologram.
  # Holograms are automatically placed at the top of the armor stand
  # No matter the scale. `hologram-offset` is in addition to the armorstand 
  # bounding box height. 
  hologram-offset: 1.3
  # Commands executed as ConsoleCommandSender when interacted.
  console-commands: []
```
