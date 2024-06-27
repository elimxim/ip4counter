package com.github.elimxim.ip4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpV4SupportTest {
    @Test
    void parseInvalidIpAddress() {
        assertThrows(NullPointerException.class, () -> IpV4Support.parseIpAddress(null));
        assertThrows(InvalidIpAddress.class, () -> IpV4Support.parseIpAddress(""));
        assertThrows(InvalidIpAddress.class, () -> IpV4Support.parseIpAddress("."));
        assertThrows(InvalidIpAddress.class, () -> IpV4Support.parseIpAddress("...."));
        assertThrows(InvalidIpAddress.class, () -> IpV4Support.parseIpAddress("1.1.1"));
    }

    @Test
    void parseIpAddressExtraPeriod() {
        assertDoesNotThrow(() -> IpV4Support.parseIpAddress("127.0.0.1."));
        assertThrows(InvalidIpAddress.class, () -> IpV4Support.parseIpAddress(".127.0.0.1"));
    }

    @Test
    void parseIpAddress() {
        assertEquals((int) 0L, IpV4Support.parseIpAddress("0.0.0.0"));
        assertEquals((int) 134744072L, IpV4Support.parseIpAddress("8.8.8.8"));
        assertEquals((int) 2130706433L, IpV4Support.parseIpAddress("127.0.0.1"));
        assertEquals((int) 134743044L, IpV4Support.parseIpAddress("8.8.4.4"));
        assertEquals((int) 3232235777L, IpV4Support.parseIpAddress("192.168.1.1"));
        assertEquals((int) 167772161L, IpV4Support.parseIpAddress("10.0.0.1"));
        assertEquals((int) 3758096385L, IpV4Support.parseIpAddress("224.0.0.1"));
    }

    @Test
    void parseIpAddressWithMaxValueOctets() {
        assertEquals((int) 255L, IpV4Support.parseIpAddress("0.0.0.255"));
        assertEquals((int) 65280L, IpV4Support.parseIpAddress("0.0.255.0"));
        assertEquals((int) 16711680L, IpV4Support.parseIpAddress("0.255.0.0"));
        assertEquals((int) 4278190080L, IpV4Support.parseIpAddress("255.0.0.0"));
        assertEquals((int) 4294967295L, IpV4Support.parseIpAddress("255.255.255.255"));
    }

    @Test
    void parseIpAddressWithOverflowedOctets() {
        assertEquals(0, IpV4Support.parseIpAddress("0.0.0.256"));
        assertEquals(0, IpV4Support.parseIpAddress("0.0.256.0"));
        assertEquals(0, IpV4Support.parseIpAddress("0.256.0.0"));
        assertEquals(0, IpV4Support.parseIpAddress("256.0.0.0"));
        assertEquals(0, IpV4Support.parseIpAddress("256.256.256.256"));
    }
}
