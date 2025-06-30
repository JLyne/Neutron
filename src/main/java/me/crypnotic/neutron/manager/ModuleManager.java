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
package me.crypnotic.neutron.manager;

import java.util.HashMap;
import java.util.Map;

import me.crypnotic.neutron.NeutronPlugin;
import me.crypnotic.neutron.api.Reloadable;
import me.crypnotic.neutron.api.StateResult;
import me.crypnotic.neutron.api.module.Module;
import me.crypnotic.neutron.module.command.CommandModule;
import me.crypnotic.neutron.module.serverlist.ServerListModule;
import org.spongepowered.configurate.ConfigurationNode;

public class ModuleManager implements Reloadable {
    private final NeutronPlugin neutron;
    private ConfigurationNode configuration;

    private final Map<Class<? extends Module>, Module> modules = new HashMap<>();

    public ModuleManager(NeutronPlugin neutron, ConfigurationNode configuration) {
        this.neutron = neutron;
        this.configuration = configuration;
    }

    @Override
    public StateResult init() {
        modules.put(CommandModule.class, new CommandModule());
        modules.put(ServerListModule.class, new ServerListModule());

        int enabled = 0;
        for (Module module : modules.values()) {
            ConfigurationNode node = configuration.node(module.getName());
            if (node.virtual()) {
				neutron.getLogger().warn("Failed to load module: {}", module.getName());
                continue;
            }

            module.setEnabled(node.node("enabled").getBoolean());
            if (module.isEnabled()) {
                if (module.init().isSuccess()) {
                    enabled += 1;
                } else {
					neutron.getLogger().warn("Module failed to initialize: {}", module.getName());

                    module.setEnabled(false);
                }
            }
        }

        neutron.getProxy().getEventManager().register(neutron, this);

        // Save configuration after all modules load in order to copy default values
//        configuration.save();

        neutron.getLogger().info("Modules loaded: {} (enabled: {})", modules.size(), enabled);

        return StateResult.success();
    }

    @Override
    public StateResult reload(ConfigurationNode configuration) {
        this.configuration = configuration;

        int enabled = 0;
        for (Module module : modules.values()) {
            ConfigurationNode node = configuration.node(module.getName());
            if (node.virtual()) {
				neutron.getLogger().warn("Failed to reload module: {}", module.getName());
                continue;
            }

            boolean newState = node.node("enabled").getBoolean();

            if (module.isEnabled() && !newState) {
                module.shutdown();

                module.setEnabled(newState);
            } else if (newState) {
                module.setEnabled(newState);

                if (module.reload(configuration).isSuccess()) {
                    enabled += 1;
                } else {
					neutron.getLogger().warn("Module failed to reload: {}", module.getName());

                    module.setEnabled(false);
                }
            }
        }

        neutron.getLogger().info("Modules reloaded: {} (enabled: {})", modules.size(), enabled);

        return StateResult.success();
    }

    @Override
    public StateResult shutdown() {
        neutron.getLogger().info("Shutting down active modules...");

        modules.values().stream().filter(Module::isEnabled).forEach(Module::shutdown);

        return StateResult.success();
    }

    public ConfigurationNode getRoot() {
        return configuration.node();
    }

    @Override
    public String getName() {
        return "ModuleManager";
    }
}
