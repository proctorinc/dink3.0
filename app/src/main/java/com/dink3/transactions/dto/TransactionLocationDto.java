package com.dink3.transactions.dto;

import com.dink3.jooq.tables.pojos.TransactionLocation;
import com.plaid.client.model.Location;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionLocationDto {

    private String address;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private Float lat;
    private Float lon;

    public TransactionLocation toLocation() {
        TransactionLocation location = new TransactionLocation();
        location.setAddress(address);
        location.setCity(city);
        location.setRegion(region);
        location.setPostalCode(postalCode);
        location.setCountry(country);
        location.setLat(lat);
        location.setLon(lon);
        return location;
    }

    public static TransactionLocationDto fromPlaidLocation(
        Location plaidLocation
    ) {
        return TransactionLocationDto.builder()
            .address(plaidLocation.getAddress())
            .city(plaidLocation.getCity())
            .region(plaidLocation.getRegion())
            .postalCode(plaidLocation.getPostalCode())
            .country(plaidLocation.getCountry())
            .lat(
                plaidLocation.getLat() != null
                    ? plaidLocation.getLat().floatValue()
                    : null
            )
            .lon(
                plaidLocation.getLon() != null
                    ? plaidLocation.getLon().floatValue()
                    : null
            )
            .build();
    }

    public static TransactionLocationDto fromLocation(
        TransactionLocation location
    ) {
        return TransactionLocationDto.builder()
            .address(location.getAddress())
            .city(location.getCity())
            .region(location.getRegion())
            .postalCode(location.getPostalCode())
            .country(location.getCountry())
            .lat(
                location.getLat() != null
                    ? plaidLocation.getLat().floatValue()
                    : null
            )
            .lon(
                plaidLocation.getLon() != null
                    ? plaidLocation.getLon().floatValue()
                    : null
            )
            .build();
    }
}
