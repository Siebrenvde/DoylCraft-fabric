package dev.siebrenvde.fabric.doylcraft.utils;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Messages {

    public static Text error(MutableText message, Exception e) {
        if(e != null) {
            MutableText tc = Text.literal(e.getClass().getSimpleName()).formatted(Colours.ERROR);
            if(e.getMessage() != null) {
                tc = tc.append(Text.literal("\n"));
                tc = tc.append(Text.literal(e.getMessage()).formatted(Colours.DATA));
            }
            message = message.setStyle(message.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tc)));
        }
        return message;
    }

    public static Text error(String message, Exception e) { return error(Text.literal(message).formatted(Colours.ERROR), e); }
    public static Text error(MutableText message) { return error(message, null); }
    public static Text error(String message) { return error(message, null); }

}
