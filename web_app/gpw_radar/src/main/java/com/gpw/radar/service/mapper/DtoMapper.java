package com.gpw.radar.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class DtoMapper<T, W> {

    protected ModelMapper modelMapper = new ModelMapper();

    public List<W> mapToDto(Collection<T> source, Class<W> clazz) {
        List<W> result = new LinkedList<>();
        source.forEach(e -> result.add(mapToDto(e, clazz)));
        return result;
    }

    public W mapToDto(T source, Class<W> clazz) {
        return modelMapper.map(source, clazz);
    }
}
