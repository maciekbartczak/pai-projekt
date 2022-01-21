package server.user;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UserRepository {

    private final XmlMapper xmlMapper;
    private final String filename;

    public UserRepository(String filename) {
        this.xmlMapper = XmlMapper.builder()
                .defaultUseWrapper(false)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        this.filename = filename;
    }

    public synchronized void saveAll(UserDataEntries userDataEntries) {
        try {
            xmlMapper.writeValue(new File(filename), userDataEntries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized UserDataEntries findAll() {
        try {
            return xmlMapper.readValue(new File(filename), UserDataEntries.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
