package com.eduq.quatoca.torneiotmapi.domain.model.enums.converters;

import java.util.stream.Stream;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.CategoriaJogador;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CategoriaJogadorConverter implements AttributeConverter<CategoriaJogador, String> {

    @Override
    public String convertToDatabaseColumn(CategoriaJogador categoria) {
        if (categoria == null) {
            return null;
        }
        return categoria.getValue();
    }

    @Override
    public CategoriaJogador convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return Stream.of(CategoriaJogador.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        }
    }
