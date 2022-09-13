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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import me.crypnotic.neutron.api.command.CommandContext;
import me.crypnotic.neutron.api.command.CommandWrapper;
import me.crypnotic.neutron.api.locale.LocaleMessage;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;

public class GlistCommand extends CommandWrapper {

    @Override
    public void handle(CommandSource source, CommandContext context) {
        assertPermission(source, "neutron.command.glist");

        long playerCount = getNeutron().getProxy().getAllPlayers().stream()
                .filter(player -> !(source instanceof Player)
                        || getNeutron().getSuperVanishBridgeHelper().canSee((Player) source, player))
                .count();

        message(source, LocaleMessage.LIST_HEADER,
                Collections.singletonMap("online", String.valueOf(playerCount)));

        for (RegisteredServer server : getNeutron().getProxy().getAllServers()) {
            ServerInfo info = server.getServerInfo();
            Collection<Player> players = server.getPlayersConnected().stream()
                    .filter(player -> !(source instanceof Player)
                            || getNeutron().getSuperVanishBridgeHelper().canSee((Player) source, player))
                    .toList();

            String playerString = players.stream().map(Player::getUsername)
                    .sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", "));

            Component message = getMessage(source, LocaleMessage.LIST_MESSAGE,
                                           Map.of(
                                                   "server", info.getName(),
                                                   "online", String.valueOf(players.size()),
                                                   "players", playerString),
                                           Collections.emptyMap());

            source.sendMessage(message, MessageType.SYSTEM);
        }
    }

    @Override
    public String getUsage() {
        return "/glist";
    }
}
