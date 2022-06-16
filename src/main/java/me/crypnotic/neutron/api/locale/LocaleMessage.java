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
package me.crypnotic.neutron.api.locale;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LocaleMessage {
    FIND_MESSAGE("<aqua><player> <gray>is connected to <aqua><server>"),

    INFO_HEADER("<bold><gray>==> Information for player: <aqua><player>"),
    INFO_LOCALE("<gray>Locale: <aqua><locale>"),
    INFO_PING("<gray>Ping: <aqua><ping>"),
    INFO_SERVER("<gray>Current Server: <aqua><server>"),
    INFO_UUID("<gray>Unique Id: <aqua><uuid>"),
    INFO_VERSION("<gray>Minecraft Version: <aqua><version>"),

    INVALID_USAGE("<red>Usage: <usage>"),

    LIST_HEADER("<green>There are currently <aqua><online> <green>players online<br><gray><italic>Hover over a server to see the players online"),
    LIST_MESSAGE("<green>[<server>] <yellow><online> online"),

    NO_PERMISSION("<red>You don't have permission to execute this command."),
    NOT_CONNECTED_TO_SERVER("<red>You must be connected to a server to use this subcommand."),

    SEND_ALL("<green>All players have been sent to <aqua><server>"),
    SEND_CURRENT("<green>All players from your current server have been sent to <aqua><server>"),
    SEND_MESSAGE("<green>You have been sent to <aqua><server>"),
    SEND_PLAYER("<aqua><player> <green>has been sent to <aqua><server>"),

    PLAYER_OFFLINE("<red><player> is currently offline."),
    PLAYER_ONLY_COMMAND("<red>Only players can use this subcommand."),
    PLAYER_ONLY_SUBCOMMAND("<red>Only players can use this subcommand."),

    UNKNOWN_PLAYER("<red>Unknown player: <player>"),
    UNKNOWN_SERVER("<red>Unknown server: <server>");

    @Getter
    private final String defaultMessage;
    @Getter
    private final String name = toString().toLowerCase();

    public static LocaleMessage match(String key) {
        try {
            return LocaleMessage.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
