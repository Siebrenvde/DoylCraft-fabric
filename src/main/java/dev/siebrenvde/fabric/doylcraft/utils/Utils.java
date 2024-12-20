package dev.siebrenvde.fabric.doylcraft.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.*;

import java.util.UUID;

public class Utils {

    public static Text entityComponent(MutableText component, EntityType type, UUID uuid, Text name) {
        Style style = component.getStyle();
        style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(
            type,
            uuid,
            name != null ? name : Text.translatable(type.getTranslationKey())
        )));
        if(type == EntityType.PLAYER && name != null) {
            style = style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + name.getString() + " "));
        } else {
            style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, uuid.toString()));
        }
        component.setStyle(style);
        return component;
    }

    public static Text entityComponent(MutableText component, Entity entity) {
        return entityComponent(component, entity.getType(), entity.getUuid(), entity.getCustomName());
    }

    public static Text entityComponent(MutableText component, PlayerEntity player) {
        return entityComponent(component, EntityType.PLAYER, player.getUuid(), player.getName());
    }

}
