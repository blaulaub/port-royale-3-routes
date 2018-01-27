package ch.patchcode.port_royale_3.routes;

import java.util.Set;

public interface DistanceData {

    Set<String> getPlaces();

    Double getDistance(String fromPlace, String toPlace);

}