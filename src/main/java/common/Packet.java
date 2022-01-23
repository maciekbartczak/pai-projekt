package common;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class Packet implements Serializable {

    public final PacketType type;
    public final PacketContent content;

    public enum PacketType {
        REGISTER,
        LOGIN,
        SEARCH,
        REMIND_PASSWORD
    }
}
