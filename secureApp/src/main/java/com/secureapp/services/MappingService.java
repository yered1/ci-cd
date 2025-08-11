package com.secureapp.services;

import java.lang.reflect.Field;
import java.util.Set;

public class MappingService {
    public <T> void bindAllowList(Object dto, T entity, Set<String> allowed) {
        if (dto == null || entity == null) return;
        for (Field f : dto.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                Object v = f.get(dto);
                if (v == null || !allowed.contains(f.getName())) continue;
                Field ef = entity.getClass().getDeclaredField(f.getName());
                ef.setAccessible(true);
                ef.set(entity, v);
            } catch (Exception ignore) {}
        }
    }
}
