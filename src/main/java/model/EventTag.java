package model;

import java.util.Set;

/**
 * {@link EventTag} aids classification of the {@link Event}s or match of preferences for {@link Consumer}s.
 */
public class EventTag {
    private final Set<String> values;
    private final String defaultValue;

    /**
     * Create a new Tag with the given possible values and a default value.
     * @param values: Possible values of the Tag
     * @param defaultValue: Default value of the Tag
     */
    public EventTag(Set<String> values, String defaultValue) {
        this.defaultValue = defaultValue;
        this.values = values;
    }

    public Set<String> getValues() {
        return values;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
