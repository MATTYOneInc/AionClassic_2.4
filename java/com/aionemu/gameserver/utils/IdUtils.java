package com.aionemu.gameserver.utils;

public class IdUtils {
    public IdUtils() {
    }

    public static long long2UInt32(long id) {
        return id > 0L ? id : id & 4294967295L;
    }

    public static long int2UInt32(int id) {
        return id > 0 ? (long)id : (long)id & 4294967295L;
    }
}
