package com.gpw.radar.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonTransformer<T> {

    private ObjectMapper mapper = new ObjectMapper();

    public List<T> deserializeFromJson(String jsonSource, Class<T> clazz) throws IOException {
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        List<T> readValue = mapper.readValue(jsonSource, typeFactory.constructCollectionType(ArrayList.class, clazz));
        return readValue;
    }

    public List<T> deserializeFromJson(Message message, Class<T> clazz) throws IOException {
        return deserializeFromJson(new String(message.getBody()), clazz);
    }

}
