package com.gpw.radar.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Mapper<T, W> {

    public List<W> transformFromJsonToDomainObject(Message message, Class jsonObj, Class clazz) throws IOException {
        return transformToDomainObject(deserializeFromJson(message, jsonObj), clazz);
    }

    public List<W> transformToDomainObject(List<T> source, Class clazz) {
        ModelMapper modelMapper = new ModelMapper();
        List<W> objectStream = (List<W>) source.stream().map(e -> modelMapper.map(e, clazz)).collect(Collectors.toList());
        return objectStream;
    }

    public List<T> deserializeFromJson(Message message, Class clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory t = TypeFactory.defaultInstance();
        List<T> list = mapper.readValue(new String(message.getBody()), t.constructCollectionType(ArrayList.class, clazz));
        return list;
    }
}
