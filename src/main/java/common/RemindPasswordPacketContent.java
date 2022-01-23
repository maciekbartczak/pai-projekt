package common;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class RemindPasswordPacketContent implements PacketContent, Serializable {
    public final String username;
}
