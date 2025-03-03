package de.oliver.fancyholograms.trait.builtin;

import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.trait.HologramTrait;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApiStatus.Experimental
public class MultiplePagesTrait extends HologramTrait {

    private static final int NEXT_PAGE_DELAY_SECONDS = 5;

    private final List<Page> pages;
    private int currentPageIdx;

    public MultiplePagesTrait() {
        super("multiple_pages");
        this.pages = new ArrayList<>();
        this.currentPageIdx = 0;

        this.pages.add(new Page(List.of("Page 1", "Line 1", "Line 2")));
        this.pages.add(new Page(List.of("Page 2", "Line 1", "Line 2")));
        this.pages.add(new Page(List.of("Page 3", "Line 1", "Line 2")));
    }

    @Override
    public void onAttach() {
        if (!(hologram.getData() instanceof TextHologramData td)) {
            throw new IllegalStateException("Hologram must be text hologram to use MultiplePagesTrait");
        }

        hologramThread.scheduleWithFixedDelay(() -> {
            Page currentPage = pages.get(currentPageIdx);
            td.setText(new ArrayList<>(currentPage.lines()));

            currentPageIdx = (currentPageIdx + 1) % pages.size(); // cycle through pages
        }, 0, NEXT_PAGE_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void save() {
        //TODO save pages to data
    }

    @Override
    public void load() {
        //TODO load pages from data
    }

    record Page(List<String> lines) {

        public void addLine(String line) {
            lines.add(line);
        }

        public void removeLine(int index) {
            lines.remove(index);
        }
    }
}
