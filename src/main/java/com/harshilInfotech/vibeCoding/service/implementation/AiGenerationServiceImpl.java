package com.harshilInfotech.vibeCoding.service.implementation;

import com.harshilInfotech.vibeCoding.service.AiGenerationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiGenerationServiceImpl implements AiGenerationService {



    @Override
    public Flux<String> streamResponse(String message, Long projectId) {

        return null;

    }

}
