package de.oliver.fancyholograms.storage.json;

import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.storage.json.model.*;

public class JsonAdapter {

    public static JsonHologramData hologramDataToJson(HologramData data) {
        return new JsonHologramData(
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
    }

    public static JsonDisplayHologramData displayHologramDataToJson(DisplayHologramData data) {
        return new JsonDisplayHologramData(
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
    }

    public static JsonTextHologramData textHologramDataToJson(TextHologramData data) {
        return new JsonTextHologramData(
                data.getText(),
                data.hasTextShadow(),
                data.isSeeThrough(),
                data.getTextAlignment(),
                data.getTextUpdateInterval(),
                data.getBackground().toString()
        );
    }

    public static JsonBlockHologramData blockHologramDataToJson(BlockHologramData data) {
        return new JsonBlockHologramData(
                data.getBlock().name()
        );
    }

    public static JsonItemHologramData itemHologramDataToJson(ItemHologramData data) {
        return new JsonItemHologramData(
                new String(data.getItemStack().serializeAsBytes())
        );
    }

    public static JsonDataUnion toUnion(TextHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonTextHologramData textHologramData = textHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                textHologramData,
                null,
                null
        );
    }

    public static JsonDataUnion toUnion(ItemHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonItemHologramData itemHologramData = itemHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                null,
                itemHologramData,
                null
        );
    }

    public static JsonDataUnion toUnion(BlockHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonBlockHologramData blockHologramData = blockHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                null,
                null,
                blockHologramData
        );
    }

    public static HologramData fromJson(JsonDataUnion data) {
        return null;
    }
}
