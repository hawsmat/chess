package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        List<ChessPiece.PieceType> pieces = Arrays.asList(
            ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.KING,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.ROOK
        );
        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPosition(2, i+1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(1, i+1), new ChessPiece(ChessGame.TeamColor.WHITE, pieces.get(i)));
            addPiece(new ChessPosition(7, i+1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(8, i+1), new ChessPiece(ChessGame.TeamColor.BLACK, pieces.get(i)));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String str = "\n";
        for (int i = 8; i > 1; i--) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                if (getPiece(position) == null) {
                    str += " |";
                }
                else {
                    ChessPiece piece = new ChessPiece(getPiece(position).getTeamColor(), getPiece(position).getPieceType());
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                            str += "R|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                            str += "N|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                            str += "B|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                            str += "Q|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                            str += "K|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                            str += "P|";
                        }
                    }
                    else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                            str += "r|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                            str += "n|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                            str += "b|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                            str += "q|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                            str += "k|";
                        }
                        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                            str += "p|";
                        }
                    }
                }
            }
            str += "\n";
        }
        return str;
    }
}
