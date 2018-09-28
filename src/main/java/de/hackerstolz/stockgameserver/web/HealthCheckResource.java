package de.hackerstolz.stockgameserver.web;

import de.hackerstolz.stockgameserver.service.AnalysesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/isalive")
public class HealthCheckResource {

    private final AnalysesService analysesService;

    public HealthCheckResource(AnalysesService analysesService) {
        this.analysesService = analysesService;
    }

    @GetMapping
    public ResponseEntity<Void> isAlive() {
        analysesService.count();
        return ResponseEntity.ok().build();
    }
}
