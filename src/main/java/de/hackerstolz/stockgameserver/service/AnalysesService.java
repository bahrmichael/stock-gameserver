package de.hackerstolz.stockgameserver.service;

import de.hackerstolz.stockgameserver.model.Analysis;
import de.hackerstolz.stockgameserver.model.AnalysisItem;
import de.hackerstolz.stockgameserver.model.RssAnalysis;
import de.hackerstolz.stockgameserver.repositories.AnalysesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AnalysesService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;
    private final AnalysesRepository repository;

    public AnalysesService(final RestTemplate restTemplate,
            final AnalysesRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    @Async
    @Scheduled(fixedDelay = 60_000L)
    public void loadAnalyses() {
        log.info("Loading analyses.");
        getAnalysisLinks().forEach(link -> {
            if (repository.countBySource(link) == 0) {
                log.info("Loading new analysis from {}", link);
                final Analysis analysis = getAnalysisFromLink(link);
                if (analysis.getSymbol() != null) {
                    log.info("Storing new analysis: {}", analysis);
                    repository.save(analysis);
                }
            }
        });
    }

    List<String> getAnalysisLinks() {
        final String forObject = restTemplate.getForObject("https://www.finanzen.net/rss/analysen",
                                                           String.class, new HashMap<>());
        final RssAnalysis result = JAXB.unmarshal(new StringReader(forObject), RssAnalysis.class);
        return result.getRssChannel().getItem().stream().map(AnalysisItem::getLink).collect(Collectors.toList());
    }

    Analysis getAnalysisFromLink(final String link) {
        final String result = restTemplate.getForObject(link, String.class, new HashMap<>());

        final int buy = extractNumber("Buy", result);
        final int sell = extractNumber("Sell", result);
        final int hold = extractNumber("Hold", result);
        final String symbol = extract(" Symbol", result);

        return new Analysis(symbol, buy, hold, sell, link);
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

    public List<Analysis> findAll() {
        return repository.findAll();
    }

    public long count() {
        return repository.count();
    }
}
