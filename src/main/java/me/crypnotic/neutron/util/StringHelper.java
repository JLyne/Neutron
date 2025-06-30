/*
* This file is part of Neutron, licensed under the MIT License
*
* Copyright (c) 2020 Crypnotic <crypnoticofficial@gmail.com>
* Copyright (c) 2020 Contributors
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
package me.crypnotic.neutron.util;

import java.util.*;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerPing.SamplePlayer;

import me.crypnotic.neutron.api.Neutron;
import me.crypnotic.neutron.api.locale.LocaleMessage;
import me.crypnotic.neutron.api.locale.LocaleMessageTable;
import me.crypnotic.neutron.manager.locale.LocaleManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class StringHelper {

    public static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    public static String format(String message, Map<String, String> stringReplacements) {
        for (Map.Entry<String, String> entry : stringReplacements.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return message;
    }

    public static Component formatComponent(String message) {
        return formatComponent(message, Collections.emptyMap(), Collections.emptyMap());
    }

    public static Component formatComponent(String message, Map<String, String> stringReplacements, Map<String, ComponentLike> componentReplacements) {
        TagResolver.@NotNull Builder placeholders = TagResolver.builder();

        for (Map.Entry<String, String> entry : stringReplacements.entrySet()) {
            placeholders.resolver(Placeholder.parsed(entry.getKey(), entry.getValue()));
        }

        for (Map.Entry<String, ComponentLike> entry : componentReplacements.entrySet()) {
            placeholders.resolver(Placeholder.component(entry.getKey(), entry.getValue()));
        }

        return miniMessage.deserialize(message, placeholders.build());
    }

    public static SamplePlayer[] toSamplePlayerArray(List<String> input) {
        SamplePlayer[] result = new SamplePlayer[input.size()];
        for (int i = 0; i < input.size(); i++) {
            result[i] = new SamplePlayer(legacySerializer.serialize(formatComponent(input.get(i))), UUID.randomUUID());
        }
        return result;
    }

    public static void sendComponent(CommandSource source, LocaleMessage message, Map<String, String> stringReplacements, Map<String, ComponentLike> componentReplacements) {
        source.sendMessage(getComponent(source, message, stringReplacements, componentReplacements));
    }

    public static Component getComponent(CommandSource source, LocaleMessage message, Map<String, String> stringReplacements, Map<String, ComponentLike> componentReplacements) {
        LocaleManager manager = Neutron.getNeutron().getLocaleManager();
        Locale locale = manager.getDefaultLocale();
        if (source instanceof Player) {
            locale = ((Player) source).getPlayerSettings().getLocale();
        }

        LocaleMessageTable table = manager.get(locale);
        String text = table != null ? table.get(message) : message.getDefaultMessage();

        return formatComponent(text, stringReplacements, componentReplacements);
    }
}
