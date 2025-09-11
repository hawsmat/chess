package chess;

import java.util.Collection;

public class Bishop {
    private Collection<ChessPosition> moves;
    int x;
    int y;

    public Bishop(ChessPosition position){
        x = position.getRow();
        y = position.getColumn();
    }

    public Collection getMoves() {
        upRight();
        upLeft();
        downRight();
        downLeft();
        return moves;
    }

    public void upRight(){
        int greatest = Math.max(Math.abs(x), (Math.abs(y)));
        for (int i = greatest; i < 9; i++) {
            moves.add(new ChessPosition(x+1, y-1));
        }
    }
    public void upLeft(){
        int greatest = Math.max(Math.abs(x), (Math.abs(y)));
        for (int i = greatest; i < 9; i++) {
            moves.add(new ChessPosition(x+1, y-1));
        }
    }
    public void downRight(){
        int greatest = Math.max(Math.abs(x), (Math.abs(y)));
        for (int i = greatest; i < 9; i++) {
            moves.add(new ChessPosition(x+1, y-1));
        }
    }
    public void downLeft(){
        int greatest = Math.max(Math.abs(x), (Math.abs(y)));
        for (int i = greatest; i < 9; i++) {
            moves.add(new ChessPosition(x+1, y-1));
        }
    }

}
