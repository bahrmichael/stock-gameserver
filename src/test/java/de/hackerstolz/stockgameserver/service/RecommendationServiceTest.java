package de.hackerstolz.stockgameserver.service;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hackerstolz.stockgameserver.model.Recommendation;

public class RecommendationServiceTest {

    private RestTemplate restTemplate = mock(RestTemplate.class);
    private RecommendationService sut = new RecommendationService(restTemplate, null);

    @Test
    public void getRecommendationFromLink() {
        final String link = "aLink";
        when(restTemplate.getForObject(eq(link), eq(String.class), any(HashMap.class)))
                .thenReturn(" <br /> Symbol: GOOGL</span></h2><div class><span class=\"backgroundGreenWhite "
                            + "pull-left\"></span>Buy: 14</div><div class=\"clearfix\"><span "
                            + "class=\"backgroundYellowWhite pull-left\"></span>Hold: 3</div><div "
                            + "class=\"clearfix\"><span class=\"backgroundRedWhite pull-left\"></span>Sell: "
                            + "0</div></div></div><div class=\"courseAimLineBox clearfix\"><div "
                            + "class=\"courseAimLine\"></div><div class=\"courseAimLabel\" s");

        final Recommendation recommendation = sut.getRecommendationFromLink(link);

        assertEquals(14, recommendation.getBuy());
        assertEquals(0, recommendation.getSell());
        assertEquals(3, recommendation.getHold());
        assertEquals(link, recommendation.getSource());
        assertEquals("GOOGL", recommendation.getSymbol());
    }

    @Test
    public void getRecommendationLinks() {
        when(restTemplate.getForObject(any(String.class), eq(String.class), any(HashMap.class)))
                .thenReturn("<rss version=\"2.0\"><channel><item><link>mylink</link></item></channel></rss>");

        final List<String> recommendation = sut.getRecommendationLinks();

        assertEquals(1, recommendation.size());
        assertEquals("mylink", recommendation.get(0));
    }
}
