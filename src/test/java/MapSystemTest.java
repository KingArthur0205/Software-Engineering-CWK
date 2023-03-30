import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import external.OfflineMapSystem;
import model.TransportMode;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class MapSystemTest {
    @Test
    void createHopper() {
        OfflineMapSystem a = new OfflineMapSystem();
        GHPoint b = a.convertToCoordinates("55.944623385511036, -3.189634347715134");
        GHPoint c = a.convertToCoordinates("55.94928537708562, -3.1983032475254674");
        ResponsePath path = a.routeBetweenPoints(TransportMode.car,b, c);

        Translation tr = a.getHopper().getTranslationMap().getWithFallBack(Locale.UK);
        InstructionList il = path.getInstructions();
        // iterate over all turn instructions
        for (Instruction instruction : il) {
            System.out.println("distance " + instruction.getDistance() + " for instruction: " + instruction.getTurnDescription(tr));
        }

        ResponsePath path2 = a.routeBetweenPoints(TransportMode.wheelchair,b, c);
        System.out.println();
        Translation tr2 = a.getHopper().getTranslationMap().getWithFallBack(Locale.UK);
        InstructionList il2 = path.getInstructions();
        // iterate over all turn instructions
        for (Instruction instruction : il2) {
            System.out.println("distance " + instruction.getDistance() + " for instruction: " + instruction.getTurnDescription(tr2));
        }
    }
}
