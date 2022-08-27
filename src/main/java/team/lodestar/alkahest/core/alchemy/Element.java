package team.lodestar.alkahest.core.alchemy;

import java.util.Locale;

public enum Element {
    WATER,
    EARTH,
    FIRE,
    AIR;

    public static Element byName(String name) {
        for(Element element : values()) {
            if(element.name().toLowerCase(Locale.ROOT).equals(name)) {
                return element;
            }
        }
        return null;
    }
}
