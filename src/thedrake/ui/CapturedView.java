package thedrake.ui;

import thedrake.PlayingSide;
import thedrake.Troop;
import thedrake.TroopFace;

public class CapturedView extends BasicView {

    private final TileBackgrounds backgrounds = new TileBackgrounds();

    private Troop troop;

    private int order;

    private final PlayingSide playingSide;

    public CapturedView(int order, PlayingSide playingSide) {
        super(ViewType.CAPTUREDVIEW);
        this.order = order;
        this.playingSide = playingSide;

        setPrefSize(100, 100);

        setBackground(TileBackgrounds.EMPTY_BG);
    }

    public void setTroop(Troop troop) {
        this.troop = troop;
        setBackground(backgrounds.getTroop(troop, playingSide, TroopFace.AVERS));
    }

    public void decrementOrder() {
        order--;
    }

    public int getOrder() {
        return order;
    }

    public PlayingSide getPlayingSide() {
        return playingSide;
    }
}


