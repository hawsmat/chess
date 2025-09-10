package chess;

import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;
    private Collection<ChessMove> moves;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getPieceType() == PieceType.BISHOP) {
            return List.of();
        }
        return List.of();
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
