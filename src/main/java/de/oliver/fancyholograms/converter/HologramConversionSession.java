package de.oliver.fancyholograms.converter;

import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HologramConversionSession {
    private final @NotNull ConverterTarget target;
    private final @NotNull CommandSender invoker;
    private final String[] arguments;

    public HologramConversionSession(
        @NotNull ConverterTarget target
    ) {
        this(target, Bukkit.getConsoleSender(), new String[0]);
    }

    public HologramConversionSession(
        @NotNull ConverterTarget target,
        @NotNull CommandSender invoker,
        @NotNull String[] arguments
    ) {
        this.target = target;
        this.invoker = invoker;
        this.arguments = arguments;
    }

    public @NotNull ConverterTarget getTarget() {
        return this.target;
    }

    public @NotNull CommandSender getInvoker() {
        return this.invoker;
    }

    public @NotNull String[] getAdditionalArguments() {
        return this.arguments;
    }

    public void logUnsuccessfulConversion(@NotNull String oldHologram, @Nullable String message) {
        if (message != null) {
            MessageHelper.error(
                getInvoker(),
                String.format("There was an issue converting %s: %s", oldHologram, message)
            );
        } else {
            MessageHelper.error(
                getInvoker(),
                String.format("There was an issue converting %s!", oldHologram)
            );
        }
    }

    public void logSuccessfulConversion(@NotNull String oldHologram, @NotNull HologramData result) {
        logSuccessfulConversion(oldHologram, List.of(result));
    }

    public void logSuccessfulConversion(@NotNull String oldHologram, @NotNull List<HologramData> results) {
        MessageHelper.info(
            getInvoker(),
            String.format("Successfully converted %s to %s hologram(s).", oldHologram, results.size())
        );

        for (@NotNull HologramData data : results) {
            MessageHelper.info(
                getInvoker(),
                String.format(" - %s type: %s", data.getName(), data.getType().name())
            );
        }
    }
}
