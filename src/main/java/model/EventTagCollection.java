package model;

import com.google.protobuf.MapEntry;
import state.IEventState;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link EventTagCollection} holds a map of names and values, corresponding each to an {@link EventTag} and the selected value.
 */
public class EventTagCollection implements Serializable {
    private final Map<String, String> tags;
    private static final String pattern = "^(\\w+=[^,]+)(,\\w+=[^,]+)*$";

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
            Pattern compiledPattern = Pattern.compile(pattern);

            // Check if the name=value pair matches the pattern
            Matcher matcher = compiledPattern.matcher(valuesOfEachTag);
            if(!matcher.matches()) {
                return ;
            }

            // Split each "name=value" pair to two individual String of name and value and store them in tags.
            String[] pairs = valuesOfEachTag.split(",");

            // A buffer to store the name-value pairs. bufferTags will be set to tags only if the String parsing succeed.
            Map<String, String> bufferTags = new HashMap<>();
            for (String pair : pairs) {
                String[] tagAndValue = pair.split("=");
                // Verify that each pair is parsed into separate name and value
                // assert(tagAndValue.length == 2);
                if (tagAndValue.length != 2) {
                    return;
                }

                String tagName = tagAndValue[0];
                String value = tagAndValue[1];
                bufferTags.put(tagName, value);
            }


            for (Map.Entry<String, String> entry : bufferTags.entrySet()) {
                tags.put(entry.getKey(), entry.getValue());
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
        return tags.get(tagName);
    }
}
