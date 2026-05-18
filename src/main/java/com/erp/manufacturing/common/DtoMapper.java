package com.erp.manufacturing.common;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoMapper {

    private final ModelMapper modelMapper;

    public <S, T> T map(S source, Class<T> targetType) {
        return modelMapper.map(source, targetType);
    }

    public <S, T> Page<T> mapPage(Page<S> source, Class<T> targetType) {
        return source.map(item -> map(item, targetType));
    }

    public <S, T> void mapToExisting(S source, T destination) {
        modelMapper.map(source, destination);
    }
}
