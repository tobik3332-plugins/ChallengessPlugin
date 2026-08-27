package cz.craft.challengess;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        if (input.contains("&")) {
            return LEGACY.deserialize(input)
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        return MINI_MESSAGE.deserialize(input)
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
