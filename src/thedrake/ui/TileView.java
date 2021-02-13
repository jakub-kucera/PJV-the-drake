package thedrake.ui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import thedrake.BoardPos;
import thedrake.Move;
import thedrake.Tile;

public class TileView extends BasicView {

    private final TileBackgrounds backgrounds = new TileBackgrounds();

    private final Border selectionBorder = new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))
    );

    private final ImageView moveImage;

    private Tile tile;

    private final BoardPos position;

    private final TileViewContext context;

    private Move move;

    public TileView(Tile tile, BoardPos position, TileViewContext context) {
        super(ViewType.TILEVIEW);
        this.tile = tile;
        this.position = position;
        this.context = context;

        setPrefSize(100, 100);

        update();

        setOnMouseClicked(e -> onClick());

        moveImage = new ImageView(getClass().getResource("/assets/move.png").toString());
        moveImage.setVisible(false);
        getChildren().add(moveImage);
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        update();
    }

    public void onClick() {
        if(move != null) {
            context.executeMove(move);
        }
        else if(tile.hasTroop()) {
            select();
        }
    }

    private void select() {
        setBorder(selectionBorder);
        context.tileViewSelected(this);
    }

    public void unselect() {
        setBorder(null);
    }

    private void update() {
        setBackground(backgrounds.get(tile));
    }

    public BoardPos position() {
        return position;
    }

    public void setMove(Move move) {
        this.move = move;
        moveImage.setVisible(true);
    }

    @Override
    public void clearMove() {
        this.move = null;
        moveImage.setVisible(false);
    }
}


