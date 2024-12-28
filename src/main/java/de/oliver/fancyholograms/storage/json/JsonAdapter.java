package de.oliver.fancyholograms.storage.json;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.storage.json.model.*;

public class JsonAdapter {

    public static JsonDataUnion toJson(TextHologramData data) {
        JsonHologramData hologramData = new JsonHologramData(
                data.getName(),
                data.getType(),
                new JsonLocation(
                        data.getLocation().getWorld().getName(),
                        data.getLocation().getX(),
                        data.getLocation().getY(),
                        data.getLocation().getZ(),
                        data.getLocation().getYaw(),
                        data.getLocation().getPitch()
                ),
                data.getVisibilityDistance(),
                data.getVisibility(),
                data.getLinkedNpcName()
        );

        JsonDisplayHologramData displayHologramData = new JsonDisplayHologramData(
                new JsonVec3f(
                        data.getScale().x(),
                        data.getScale().y(),
                        data.getScale().z()
                ),
                new JsonVec3f(
                        data.getTranslation().x(),
                        data.getTranslation().y(),
                        data.getTranslation().z()
                ),
                data.getShadowRadius(),
                data.getShadowStrength(),
                data.getBrightness().getBlockLight(),
                data.getBrightness().getSkyLight(),
                data.getBillboard()
        );

        JsonTextHologramData textHologramData = new JsonTextHologramData(
                data.getText(),
                data.hasTextShadow(),
                data.isSeeThrough(),
                data.getTextAlignment(),
                data.getTextUpdateInterval(),
                data.getBackground().toString()
        );

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                textHologramData
        );
    }

    public static HologramData fromJson(JsonDataUnion data) {
        return null;
    }
}
