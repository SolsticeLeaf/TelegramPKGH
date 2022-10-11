package dev.pkgh.sdk.type;

/**
 * @author winston
 */
public enum UserPermission {

    USER,
    ADMINISTRATOR;

    final int mask = 1 << ordinal();

}
