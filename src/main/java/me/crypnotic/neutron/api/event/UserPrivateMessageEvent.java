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
package me.crypnotic.neutron.api.event;

import java.util.Optional;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.ResultedEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.crypnotic.neutron.api.user.User;
import net.kyori.adventure.text.Component;

public final class UserPrivateMessageEvent implements ResultedEvent<UserPrivateMessageEvent.PrivateMessageResult> {

    @Getter
    private final Optional<User<? extends CommandSource>> sender;
    @Getter
    private final Optional<User<? extends CommandSource>> recipient;
    @Getter
    private final Component message;
    @Getter
    private final boolean reply;
    @Getter
    @Setter
    private PrivateMessageResult result;

    public UserPrivateMessageEvent(Optional<User<? extends CommandSource>> sender, Optional<User<? extends CommandSource>> recipient,
            Component message, boolean reply) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.reply = reply;

        this.result = PrivateMessageResult.create();
    }

    @AllArgsConstructor
    public static final class PrivateMessageResult implements ResultedEvent.Result {

        @Getter
        @Setter
        private Optional<Component> reason;
        @Getter
        private boolean allowed;

        public void allow() {
            this.allowed = true;
        }

        public void deny(Component reason) {
            this.reason = Optional.ofNullable(reason);
            this.allowed = false;
        }

        public static final PrivateMessageResult create() {
            return new PrivateMessageResult(Optional.empty(), true);
        }
    }
}