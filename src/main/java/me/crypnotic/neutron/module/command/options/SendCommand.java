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
import java.util.stream.Collectors;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import com.velocitypowered.api.proxy.server.ServerInfo;
import me.crypnotic.neutron.api.Neutron;
import me.crypnotic.neutron.api.command.CommandContext;
import me.crypnotic.neutron.api.command.CommandWrapper;
import me.crypnotic.neutron.api.locale.LocaleMessage;

public class SendCommand extends CommandWrapper {

    @Override
    public void handle(CommandSource source, CommandContext context) {
        assertPermission(source, "neutron.command.send");
        assertUsage(source, context.size() > 1);

        RegisteredServer targetServer = getNeutron().getProxy().getServer(context.get(1).toLowerCase()).orElse(null);
        assertNotNull(source, targetServer, LocaleMessage.UNKNOWN_SERVER, Collections.singletonMap("server", context.get(1)));
        ServerInfo info = targetServer.getServerInfo();

        switch (context.get(0).toLowerCase()) {
        case "current":
            assertPlayer(source, LocaleMessage.PLAYER_ONLY_SUBCOMMAND);

            Player player = (Player) source;
            ServerConnection currentServer = player.getCurrentServer().orElse(null);
            assertNotNull(player, currentServer, LocaleMessage.NOT_CONNECTED_TO_SERVER);

            currentServer.getServer().getPlayersConnected().forEach(targetPlayer -> {
                targetPlayer.createConnectionRequest(targetServer).fireAndForget();
                message(targetPlayer, LocaleMessage.SEND_MESSAGE, Collections.singletonMap("server", info.getName()));
            });

            message(player, LocaleMessage.SEND_CURRENT, Collections.singletonMap("server", info.getName()));
            break;
        case "all":
            getNeutron().getProxy().getAllPlayers().forEach(targetPlayer -> {
                targetPlayer.createConnectionRequest(targetServer).fireAndForget();
                message(targetPlayer, LocaleMessage.SEND_MESSAGE, Collections.singletonMap("server", info.getName()));
            });

            message(source, LocaleMessage.SEND_ALL, Collections.singletonMap("server", info.getName()));
            break;
        default:
            Player targetPlayer = getNeutron().getProxy().getPlayer(context.get(0)).orElse(null);
            assertNotNull(source, targetPlayer, LocaleMessage.UNKNOWN_PLAYER,
                          Collections.singletonMap("player", context.get(0)));
            assertVisiblePlayer(source, targetPlayer, LocaleMessage.UNKNOWN_PLAYER,
                                Collections.singletonMap("player", context.get(0)));

            targetPlayer.createConnectionRequest(targetServer).fireAndForget();
            message(targetPlayer, LocaleMessage.SEND_MESSAGE,
                    Collections.singletonMap("server", info.getName()));
            message(source, LocaleMessage.SEND_PLAYER, Map.of(
                    "player", targetPlayer.getUsername(), "server", info.getName()));
            break;
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] arguments = invocation.arguments();

        if(arguments.length < 2) {
            String query = arguments.length == 1 ? arguments[0] : "";

            List<String> result = Neutron.getNeutron().getSuperVanishBridgeHelper()
                    .getUsernameSuggestions(query, invocation.source());

            /* Inject `current`/`all` subcommands */
            result.add("current");
            result.add("all");

            return result;
        } else {
            return getNeutron().getProxy().matchServer(arguments[1]).stream().map(s -> s.getServerInfo().getName())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String getUsage() {
        return "/send (player / current / all) (server)";
    }
}
