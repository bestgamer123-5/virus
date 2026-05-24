package com.example.rpgminecraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityCombatEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpgMinecraftMod implements ModInitializer {
    public static final String MOD_ID = "rpgminecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Item RPG_SWORD = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "rpg_sword"),
            new SwordItem(
                    ToolMaterial.DIAMOND,
                    new Item.Settings().maxCount(1).attributeModifiers(
                            SwordItem.createAttributeModifiers(ToolMaterial.DIAMOND, 6, -2.2f)
                    )
            )
    );

    @Override
    public void onInitialize() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof PlayerEntity player) {
                awardExperience(player, killedEntity);
            }
        });

        LOGGER.info("RPG Minecraft loaded! Defeat mobs to gain bonus XP with your RPG sword.");
    }

    private static void awardExperience(PlayerEntity player, LivingEntity defeatedEntity) {
        ItemStack mainHand = player.getMainHandStack();
        if (!mainHand.isOf(RPG_SWORD)) {
            return;
        }

        int bonusXp = Math.max(1, defeatedEntity.getMaxHealth() >= 20 ? 4 : 2);
        player.addExperience(bonusXp);
        LOGGER.debug("Granted {} bonus XP to {}", bonusXp, player.getName().getString());
    }
}
