package com.github.protospigot.protocol;

/**
 * Represents a type of protocol.
 */
public enum ProtocolType {
    /**
     * A protocol type used by 1.6 and earlier clients.
     */
    LEGACY,
    /**
     * A protocol type used by 1.7 and newer clients.
     */
    MODERN
}