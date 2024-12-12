package org.example;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class IpPraser {

    private final DatabaseReader dbReader;

    public IpPraser() throws IOException {
        File database = new File("../snaplink/src/main/resources/GeoLite2-City.mmdb");
        this.dbReader = new DatabaseReader.Builder(database).withCache(new CHMCache()).build();
    }

    public String resolveGeolocation(String ipAddress) {

        if (ipAddress == null) {
            return "IP address unavailable";
        }

        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            CityResponse response = dbReader.city(ip);

            String city = response.getCity().getName();
            String region = response.getMostSpecificSubdivision().getName();
            String country = response.getCountry().getName();

            return country;
        } catch (Exception e) {
            return "Unknown Location";
        }
    }
}
