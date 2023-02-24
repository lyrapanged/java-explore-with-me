package ru.practicum;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class StatsClient {

    private final WebClient webClient;

    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        return webClient
                .post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(endpointHitDto), EndpointHitDto.class)
                .retrieve()
                .bodyToMono(EndpointHitDto.class)
                .block();
    }

    public List<ViewStatsDto> get(String start, String end, List<String> uris, boolean unique) {
        return Collections.singletonList(webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", "{start}")
                        .queryParam("end", "{end}")
                        .queryParam("uris[]", "uris", "uris")
                        .queryParam("unique", "{unique}")
                        .build())
                .retrieve()
                .bodyToMono(ViewStatsDto.class)
                .block());
    }
}
