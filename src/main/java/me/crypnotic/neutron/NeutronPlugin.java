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
package me.crypnotic.neutron;

import java.io.File;
import java.nio.file.Path;

import me.crypnotic.neutron.api.serializer.ComponentSerializer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import me.crypnotic.neutron.api.Neutron;
import me.crypnotic.neutron.event.StateHandler;
import me.crypnotic.neutron.manager.ModuleManager;
import me.crypnotic.neutron.manager.locale.LocaleManager;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import uk.co.notnull.vanishbridge.helper.VanishBridgeHelper;

public final class NeutronPlugin {
    @Inject
    private ProxyServer proxy;
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path dataFolderPath;

	private LocaleManager localeManager;

    private ModuleManager moduleManager;

    private VanishBridgeHelper superVanishBridgeHelper;

    public NeutronPlugin() {
        Neutron.setNeutron(this);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
		ConfigurationNode configuration;

		try {
			configuration = loadConfig();
		} catch (ConfigurateException e) {
			getLogger().warn("Failed to load config", e);
            throw new RuntimeException(e);
		}

		StateHandler stateHandler = new StateHandler(this);

        this.localeManager = new LocaleManager(this, configuration);
        this.moduleManager = new ModuleManager(this, configuration);

        stateHandler.init();

        proxy.getEventManager().register(this, new StateHandler(this));
        superVanishBridgeHelper = new VanishBridgeHelper(proxy);
    }

    public ConfigurationNode loadConfig() throws ConfigurateException {
        return HoconConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(Component.class, new ComponentSerializer())))
                .file(
						new File(dataFolderPath.toAbsolutePath().toString(), "config.conf")).build().load();
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataFolderPath() {
        return dataFolderPath;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public VanishBridgeHelper getVanishBridgeHelper() {
        return superVanishBridgeHelper;
    }
}
