package com.gpw.radar.service.mapper;

import org.modelmapper.ModelMapper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DtoMapper<T, W> {

    protected ModelMapper modelMapper = new ModelMapper();
    protected Class<W> destinationClass;

    public DtoMapper(Class<W> destinationClass) {
        this.destinationClass = destinationClass;
    }

    public List<W> mapToDto(Collection<T> source) {
        List<W> result = new LinkedList<>();
        source.forEach(e -> result.add(mapToDto(e)));
        return result;
    }

    @SuppressWarnings("unchecked")
    public W mapToDto(T source) {
        return modelMapper.map(source, destinationClass);
    }
}
