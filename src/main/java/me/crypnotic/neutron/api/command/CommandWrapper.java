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
package me.crypnotic.neutron.api.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import me.crypnotic.neutron.NeutronPlugin;
import me.crypnotic.neutron.api.Neutron;
import me.crypnotic.neutron.api.locale.LocaleMessage;
import me.crypnotic.neutron.util.StringHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import java.util.Collections;
import java.util.Map;

public abstract class CommandWrapper implements SimpleCommand {
    private boolean enabled;
    private String[] aliases;

    @Override
    public void execute(Invocation invocation) {
        try {
            handle(invocation.source(), new CommandContext(invocation.arguments()));
        } catch (CommandExitException exception) {
            /* Catch silently */
        }
    }

    public void assertUsage(CommandSource source, boolean assertion) {
        assertCustom(source, assertion, LocaleMessage.INVALID_USAGE, Collections.singletonMap("usage", getUsage()), Collections.emptyMap());
    }

    public void assertPlayer(CommandSource source, LocaleMessage message) {
        assertCustom(source, source instanceof Player, message, Collections.emptyMap(), Collections.emptyMap());
    }

    public void assertVisiblePlayer(CommandSource source, Player target, LocaleMessage message, Map<String, String> stringReplacements) {
        boolean assertResult = !(source instanceof Player) || Neutron.getNeutron().getVanishBridgeHelper()
                .canSee((Player) source, target);
        assertCustom(source, assertResult, message, stringReplacements, Collections.emptyMap());
    }

    public void assertNotNull(CommandSource source, Object value, LocaleMessage message) {
        assertCustom(source, value != null, message, Collections.emptyMap(), Collections.emptyMap());
    }

    public void assertNotNull(CommandSource source, Object value, LocaleMessage message, Map<String, String> stringReplacements) {
        assertCustom(source, value != null, message, stringReplacements, Collections.emptyMap());
    }

    public void assertPermission(CommandSource source, String permission) {
        assertCustom(source, source.hasPermission(permission), LocaleMessage.NO_PERMISSION);
    }

    public void assertCustom(CommandSource source, boolean assertion, LocaleMessage message) {
        assertCustom(source, assertion, message, Collections.emptyMap(), Collections.emptyMap());
    }

    public void assertCustom(CommandSource source, boolean assertion, LocaleMessage message, Map<String, String> stringReplacements, Map<String, ComponentLike> componentReplacements) {
        if (!assertion) {
            message(source, message, stringReplacements, componentReplacements);

            throw new CommandExitException();
        }
    }

    public void message(CommandSource source, LocaleMessage message) {
        StringHelper.sendComponent(source, message, Collections.emptyMap(), Collections.emptyMap());
    }

    public void message(CommandSource source, LocaleMessage message, Map<String, String> stringReplacements) {
        StringHelper.sendComponent(source, message, stringReplacements, Collections.emptyMap());
    }

    public void message(CommandSource source, LocaleMessage message, Map<String, String> stringReplacements, Map<String, ComponentLike> componentReplacements) {
        StringHelper.sendComponent(source, message, stringReplacements, componentReplacements);
    }

    public Component getMessage(CommandSource source, LocaleMessage message, Map<String, String> stringReplacements, Map<String, ComponentLike> componentReplacements) {
        return StringHelper.getComponent(source, message, stringReplacements, componentReplacements);
    }

    public abstract void handle(CommandSource source, CommandContext context) throws CommandExitException;

    public abstract String getUsage();

    public static class CommandExitException extends RuntimeException {
        private static final long serialVersionUID = -1299193476106186693L;
    }

    public NeutronPlugin getNeutron() {
        return Neutron.getNeutron();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }
}
