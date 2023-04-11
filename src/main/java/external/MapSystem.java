package external;

import com.graphhopper.ResponsePath;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import model.TransportMode;

public interface MapSystem {
    /**
     * Check the venue address for correctness and whether it fits the map boundaries.
     * @param address The venue address.
     * @return The converted {@link GHPoint} with longitude and altitude. Issue an exception if the conversion cannot
     * be done.
     */
    GHPoint convertToCoordinates(String address);

    /**
     * Check if the addressPoint is within the boundaries of the map
     * @param addressPoint The converted point of the address.
     * @return The result that indicates if the address is within the map boundaries.
     */
    boolean isPointWithinMapBounds(GHPoint addressPoint);

    /**
     * Compute the shortest route between two {@link GHPoint}s, which correspond to the start address and destination
     * address using the given {@link TransportMode}.
     * @return The route between the start and destination address.
     */
    ResponsePath routeBetweenPoints(TransportMode transportMode, GHPoint startAddressPoint, GHPoint destinationAddressPoint);

    Translation getTranslation();
}
