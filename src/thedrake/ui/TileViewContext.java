package thedrake.ui;

import thedrake.Move;

public interface TileViewContext {
    void tileViewSelected(TileView tileView);

    void stackViewSelected(StackView stackView);

    void executeMove(Move move);
}
