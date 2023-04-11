package external;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.routing.util.DefaultSnapFilter;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.graphhopper.util.shapes.GHPoint;
import model.TransportMode;

import java.util.Locale;

/**
 * An implementation of {@link MapSystem}.
 * This class provides operations to convert an address String of "lon-lat" form to a {@link GHPoint}, route between two GHPoint,
 * and check if an address is within the boundary.
 *
 * In the system, we set the map to be in Scotland.
 * The possible tranport modes for routing are bike, foot, car, and wheelchair.
 */
public class OfflineMapSystem implements MapSystem{
    private GraphHopper hopper;

    /**
     * Create a {@link MapSystem}
     * Set the map to Scotland and the possible transport modes.
     */
    public OfflineMapSystem() {
        hopper = new GraphHopper();
        hopper.setOSMFile("scotland-latest.osm.pbf");

        hopper.setGraphHopperLocation("target/routing-graph-cache");
        hopper.setProfiles(new Profile("foot").setVehicle("foot"), new Profile("car").setVehicle("car"),
                new Profile("wheelchair").setVehicle("wheelchair"), new Profile("bike").setVehicle("bike"));

        // Setup the map data
        hopper.importOrLoad();
    }

    /**
     * @param address The venue address.
     * @return        The converted {@link GHPoint} of the venue address.
     */
    @Override
    public GHPoint convertToCoordinates(String address) {
        return GHPoint.fromString(address);
    }

    /**
     * @param addressPoint The converted point of the address.
     * @return             A Boolean value indicating whether the venue address is within the boundary of the map.
     */
    @Override
    public boolean isPointWithinMapBounds(GHPoint addressPoint) {
        //return hopper.getBaseGraph().getBounds().contains(addressPoint.getLat(), addressPoint.getLon());
        return hopper.getLocationIndex().findClosest(addressPoint.getLat(), addressPoint.getLon(), DefaultSnapFilter.ALL_EDGES).isValid();
    }

    /**
     * @param transportMode      The {@link TransportMode} used to route between two addresses.
     * @param startAddress       The start address of routing.
     * @param destinationAddress The destination address of routing.
     * @return                   The {@link ResponsePath} between the start address and destination address.
     */
    @Override
    public ResponsePath routeBetweenPoints(TransportMode transportMode, GHPoint startAddress, GHPoint destinationAddress) {
        String transMode = "";
        switch (transportMode) {
            case car -> transMode = "car";
            case bike -> transMode = "bike";
            case foot -> transMode = "foot";
            case wheelchair -> transMode = "wheelchair";
            default -> transMode = "foot";
        }

        GHRequest req = new GHRequest(startAddress, destinationAddress).setProfile(transMode).setLocale(Locale.UK);
        GHResponse rsp = hopper.route(req);
        return rsp.getBest();
    }

    @Override
    public Translation getTranslation() {
        return hopper.getTranslationMap().getWithFallBack(Locale.UK);
    }

    /**
     * Properly shutdown {@link GraphHopper}.
     */
    public void close() {
        hopper.close();
    }

    public GraphHopper getHopper() {
        return hopper;
    }
}
