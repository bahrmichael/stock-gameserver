package de.hackerstolz.stockgameserver.web;

import de.hackerstolz.stockgameserver.model.Analysis;
import de.hackerstolz.stockgameserver.service.AnalysesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analyses/")
public class AnalysesResource {

    private final AnalysesService analysesService;

    public AnalysesResource(AnalysesService analysesService) {
        this.analysesService = analysesService;
    }

    @GetMapping
    public List<Analysis> getAnalyses() {
        return analysesService.findAll();
    }
}
