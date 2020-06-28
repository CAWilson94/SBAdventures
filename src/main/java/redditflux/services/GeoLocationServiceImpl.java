package redditflux.services;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import redditflux.models.GeoLocationResponse;
import redditflux.models.GeographicCoordinates;

public class GeoLocationServiceImpl implements GeoLocationService {

    private static final String OK_STATUS = "OK";
    private static final String ZERO_RESULTS = "ZERO_RESULTS";
    private static final String ERROR_GETTING_LOCATION = "error getting location";
    private static final String ERROR_LOCATION_WAS_NULL = "error location was null";
    private static final String ADDRESS_NOT_FOUND = "address not found";
    private static final String ADDRESS_PARAMETER = "?address=";
    private static final String MISSING_ADDRESS = "missing address";

    WebClient webClient; // The star of the show lads
    private final String endPoint; // cheeky wee endpoint ...

    public GeoLocationServiceImpl(final String endPoint) {
        this.endPoint = endPoint;
        this.webClient = WebClient.create();
    }

    @Override
    public Mono<GeographicCoordinates> fromAddress(Mono<String> addressMono) {
        return addressMono
                .transform(this::buildUrl)
                .transform(this::get)
                .transform(this::geometryLocation);
    }

    Mono<String> buildUrl(final Mono<String> addressMono) { // again what is THIS CRAZY
        return addressMono.flatMap(address -> {
            /**
             *
             * add this in later with the other exceptions :)
            if (address.equals("")) {
                return Mono.error(new InvalidParametersException(MISSING_ADDRESS));
            }
             **/
            return Mono.just(endPoint.concat(ADDRESS_PARAMETER).concat(address));
        });
    }

    Mono<GeoLocationResponse> get(final Mono<String> urlMono) {
        return urlMono.flatMap(url -> webClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(GeoLocationResponse.class)));
    }

    Mono<GeographicCoordinates> geometryLocation(final Mono<GeoLocationResponse> geoLocationResponseMono) {
        return geoLocationResponseMono.flatMap(geoLocationResponse -> {
                    if (geoLocationResponse.getStatus() != null) {
                        switch (geoLocationResponse.getStatus()) {
                            case OK_STATUS:
                                return Mono.just(
                                        new GeographicCoordinates(geoLocationResponse.getResults()[0].getGeometry().getLocation().getLat(),
                                                geoLocationResponse.getResults()[0].getGeometry().getLocation().getLng()));
                            case ZERO_RESULTS:
                                return Mono.error(new Error(ADDRESS_NOT_FOUND));
                            default:
                                return Mono.error(new Error(ERROR_GETTING_LOCATION));
                        }
                    } else {
                        return Mono.error(new Error(ERROR_LOCATION_WAS_NULL));
                    }
                }
        );
    }


}
