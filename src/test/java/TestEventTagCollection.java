import model.EventTagCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class TestEventTagCollection {
    Map<String, String> testMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        testMap.clear();
    }

    @Test
    void testEmptyConstructor() {
        EventTagCollection collection = new EventTagCollection();
        assertEquals(collection.getTags().size(), 0);
    }

    @Test
    void testConstructorWithNull() {
        EventTagCollection collection = new EventTagCollection(null);
        assertEquals(collection.getTags().size(), 0);
    }

    @Test
    void testConstructorWithEmptyString() {
        EventTagCollection collection = new EventTagCollection("");
        assertEquals(collection.getTags().size(), 0);
    }

    @Test
    void testConstructorWithOnePair() {
        String constructorInput = "hasAirFiltration=Yes";
        EventTagCollection collection = new EventTagCollection(constructorInput);
        testMap.put("hasAirFiltration", "Yes");

        assertEquals(collection.getTags(), testMap);
        assertEquals(collection.getValueFor("hasAirFiltration"), "Yes");
    }

    @Test
    void testConstructorWithIllegalInput() {
        String constructorInput = "Dog Friendly=";
        EventTagCollection collection = new EventTagCollection(constructorInput);
        assertEquals(collection.getTags().size(), 0);
    }

    @Test
    void testConstructorWithIllegalInput2() {
        String constructorInput = "Wrong Format";
        EventTagCollection collection = new EventTagCollection(constructorInput);
        assertEquals(collection.getTags().size(), 0);
    }

    @Test
    void testGetValueForExistingValue() {
        String constructorInput = "hasAirFiltration=true,venueCapacity=<20";
        EventTagCollection collection = new EventTagCollection(constructorInput);
        assertEquals(collection.getTags().size(), 2);
        assertEquals(collection.getValueFor("hasAirFiltration"), "true");
        assertEquals(collection.getValueFor("venueCapacity"), "<20");
    }

    @Test
    void testGetValueForNonexistingValue() {
        EventTagCollection collection = new EventTagCollection();
        assertEquals(collection.getValueFor("hasAirFiltration"), null);
    }

    @Test
    void testGetValueForNullTagName() {
        EventTagCollection collection = new EventTagCollection();
        assertEquals(collection.getValueFor(null), null);
    }

    @Test
    void testToString() {
        String constructorInput = "hasAirFiltration=true,venueCapacity=<20";
        EventTagCollection collection = new EventTagCollection(constructorInput);
        String collectionToString = "EventTagCollection{tags={hasAirFiltration=true, venueCapacity=<20}}";
        assertEquals(collectionToString, collection.toString());
    }
}
