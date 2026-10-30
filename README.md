# Lucky Swap — Minecraft 1.19.3 (Fabric)

This project uses the real item registered by the Lucky Block Fabric mod (`lucky:lucky_block`).
It does not create a replacement block and does not use yellow concrete.

## Features
- Gives each online player one real Lucky Block every 10 seconds.
- Swaps all online players' locations every 10 minutes.
- Announces a 3, 2, 1 countdown before the swap.
- If a player's inventory is full, the Lucky Block is dropped at their location.

## Requirements
- Minecraft Java Edition 1.19.3
- Java 17
- Fabric Loader 0.14.17 or newer
- Fabric API for 1.19.3
- Lucky Block Fabric mod compatible with Minecraft 1.19.3

## Install
Install Fabric Loader, Fabric API, Lucky Block, and this mod on the server.
Install the Lucky Block mod on each client as required by that mod. The Lucky Swap mod itself runs server-side.

## Build
Run `gradle build`. The JAR will be in `build/libs/`.
