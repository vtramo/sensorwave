package org.github.rmiot.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseGeocodingData  {

    public BigDecimal latitude;
    public BigDecimal longitude;
    public String label;
    public String name;
    public BigDecimal distance;
    public String type;
    public String number;
    public String street;

    @JsonProperty("postal_code")
    public String postalCode;
    public BigDecimal confidence;
    public String region;

    @JsonProperty("region_code")
    public String regionCode;

    @JsonProperty("administrative_area")
    public String administrativeArea;

    public String neighbourhood;
    public String country;
    public String county;
    public String locality;

    @JsonProperty("country_code")
    public String countryCode;
    public String continent;

    @JsonProperty("map_url")
    public String mapUrl;
}