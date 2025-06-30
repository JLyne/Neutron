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
package me.crypnotic.neutron.module.command.options;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;

import me.crypnotic.neutron.api.Neutron;
import me.crypnotic.neutron.api.command.CommandContext;
import me.crypnotic.neutron.api.command.CommandWrapper;
import me.crypnotic.neutron.api.locale.LocaleMessage;

public class FindCommand extends CommandWrapper {

    @Override
    public void handle(CommandSource source, CommandContext context) {
        assertPermission(source, "neutron.command.find");
        assertUsage(source, context.size() > 0);

        Player target = getNeutron().getProxy().getPlayer(context.get(0)).orElse(null);
        assertNotNull(source, target, LocaleMessage.UNKNOWN_PLAYER, Collections.singletonMap("player", context.get(0)));
        assertVisiblePlayer(source, target, LocaleMessage.UNKNOWN_PLAYER, Collections.singletonMap("player", context.get(0)));

        ServerConnection server = target.getCurrentServer().get();
        /* We'll consider this offline as the Player is in a limbo state */
        assertNotNull(source, server, LocaleMessage.PLAYER_OFFLINE, Collections.singletonMap("player", context.get(0)));

        message(source, LocaleMessage.FIND_MESSAGE, Map.of(
                "player", target.getUsername(), "server", server.getServerInfo().getName()));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] arguments = invocation.arguments();
        String query = arguments.length > 0 ? arguments[0] : "";
        return Neutron.getNeutron().getVanishBridgeHelper().getUsernameSuggestions(query, invocation.source());
    }

    @Override
    public String getUsage() {
        return "/find (player)";
    }
}
