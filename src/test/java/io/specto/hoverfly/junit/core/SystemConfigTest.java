package io.specto.hoverfly.junit.core;


import org.junit.Test;

import static io.specto.hoverfly.junit.core.SystemConfigFactory.ArchType.ARCH_386;
import static io.specto.hoverfly.junit.core.SystemConfigFactory.ArchType.ARCH_AMD64;
import static io.specto.hoverfly.junit.core.SystemConfigFactory.DEFAULT_BINARY_NAME_FORMAT;
import static io.specto.hoverfly.junit.core.SystemConfigFactory.OsName.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SystemConfigTest {

    private SystemConfig systemConfig;

    @Test
    public void shouldGetHoverflyBinaryNameForHostSystem() {

        systemConfig = new SystemConfig(LINUX, ARCH_AMD64, DEFAULT_BINARY_NAME_FORMAT);

        assertThat(systemConfig.getHoverflyBinaryName()).isEqualTo("hoverfly_linux_amd64");
    }

    @Test
    public void shouldGeHoverflyBinaryNameForWindowsSystem() {

        systemConfig = new SystemConfig(WINDOWS, ARCH_386, DEFAULT_BINARY_NAME_FORMAT);

        assertThat(systemConfig.getHoverflyBinaryName()).isEqualTo("hoverfly_windows_386.exe");

    }

    @Test
    public void shouldGeHoverflyBinaryNameForMacSystem() {

        systemConfig = new SystemConfig(OSX, ARCH_AMD64, DEFAULT_BINARY_NAME_FORMAT);

        assertThat(systemConfig.getHoverflyBinaryName()).isEqualTo("hoverfly_OSX_amd64");

    }
}