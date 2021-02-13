package thedrake.ui;

import javafx.scene.layout.Pane;

public class BasicView extends Pane {

    private final ViewType viewType;

    public BasicView(ViewType viewType) {
        this.viewType = viewType;

        setPrefSize(100, 100);
    }

    public void clearMove() {}

    public ViewType getType() {
        return viewType;
    }
}


