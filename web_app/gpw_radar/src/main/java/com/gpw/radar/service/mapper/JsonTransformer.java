package com.gpw.radar.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public List<T> deserializeCollectionFromJson(String jsonSource, Class<T> clazz) throws IOException {
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        List<T> readValue = mapper.readValue(jsonSource, typeFactory.constructCollectionType(ArrayList.class, clazz));
        return readValue;
    }

    public List<T> deserializeCollectionFromJson(Message message, Class<T> clazz) throws IOException {
        return deserializeCollectionFromJson(new String(message.getBody()), clazz);
    }

    public T deserializeObjectFromJson(Message message, Class<T> clazz) throws IOException {
        return mapper.readValue(new String(message.getBody()), clazz);
    }

    public String seralizeObjectToJson(T object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

}
