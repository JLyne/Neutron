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
package me.crypnotic.neutron.module.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.velocitypowered.api.command.CommandMeta;
import me.crypnotic.neutron.api.StateResult;
import me.crypnotic.neutron.api.command.CommandWrapper;
import me.crypnotic.neutron.api.module.Module;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class CommandModule extends Module {

    private final Map<Commands, CommandWrapper> commands = new HashMap<>();

    @Override
    public StateResult init() {
        ConfigurationNode options = getRootNode().node("options");
        if (options.virtual()) {
            getNeutron().getLogger().warn("No config entry found for command module options");
            return StateResult.fail();
        }

        for (Commands spec : Commands.values()) {
            ConfigurationNode node = options.node(spec.getKey());
            if (node.virtual()) {
				getNeutron().getLogger().warn("No config entry for command: {}", spec.getKey());
                continue;
            }

            CommandWrapper wrapper = spec.getSupplier().get();

			List<String> aliases;
			try {
				aliases = node.node("aliases").getList(String.class);
			} catch (SerializationException e) {
				throw new RuntimeException(e);
			}

			wrapper.setEnabled(node.node("enabled").getBoolean());
            wrapper.setAliases(aliases.toArray(new String[0]));

            if (wrapper.isEnabled()) {
                CommandMeta meta = getNeutron().getProxy().getCommandManager()
                        .metaBuilder(wrapper.getAliases()[0]).aliases(wrapper.getAliases()).build();
                getNeutron().getProxy().getCommandManager().register(meta, wrapper);
            }

            commands.put(spec, wrapper);
        }

        return StateResult.success();
    }

    @Override
    public StateResult reload(ConfigurationNode configuration) {
        return StateResult.of(shutdown(), init());
    }

    @Override
    public StateResult shutdown() {
        commands.values().stream().map(CommandWrapper::getAliases).flatMap(Arrays::stream)
                .forEach(getNeutron().getProxy().getCommandManager()::unregister);

        commands.clear();

        return StateResult.success();
    }

    @Override
    public String getName() {
        return "command";
    }
}
