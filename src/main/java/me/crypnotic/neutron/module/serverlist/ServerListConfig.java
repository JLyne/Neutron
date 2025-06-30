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
package me.crypnotic.neutron.module.serverlist;

import java.util.List;

import me.crypnotic.neutron.util.StringHelper;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ServerListConfig {
    @Setting("motd")
    private final Component motd = StringHelper.miniMessage.deserialize("<gray>This velocity proxy is proudly powered by <aqua>Neutron");

    @Setting("player-count")
    private final PlayerCount playerCount = new PlayerCount();

    @Setting("server-preview")
    private final ServerPreview serverPreview = new ServerPreview();

    public Component getMotd() {
        return motd;
    }

    public PlayerCount getPlayerCount() {
        return playerCount;
    }

    public ServerPreview getServerPreview() {
        return serverPreview;
    }

    @ConfigSerializable
    public static class PlayerCount {
        @Setting(value = "action")
        @Comment(value = """
				      # The server list player count has three different actions:\r
				        # \r
				        # CURRENT - player count matches the number of players online\r
				        # ONEMORE - player count shows the number of players online plus 1 \r
				        # PING - player count shows the sum of all backend servers' max player counts. Cached every 5 minutes\r
				        # STATIC - player count will always be the number defined under `player-count`\r
				        #\r
				        # `player-count` is only used with the STATIC player count type\
				""")
        private final PlayerCountAction action = PlayerCountAction.STATIC;

        @Setting("player-count")
        private final int maxPlayerCount = 500;

        public enum PlayerCountAction {
            CURRENT,
            ONEMORE,
            PING,
            STATIC
        }

        public int getMaxPlayerCount() {
            return maxPlayerCount;
        }

        public PlayerCountAction getAction() {
            return action;
        }
    }

    @ConfigSerializable
    public static class ServerPreview {
        @Setting(value = "action")
        @Comment(value = """
				      # The server list preview has three different actions:\r
				        # \r
				        # MESSAGE - preview will show the messages defined under `messages`\r
				        # PLAYERS - preview matches the vanilla server preview of showing online players\r
				        # EMPTY - preview is empty\r
				        #\r
				        # `messages` is only used with the MESSAGE preview type\
				""")
        private final ServerPreviewAction action = ServerPreviewAction.MESSAGE;

        @Setting("messages")
        private final List<String> messages = List.of("&7Powered by a &bNeutron");

        public enum ServerPreviewAction {
            EMPTY,
            MESSAGE,
            PLAYERS
        }

        public ServerPreviewAction getAction() {
            return action;
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}
