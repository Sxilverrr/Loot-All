package com.sxilverr.lootall;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class Compat {
    private Compat() {
    }

    public static Level level(Entity entity) {
        //? if >=1.20 {
        return entity.level();
        //?} else {
        /*return entity.level;*/
        //?}
    }

    public static Inventory inventory(Player player) {
        //? if >=1.18 {
        return player.getInventory();
        //?} else {
        /*return player.inventory;*/
        //?}
    }
}
