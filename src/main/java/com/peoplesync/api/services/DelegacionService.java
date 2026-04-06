package com.peoplesync.api.services;

import com.peoplesync.api.models.Delegacion;
import com.peoplesync.api.repositories.DelegacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DelegacionService {

    private final DelegacionRepository delegacionRepository;

    public List<Delegacion> obtenerTodas() {
        return delegacionRepository.findAll();
    }
}