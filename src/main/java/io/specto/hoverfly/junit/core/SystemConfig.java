package io.specto.hoverfly.junit.core;

import io.specto.hoverfly.junit.core.SystemConfigFactory.ArchType;
import io.specto.hoverfly.junit.core.SystemConfigFactory.OsName;

/**
 * Platform specific configuration for hoverfly
 */
class SystemConfig {

    private final OsName osName;
    private final ArchType archType;
    private final String binaryNameFormat;

    SystemConfig(OsName osName, ArchType archType, String binaryNameFormat) {
        this.osName = osName;
        this.archType = archType;
        this.binaryNameFormat = binaryNameFormat;
    }

    OsName getOsName() {
        return osName;
    }

    ArchType getArchType() {
        return archType;
    }

    String getBinaryNameFormat() {
        return binaryNameFormat;
    }

    /**
     * Calculates the binary to used based on OS and architecture
     */
    String getHoverflyBinaryName() {
        String extension = "";
        if (osName == OsName.WINDOWS) {
            extension = ".exe";
        }
        return String.format(binaryNameFormat, osName.getName(), archType.getName(), extension);
    }
}
