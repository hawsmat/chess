package chess;

import java.util.*;

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
            calculateDiagonal(myPosition, board, 1, 1);
            calculateDiagonal(myPosition, board, 1, -1);
            calculateDiagonal(myPosition, board, -1, 1);
            calculateDiagonal(myPosition, board, -1, -1);
            return moves;
        }
        else if (board.getPiece(myPosition).getPieceType() == PieceType.KING) {
            calculateKing(myPosition, board);
            return moves;
        }
        else if (board.getPiece(myPosition).getPieceType() == PieceType.KNIGHT) {
            calculateKnight(myPosition, board);
            return moves;
        }
        else if (board.getPiece(myPosition).getPieceType() == PieceType.PAWN) {
            calculatePawn(myPosition, board);
            return moves;
        }
        return List.of();
    }

    public void calculateDiagonal(ChessPosition myPosition, ChessBoard board, int row, int col) {
        for (int i = 1; i < 8; i++) {
            int x = myPosition.getRow() + i*row;
            int y = myPosition.getColumn() + i*col;
            ChessPosition newPosition = new ChessPosition(x, y);
            if (isValid(board, x, y, newPosition)) {
                if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                    break;
                }
                else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                }
            }
            else {
                break;
            }
        }
    }

    public void calculatePawn(ChessPosition myPosition, ChessBoard board) {
        List<List<Integer>> rowsAndCols = getPawnList(pieceColor);
        for (int i = 0; i < rowsAndCols.size(); i++){
            int x = myPosition.getRow() + rowsAndCols.get(i).get(0);
            int y = myPosition.getColumn() + rowsAndCols.get(i).get(1);
            ChessPosition newPosition = new ChessPosition(x, y);
            if (i == 0 && isValid(board, x, y, newPosition)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
            }
            else if (i == 1 && x-1 == 1) {
                moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
            }
            else if (i == 2 || i == 3) {
                if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                }
            }
        }
    }

    public List<List<Integer>> getPawnList(ChessGame.TeamColor pieceColor) {
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            return Arrays.asList(
                    Arrays.asList(1, 0),
                    Arrays.asList(2, 0),
                    Arrays.asList(1, 1),
                    Arrays.asList(1, -1)
            );
        }
        else {
            return Arrays.asList(
                    Arrays.asList(-1, 0),
                    Arrays.asList(-2, 0),
                    Arrays.asList(-1, 1),
                    Arrays.asList(-1, -1)
            );
        }
    }

    public void calculateKing(ChessPosition myPosition, ChessBoard board){
        List<List<Integer>> rowsAndCols = Arrays.asList(
                Arrays.asList(-1, -1),
                Arrays.asList(-1, 0),
                Arrays.asList(-1, 1),
                Arrays.asList(0, -1),
                Arrays.asList(0, 1),
                Arrays.asList(1, -1),
                Arrays.asList(1, 0),
                Arrays.asList(1, 1)
        );
        for (int i = 0; i < rowsAndCols.size(); i++){
            int x = myPosition.getRow() + rowsAndCols.get(i).get(0);
            int y = myPosition.getColumn() + rowsAndCols.get(i).get(1);
            ChessPosition newPosition = new ChessPosition(x, y);
            if (isValid(board, x, y, newPosition)) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
            }
        }
    }

    public void calculateKnight(ChessPosition myPosition, ChessBoard board){
        List<List<Integer>> rowsAndCols = Arrays.asList(
                Arrays.asList(-2, -1),
                Arrays.asList(-2, 1),
                Arrays.asList(2, 1),
                Arrays.asList(2, -1),
                Arrays.asList(1, 2),
                Arrays.asList(-1, 2),
                Arrays.asList(1, -2),
                Arrays.asList(-1, -2)
        );
        for (int i = 0; i < rowsAndCols.size(); i++){
            int x = myPosition.getRow() + rowsAndCols.get(i).get(0);
            int y = myPosition.getColumn() + rowsAndCols.get(i).get(1);
            ChessPosition newPosition = new ChessPosition(x, y);
            if (isValid(board, x, y, newPosition)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
            }
        }
    }

    public boolean isValid(ChessBoard board, int x, int y, ChessPosition newPosition) {
        if (x < 9 && x > 0 && y < 9 && y > 0 && (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != pieceColor)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type && Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, moves);
    }
}