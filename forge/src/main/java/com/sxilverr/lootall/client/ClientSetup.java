package com.sxilverr.lootall.client;

import com.sxilverr.lootall.Config;

import net.minecraftforge.api.distmarker.Dist;
//? if >=1.19 {
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
//?} else if >=1.17 {
/*import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;*/
//?}
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Config.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    //? if >=1.19 {
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "loot_transfer_feedback",
                (gui, graphics, partialTick, width, height) -> TransferFeedback.render(graphics, width, height));
    }
    //?} else if >=1.17 {
    /*@SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> OverlayRegistry.registerOverlayAbove(
                ForgeIngameGui.HOTBAR_ELEMENT, "loot_transfer_feedback",
                (gui, poseStack, partialTick, width, height) -> TransferFeedback.render(poseStack, width, height)));
    }
    *///?}
}
