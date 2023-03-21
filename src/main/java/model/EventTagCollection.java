package model;

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

    /**
     * Create a new EventTagCollection with the String valuesOfEachTag
     * @param valuesOfEachTag  The String has the form "name1=value2,name2=value2" and etc. and will be parsed to obtain
     * the map of tags with their corresponding Tags.
     */
    public EventTagCollection(String valuesOfEachTag) {
        this();
        if (valuesOfEachTag != null) {
            String[] pairs = valuesOfEachTag.split(",");
            for (String pair : pairs) {
                String[] tagAndValue = pair.split("=");
                String tagName = tagAndValue[0];
                String value = tagAndValue[1];
                tags.put(tagName, value);
            }
        }
    }

    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * Obtain the value associated with the tagName.
     * @param tagName The name of the Tag of which we want to obtain the value.
     * @return        The value associated with the tagName. null if the tagName doesn't exist
     */
    public String getValueFor(String tagName) {
        if (tagName == null) {
            return null;
        }
        return tags.get(tagName);
    }
}
