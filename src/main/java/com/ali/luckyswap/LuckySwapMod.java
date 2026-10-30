package com.ali.luckyswap;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LuckySwapMod implements ModInitializer {
    private static final int SWAP_TICKS = 20 * 60 * 10; // 10 minutes
    private static final int GIVE_TICKS = 20 * 10;      // 10 seconds
    private static final ResourceLocation LUCKY_BLOCK_ID =
            new ResourceLocation("lucky", "lucky_block");

    private static int swapTimer = SWAP_TICKS;
    private static int giveTimer = GIVE_TICKS;
    private static Item luckyBlockItem;

    @Override
    public void onInitialize() {
        // Resolve the item after all mods have registered their content.
        luckyBlockItem = Registry.ITEM.get(LUCKY_BLOCK_ID);
        if (luckyBlockItem == Registry.ITEM.get(new ResourceLocation("minecraft", "air"))) {
            System.err.println("[LuckySwap] ERROR: Could not find lucky:lucky_block. Make sure Lucky Block Fabric is installed.");
        } else {
            System.out.println("[LuckySwap] Found Lucky Block item: " + LUCKY_BLOCK_ID);
        }

        ServerTickEvents.END_SERVER_TICK.register(LuckySwapMod::onTick);
    }

    private static void onTick(MinecraftServer server) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) return;

        if (--giveTimer <= 0) {
            giveTimer = GIVE_TICKS;
            giveLuckyBlockToEveryone(players);
        }

        if (--swapTimer == 60 || swapTimer == 40 || swapTimer == 20) {
            int seconds = swapTimer / 20;
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("Player swap in " + seconds + "..."), false);
        }

        if (swapTimer <= 0) {
            swapTimer = SWAP_TICKS;
            swapPlayers(server, players);
        }
    }

    private static void giveLuckyBlockToEveryone(List<ServerPlayer> players) {
        if (luckyBlockItem == null ||
                luckyBlockItem == Registry.ITEM.get(new ResourceLocation("minecraft", "air"))) {
            return;
        }

        for (ServerPlayer player : players) {
            ItemStack stack = new ItemStack(luckyBlockItem);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }

    private static void swapPlayers(MinecraftServer server, List<ServerPlayer> players) {
        int count = players.size();
        if (count < 2) {
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("Not enough players to swap!"), false);
            return;
        }

        List<PlayerPosition> positions = new ArrayList<>();
        for (ServerPlayer player : players) {
            positions.add(new PlayerPosition(
                    player.serverLevel(),
                    player.getX(), player.getY(), player.getZ(),
                    player.getYRot(), player.getXRot()));
        }

        for (int i = 0; i < count; i++) {
            ServerPlayer player = players.get(i);
            PlayerPosition destination = positions.get((i + 1) % count);
            player.teleportTo(destination.level, destination.x, destination.y,
                    destination.z, destination.yaw, destination.pitch);
        }

        server.getPlayerList().broadcastSystemMessage(
                Component.literal("SWAP! Everyone changed locations!"), false);
    }

    private static class PlayerPosition {
        final ServerLevel level;
        final double x, y, z;
        final float yaw, pitch;

        PlayerPosition(ServerLevel level, double x, double y, double z,
                       float yaw, float pitch) {
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
}
