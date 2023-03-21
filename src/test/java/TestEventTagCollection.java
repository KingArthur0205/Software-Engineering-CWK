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
    void testConstructorWithEmptyString() {
        EventTagCollection collection = new EventTagCollection(null);
        assertEquals(collection.getTags().size(), 0);
    }

    @Test
    void testConstructorWithOnePair() {
        String constructorInput = "Dog Friendly=Yes";
        EventTagCollection collection = new EventTagCollection(constructorInput);
        testMap.put("Dog Friendly", "Yes");
        assertEquals(collection.getTags(), testMap);
    }

    @Test
    void testConstructorWithIllegalInput() {
        String constructorInput = "Dog Friendly=";
        EventTagCollection collection = new EventTagCollection(constructorInput);
        assertEquals(collection.getTags().size(), 0);
    }
}
