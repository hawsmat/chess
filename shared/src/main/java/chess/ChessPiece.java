package chess;

import java.util.ArrayList;
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
    private List<ChessMove> moves = new ArrayList<>();


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
            upRight(myPosition, board);
            upLeft(myPosition, board);
            downLeft(myPosition, board);
            downRight(myPosition, board);
            return moves;
        }
        return List.of();
    }

    public void upRight(ChessPosition myPosition, ChessBoard board) {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        for (int i = 0; i < 8; i++) {
            if (x + i < 9 & y + i < 9) {
                if (board.getPiece(new ChessPosition(x+i, y+i)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(x + i, y + i), null));
                }
                break;
            }
        }
    }

    public void upLeft(ChessPosition myPosition, ChessBoard board) {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        for (int i = 0; i < 8; i++) {
            if (x - i > 0 & y + i < 9) {
                if (board.getPiece(new ChessPosition(x-i, y+i)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(x - i, y + i), null));
                }
                break;
            }
        }
    }

    public void downRight(ChessPosition myPosition, ChessBoard board) {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        for (int i = 0; i < 8; i++) {
            if (x + i < 9 & y - i > 0) {
                if (board.getPiece(new ChessPosition(x+i, y-i)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(x + i, y - i), null));
                }
                break;
            }
        }
    }

    public void downLeft(ChessPosition myPosition, ChessBoard board) {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        for (int i = 0; i < 8; i++) {
            if (x - i > 0 & y - i > 0) {
                if (board.getPiece(new ChessPosition(x-i, y-i)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(x - i, y - i), null));
                }
                break;
            }
        }
    }
}