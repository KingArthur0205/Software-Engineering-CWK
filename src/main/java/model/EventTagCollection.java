package model;

import state.IEventState;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link EventTagCollection} holds a map of names and values, corresponding each to an {@link Event}’s tag name and the
 * value selected for it.
 */
public class EventTagCollection {
    private final Map<String, String> tags;

    /**
     * Create a new EventTagCollection with an empty map of Tags.
     */
    public EventTagCollection() {
        this.tags = new HashMap<String, String>();
    }

    @Override
    public String toString() {
        return "EventTagCollection{" +
                "tags=" + tags +
                '}';
    }

    /**
     * Create a new EventTagCollection with the String valuesOfEachTag
     * @param valuesOfEachTag  The String has the form "name1=value2,name2=value2...". and will be parsed to obtain
     * the map of tags with their corresponding Tags.
     */
    public EventTagCollection(String valuesOfEachTag) {
        this();
        if (valuesOfEachTag != null && !valuesOfEachTag.isBlank()) {
            String[] pairs = valuesOfEachTag.split(",");

            // Split each "name=value" pair to two individual String of name and value and store them in tags.
            for (String pair : pairs) {
                String[] tagAndValue = pair.split("=");
                // Verify that each pair is parsed into separate name and value
                // assert(tagAndValue.length == 2);
                if (tagAndValue.length != 2) {
                    return;
                }

                String tagName = tagAndValue[0];
                String value = tagAndValue[1];
                tags.put(tagName, value);
            }
        }
    }

    /**
     * @return a map of {@link EventTag}s and selected valeus for each of tag
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * Obtain the value associated with the tagName.
     * @param tagName The name of the Tag of which we want to obtain the value.
     * @return        AThe value associated with the tagName. null if the tagName doesn't exist
     */
    public String getValueFor(String tagName) {
        if (tagName == null) {
            return null;
        }
        return tags.get(tagName);
    }
}
