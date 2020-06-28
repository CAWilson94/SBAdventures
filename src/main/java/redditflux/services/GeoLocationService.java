package redditflux.services;

import reactor.core.publisher.Mono;
import redditflux.models.GeographicCoordinates;

public interface GeoLocationService {

    Mono<GeographicCoordinates> fromAddress(Mono<String> addressMono);
}
