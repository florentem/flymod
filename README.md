# FlyMod

A lightweight client-side Fabric mod for Minecraft 26.2 that plays a realistic 3D fly buzzing sound when players zoom past you in flight.

## Features
- **Dynamic 3D Audio**: Plays an insect buzzing sound for nearby flying players (Creative flight, Elytra gliding, and Trident Riptide launches).
- **Speed & Distance Scaling**: Sound volume scales with proximity and velocity—the closer and faster they fly, the louder it gets. Completely silent when hovering still or standing.
- **Doppler Effect**: Pitch realistically rises on approach and drops as the player flies away.
- **Resource Pack Support**: Override the default sound with any custom mono `.ogg` file placed at `assets/flymod/sounds/fly_buzz.ogg` (see [`example_resourcepack`](example_resourcepack/)).

## Configuration (Cloth Config & ModMenu)
- **Enable Fly Sound**: Master toggle for the mod.
- **Master Volume**: Adjusts sound level (0% - 200%).
- **Max Hearing Distance**: Audibility range in blocks (5 - 64).
- **Speed Sensitivity**: Multiplier for flight speed amplification.
- **Doppler Pitch Shift**: Enables or disables realistic pitch shift.
- **Play For Yourself**: Enable buzzing for your own flight (off by default).
- **Include Elytra / Trident**: Toggle detection for Elytra gliding and Riptide Trident launches in Survival.
