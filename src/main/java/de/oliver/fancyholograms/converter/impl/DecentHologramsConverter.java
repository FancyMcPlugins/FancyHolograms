package de.oliver.fancyholograms.converter.impl;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.HologramType;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.converter.HologramConverter;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DecentHologramsConverter implements HologramConverter {

    public static final DecentHologramsConverter INSTANCE = new DecentHologramsConverter();
    private static final HologramManager FH_MANAGER = FancyHolograms.get().getHologramManager();

    @Override
    public HologramData convert(String name) {
        Hologram hologram = DHAPI.getHologram(name);

        String fhName = name;

        if (FH_MANAGER.getHologram(fhName).isPresent()) {
            fhName += "-dh";
        }

        if (FH_MANAGER.getHologram(fhName).isPresent()) {
            fhName += "-" + UUID.randomUUID();
        }

        DisplayHologramData displayData = DisplayHologramData.getDefault(hologram.getLocation());
        displayData.setVisibilityDistance(hologram.getDisplayRange());

        TextHologramData textData = TextHologramData.getDefault(fhName);
        textData.setTextUpdateInterval(hologram.getUpdateInterval());

        List<String> lines = new ArrayList<>();
        for (HologramPage page : hologram.getPages()) {
            for (HologramLine line : page.getLines()) {
                lines.add(HologramConverter.legacyColorCodesToMiniMessages(line.getText()));
            }
        }

        textData.setText(lines);

        HologramData data = new HologramData(fhName, displayData, HologramType.TEXT, textData);
        de.oliver.fancyholograms.api.Hologram fancyHologram = FH_MANAGER.create(data);
        fancyHologram.showHologram(Bukkit.getOnlinePlayers());
        FancyHolograms.get().getHologramsManager().addHologram(fancyHologram);

        DHAPI.removeHologram(name);
        return data;
    }

    @Override
    public List<HologramData> convertAll() {
        Set<String> hologramNames = DecentHologramsAPI.get().getHologramManager().getHologramNames();
        List<HologramData> data = new ArrayList<>(hologramNames.size());

        for (String name : hologramNames) {
            data.add(convert(name));
        }

        return data;
    }
}
