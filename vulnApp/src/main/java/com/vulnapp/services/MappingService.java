package com.vulnapp.services;

import java.lang.reflect.Field;

public class MappingService {
    public void bind(Object dto, Object entity) {
        if (dto == null || entity == null) return;
        for (Field f : dto.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                Object v = f.get(dto);
                try {
                    Field ef = entity.getClass().getDeclaredField(f.getName());
                    ef.setAccessible(true);
                    ef.set(entity, v);
                } catch (NoSuchFieldException ignore) {}
            } catch (Exception ignore) {}
        }
    }
}
