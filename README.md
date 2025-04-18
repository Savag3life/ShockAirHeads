# Shock AirHeads
A simple but more creative solution to the typical need for an NPC/Citizen. Instead, you can add flare & personality to your features & guide players towards the "shiny" things you want them to interact with.

## Features
- Create unlimited unique AirHeads, put them everywhere!
- Configure each AirHeads animation, appearance, and behavior.
- Lightweight & fast. No real entities bogging down your server.
- Powerful developer API for spawning AirHeads wherever & whenever you want.
- [Hologram Bridge Support](https://github.com/Chubbyduck1/HologramBridge) - Use Holograms, HolographicDisplays, CMI, or DecentHolograms.
- Scale heads up to 10x normal size, and down to 5% of normal size.
- Sends messages, play sounds, dispatch console commands or execute commands as the player.
- Does __not require__ [SVCommonsLib](https://supremeventures.ca/discord)! But you should still check us out :p

![airheads](https://github.com/Savag3life/ShockAirHeads/blob/main/assets/animation.gif)
![airheads-1](https://github.com/Savag3life/ShockAirHeads/blob/main/assets/interact.gif)

## Config Example
```yml
float-animation-offset-ticks: 20
air-heads:
  # This is the "name" of the airhead, it can be anything you want.
  store-airhead:
    # Location of the airhead, this is where it will spawn.
    location: 
      world: minecraft:spawn # World NamespacedKey 
      x: 5.0
      y: 65.0
      z: 20.0
      yaw: 0.0 # Used when the head is not rotated.
      pitch: 0.0 # Used when the head is not rotated.
    # Hologram text above or below the head.
    hologram-text: 
      - <gradient:#ffee25:#f9f2a1><bold>Server Store
      - <gray>store.supremeventures.ca
    # Used to place the hologram above or below the head. Negative values will place the hologram below the head.
    hologram-offset: 1.0 
    # Head base64 texture.
    head-texture: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7...GYyMDBlYzdlODg2NDJkIn19fQ=="
    # Commands executed as the player interacting.
    interact-commands: 
      - buy
    # Commands executed by console when Airhead is interacted with.
    console-commands: []
    # The message sent in chat when the player interacts with the Airhead.
    interact-message:
      - ''
    # Plays a sound when the player interacts with the Airhead.
    sound-settings:
      volume: 1.0
      pitch: 1.0
      # Either `minecraft:` for vanilla sounds or `plugin:` for custom sounds.
      # Minecraft sound list: https://www.digminecraft.com/lists/sound_list_pc.php
      sound: minecraft:entity.player.levelup
      enabled: true # false to disable sound.
    # Animation settings.
    do-float: true
    float-up-max: 0.5
    float-down-max: 0.5
    float-cycle-duration-ticks: 80
    do-rotation: true
    rotation-per-tick: 5
    # Change the size of the Airhead entity. Min = 0.05, Max = 10.0
    scale: 1.0
  # Add more sections for more Airheads, or use `/airheads create <name>` in-game. 
  discord-airhead:
    location:
      world: minecraft:spawn
      x: 10.0
      y: 65.0
      z: 26.0
      yaw: 0.0
      pitch: 0.0
    hologram-text:
      - <gradient:#a403ff:#d5b7fd><bold>Server Discord
      - <gray>supremeventures.ca/discord
    hologram-offset: 3.3
    head-texture: "eyJ0ZXh0dXJlcyI6eyJTS0lO...TAxZWQ2MTYzMTk0MjA0NSJ9fX0="
    interact-commands:
      - discord
    console-commands: []
    interact-message: []
    sound-settings:
      volume: 1.0
      pitch: 1.0
      sound: minecraft:entity.player.levelup
      enabled: true
    do-float: true
    float-up-max: 0.5
    float-down-max: 0.5
    float-cycle-duration-ticks: 80
    do-rotation: true
    rotation-per-tick: 5
    scale: 4.0
```
