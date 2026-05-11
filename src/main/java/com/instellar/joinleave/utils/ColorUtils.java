package com.instellar.joinleave.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts config strings that may contain {@code &} color codes and
 * {@code &#RRGGBB} hex codes into Adventure {@link Component}s.
 *
 * <p>Processing pipeline:
 * <ol>
 *   <li>{@code &#RRGGBB} → {@code §x§R§R§G§G§B§B}
 *       (the "unusual X-repeated" wire format that Minecraft clients understand)</li>
 *   <li>{@code &X}       → {@code §X}
 *       (standard color/format codes, without using the deprecated ChatColor class)</li>
 *   <li>Full legacy string → Adventure {@link Component}
 *       via a serializer built with {@code .hexColors().useUnusualXRepeatedCharacterHexFormat()}</li>
 * </ol>
 */
public final class ColorUtils {

    // &#RRGGBB — hex color shorthand used in config strings
    private static final Pattern HEX_PATTERN =
            Pattern.compile("&#([A-Fa-f0-9]{6})");

    // &X — valid Minecraft color/format code characters (case-insensitive)
    private static final Pattern AMPERSAND_PATTERN =
            Pattern.compile("&([0-9a-fA-FrRkKlLmMnNoO])");

    /*
     * BUG FIX: LegacyComponentSerializer.legacySection() does NOT parse hex
     * colors by default.  We must explicitly call:
     *   .hexColors()                           → enable hex parsing
     *   .useUnusualXRepeatedCharacterHexFormat() → use the §x§R§R§G§G§B§B
     *                                             wire format that Minecraft uses
     */
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.builder()
                    .character(LegacyComponentSerializer.SECTION_CHAR)
                    .hexColors()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();

    private ColorUtils() {}

    /**
     * Parses {@code text} into an Adventure {@link Component}, handling
     * both {@code &} color codes and {@code &#RRGGBB} hex codes.
     */
    public static Component parseColors(String text) {
        String withHex    = convertHexCodes(text);        // &#RRGGBB → §x§R§R§G§G§B§B
        String withLegacy = translateAmpersandCodes(withHex); // &X     → §X
        return LEGACY.deserialize(withLegacy);
    }

    /**
     * Converts every {@code &#RRGGBB} token to the Minecraft
     * {@code §x§R§R§G§G§B§B} hex-color format.
     */
    private static String convertHexCodes(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buf = new StringBuilder(text.length() + 16);
        while (matcher.find()) {
            String hex = matcher.group(1); // e.g. "99CCFF"
            StringBuilder repl = new StringBuilder(14).append('\u00a7').append('x');
            for (char c : hex.toCharArray()) {
                repl.append('\u00a7').append(c); // §9 §9 §C §C §F §F
            }
            matcher.appendReplacement(buf, Matcher.quoteReplacement(repl.toString()));
        }
        matcher.appendTail(buf);
        return buf.toString();
    }

    /**
     * Replaces {@code &X} with {@code §X} for valid Minecraft color/format
     * code characters.  Uses Pattern instead of the deprecated
     * {@code ChatColor.translateAlternateColorCodes}.
     */
    private static String translateAmpersandCodes(String text) {
        // $1 is the captured code character; none of 0-9/a-f/k-o/r are regex-special
        return AMPERSAND_PATTERN.matcher(text).replaceAll("\u00a7$1");
    }
}
