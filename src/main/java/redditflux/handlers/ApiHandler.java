package redditflux.handlers;

import org.springframework.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import redditflux.models.SunriseSunset;
import redditflux.services.GeoLocationService;

public class ApiHandler {

    private static final String ADDRESS = "address";
    private static final String EMPTY_STRING = "";

    //private final ErrorHandler errorHandler;

    private final GeoLocationService geoLocationService;
    private final SunriseSunsetService sunriseSunsetService;

    public ApiHandler(final GeoLocationService geoLocationService, final SunriseSunsetService sunriseSunsetService,
                      final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.geoLocationService = geoLocationService;
        this.sunriseSunsetService = sunriseSunsetService;
    }

    public Mono<org.springframework.web.reactive.function.server.ServerResponse> postLocation(final ServerRequest request) {
        return request.bodyToMono(LocationRequest.class)
                .map(LocationRequest::getAddress)
                .onErrorResume(throwable -> Mono.just(EMPTY_STRING))
                .transform(this::buildResponse)
                .onErrorResume(errorHandler::throwableError);
    }

    public Mono<org.springframework.web.reactive.function.server.ServerResponse> getLocation(final ServerRequest request) {
        return Mono.just(request.pathVariable(ADDRESS))
                .transform(this::buildResponse)
                .onErrorResume(errorHandler::throwableError);
    }

    Mono<org.springframework.web.reactive.function.server.ServerResponse> buildResponse(final Mono<String> address) {
        return address
                .transform(geoLocationService::fromAddress)
                .zipWhen(this::sunriseSunset, LocationResponse::new)
                .transform(this::serverResponse);
    }

    private Mono<SunriseSunset> sunriseSunset(GeographicCoordinates geographicCoordinates) {
        return Mono.just(geographicCoordinates).transform(sunriseSunsetService::fromGeographicCoordinates);
    }

    Mono<org.springframework.web.reactive.function.server.ServerResponse> serverResponse(Mono<LocationResponse> locationResponseMono) {
        return locationResponseMono.flatMap(locationResponse ->
                ServerResponse.ok().body(Mono.just(locationResponse), LocationResponse.class));
    }
}
