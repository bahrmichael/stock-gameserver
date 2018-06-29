package de.hackerstolz.stockgameserver.service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.hackerstolz.stockgameserver.repositories.RecommendationRepository;
import de.hackerstolz.stockgameserver.model.AnalysisItem;
import de.hackerstolz.stockgameserver.model.Recommendation;
import de.hackerstolz.stockgameserver.model.RssAnalysis;

@Service
public class RecommendationService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;
    private final RecommendationRepository repository;

    public RecommendationService(final RestTemplate restTemplate,
            final RecommendationRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    @Async
    @Scheduled(fixedDelay = 60_000L)
    public void loadRecommendations() {
        log.info("Loading recommendations.");
        getRecommendationLinks().forEach(link -> {
            if (repository.countBySource(link) == 0) {
                log.info("Loading new recommendation from {}", link);
                final Recommendation recommendation = getRecommendationFromLink(link);
                if (recommendation.getSymbol() != null) {
                    log.info("Storing new recommendation: {}", recommendation);
                    repository.save(recommendation);
                }
            }
        });
    }

    List<String> getRecommendationLinks() {
        final String forObject = restTemplate.getForObject("https://www.finanzen.net/rss/analysen",
                                                           String.class, new HashMap<>());
        final RssAnalysis result = JAXB.unmarshal(new StringReader(forObject), RssAnalysis.class);
        return result.getRssChannel().getItem().stream().map(AnalysisItem::getLink).collect(Collectors.toList());
    }

    Recommendation getRecommendationFromLink(final String link) {
        final String result = restTemplate.getForObject(link, String.class, new HashMap<>());

        final int buy = extractNumber("Buy", result);
        final int sell = extractNumber("Sell", result);
        final int hold = extractNumber("Hold", result);
        final String symbol = extract(" Symbol", result);

        return new Recommendation(symbol, buy, hold, sell, link);
    }

    private int extractNumber(final String type, final String input) {
        final String extract = extract(type, input);
        if (null == extract) {
            // Symbol is sometimes missing, therefore we only warn here
            log.warn("Extraction returns 0 for type {} and input '{}'.", type, input);
            return 0;
        } else {
            return Integer.parseInt(extract);
        }
    }

    private String extract(final String type, final String input) {
        final Pattern pattern = Pattern.compile("\\>" + type + ": ([0-9A-Z]+)\\<");
        final Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public List<Recommendation> findAll() {
        return repository.findAll();
    }
}
