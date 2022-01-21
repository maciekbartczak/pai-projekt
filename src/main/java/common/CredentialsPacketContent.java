package common;

import lombok.RequiredArgsConstructor;
import server.user.UserData;

import java.io.Serializable;

@RequiredArgsConstructor
public class CredentialsPacketContent implements PacketContent, Serializable {
    public final UserData userData;
}
