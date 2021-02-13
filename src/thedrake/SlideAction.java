package thedrake;

import java.util.ArrayList;
import java.util.List;

public class SlideAction extends TroopAction {
    protected SlideAction(int offsetX, int offsetY) {
        super(offsetX, offsetY);
    }

    public SlideAction(Offset2D offset) {
        super(offset);
    }

    @Override
    public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
        List<Move> result = new ArrayList<>();
        TilePos target = origin.stepByPlayingSide(offset(), side);

        boolean canStep = true;

        while (target != TilePos.OFF_BOARD) {
            if(state.canCapture(origin, target)) {
                result.add(new StepAndCapture(origin, (BoardPos) target));
                canStep = false;
            }
            target = target.stepByPlayingSide(offset(), side);
        }

        target = origin.stepByPlayingSide(offset(), side);
        while (state.canStep(origin, target) && canStep) {
            result.add(new StepOnly(origin, (BoardPos) target));
            target = target.stepByPlayingSide(offset(), side);
        }
//
//        boolean captured = false;
//        while (target != TilePos.OFF_BOARD) {
//            if(state.canCapture(origin, target)) {
//                System.out.println("C " + target.toString());
//                result.add(new StepAndCapture(origin, (BoardPos) target));
//                captured = true;
//            }
//            else if(state.canStep(origin, target) && !captured) {
//                System.out.println("S " + target.toString());
//                result.add(new StepOnly(origin, (BoardPos) target));
//            }
//            target = target.step(offset());
//        }
//
//        if(state.canCapture(origin, target)) {
//            result.add(new StepAndCapture(origin, (BoardPos) target));
//        }

        return result;
    }
}
