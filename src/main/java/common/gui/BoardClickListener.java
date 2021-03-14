package common.gui;

import com.google.common.eventbus.Subscribe;
import common.utils.IntVector2;

import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class BoardClickListener {
    private final Consumer<IntVector2> action;

    public BoardClickListener(Consumer<IntVector2> action) {
        this.action = action;
    }

    @Subscribe
    public void clickEvent(IntVector2 event) {
        action.accept(event);
    }
}
