package external;

import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import model.TransportMode;

public class OfflineMapSystem implements MapSystem{
    private GraphHopper hopper;
    public OfflineMapSystem() {
        hopper = new GraphHopper();
    }

    @Override
    public GHPoint convertToCoordinates(String address) {
        return null;
    }

    @Override
    public boolean isPointWithinMapBounds(GHPoint addressPoint) {
        return false;
    }

    @Override
    public ResponsePath routeBetweenPoints(TransportMode transportMode, GHPoint startAddress, GHPoint destinationAddress) {
        return null;
    }

    @Override
    public Translation getTranslation() {
        return null;
    }
}
