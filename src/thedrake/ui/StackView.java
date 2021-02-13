package thedrake.ui;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import thedrake.*;

public class StackView extends BasicView {

    private final TileBackgrounds backgrounds = new TileBackgrounds();

    private final Border selectionBorder = new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))
    );

    private Troop troop;

    private int order;

    private final PlayingSide playingSide;

    private final TileViewContext context;

//    private Move move;

    public StackView(Troop troop, PlayingSide playingSide, int order, TileViewContext context) {
        super(ViewType.STACKVIEW);
        this.troop = troop;
        this.playingSide = playingSide;
        this.order = order;
        this.context = context;

        setPrefSize(100, 100);

        setBackground(backgrounds.getTroop(troop, playingSide, TroopFace.AVERS));

        setOnMouseClicked(e -> onClick());
    }

    public void onClick() {
        if (order >= 0 && troop != null) {
            select();
        }
    }

    private void select() {
        setBorder(selectionBorder);
        context.stackViewSelected(this);
    }

    public void unselect() {
        setBorder(null);
    }

    public void setBlank() {
        setBackground(TileBackgrounds.EMPTY_BG);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void decrementOrder() {
        order--;
    }

    public Troop getTroop() {
        return troop;
    }

    public void setTroop(Troop troop) {
        this.troop = troop;
    }

    public PlayingSide getPlayingSide() {
        return playingSide;
    }
}


