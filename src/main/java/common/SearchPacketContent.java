package common;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class SearchPacketContent implements PacketContent, Serializable {
    public final String query;
}
