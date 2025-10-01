package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    final private ChessGame.TeamColor pieceColor;
    final private ChessPiece.PieceType type;
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
            calculateDiagonalAndSides(myPosition, board, 1, 1);
            calculateDiagonalAndSides(myPosition, board, 1, -1);
            calculateDiagonalAndSides(myPosition, board, -1, 1);
            calculateDiagonalAndSides(myPosition, board, -1, -1);
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
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                calculatePawn(myPosition, board, 1, 2);
                return moves;
            }
            else {
                calculatePawn(myPosition, board, -1, 7);
                return moves;
            }
        }
        else if (board.getPiece(myPosition).getPieceType() == PieceType.ROOK) {
            calculateDiagonalAndSides(myPosition, board, 0, 1);
            calculateDiagonalAndSides(myPosition, board, 0, -1);
            calculateDiagonalAndSides(myPosition, board, 1, 0);
            calculateDiagonalAndSides(myPosition, board, -1, 0);
            return moves;
        }
        else if (board.getPiece(myPosition).getPieceType() == PieceType.QUEEN) {
            calculateDiagonalAndSides(myPosition, board, 1, 1);
            calculateDiagonalAndSides(myPosition, board, 1, -1);
            calculateDiagonalAndSides(myPosition, board, -1, 1);
            calculateDiagonalAndSides(myPosition, board, -1, -1);
            calculateDiagonalAndSides(myPosition, board, 0, 1);
            calculateDiagonalAndSides(myPosition, board, 0, -1);
            calculateDiagonalAndSides(myPosition, board, 1, 0);
            calculateDiagonalAndSides(myPosition, board, -1, 0);
            return moves;
        }
        return List.of();
    }

    public void calculateDiagonalAndSides(ChessPosition myPosition, ChessBoard board, int row, int col) {
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + i*row, myPosition.getColumn() + i*col);
            if (isValid(board, newPosition)) {
                if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                }
                else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
            else {
                break;
            }
        }
    }

    public void calculatePawn(ChessPosition myPosition, ChessBoard board, int direction, int startPos) {
        int x = myPosition.getRow() + direction;
        int y = myPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(x, y);
        List<PieceType> pieces = Arrays.asList(
                PieceType.ROOK,
                PieceType.QUEEN,
                PieceType.BISHOP,
                PieceType.KNIGHT
        );
        if (x + direction > 8 || x + direction < 1) {
            for (PieceType promotion: pieces) {
                pawnCapture(myPosition, new ChessPosition(x, y+1), promotion, board);
                pawnCapture(myPosition, new ChessPosition(x, y-1),promotion, board);
                pawnUp(myPosition, new ChessPosition(x, y), promotion, board);
            }
        }
        else {
            pawnCapture(myPosition, new ChessPosition(x, y+1), null, board);
            pawnCapture(myPosition, new ChessPosition(x, y-1),null, board);
            pawnUp(myPosition, newPosition, null, board);
            if (x-direction == startPos) {
                pawnUpTwo(myPosition, new ChessPosition(x+direction, y), null, board, direction);
            }
        }
    }

    public void pawnCapture(ChessPosition myPosition, ChessPosition newPosition, PieceType type, ChessBoard board){
        if (newPosition.getColumn() > 0 && newPosition.getColumn() < 9 && newPosition.getRow() > 0 && newPosition.getRow() < 9 && board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != pieceColor) {
            moves.add(new ChessMove(myPosition, newPosition, type));
        }
    }

    public void pawnUp(ChessPosition myPosition, ChessPosition newPosition, PieceType type, ChessBoard board){
        if (pawnIsValid(board, newPosition)) {
            moves.add(new ChessMove(myPosition, newPosition, type));
        }
    }

    public void pawnUpTwo(ChessPosition myPosition, ChessPosition newPosition, PieceType type, ChessBoard board, int direction){
        if (pawnIsValid(board, newPosition) && pawnIsValid(board, new ChessPosition(newPosition.getRow()-direction, newPosition.getColumn()))) {
            moves.add(new ChessMove(myPosition, newPosition, type));
        }
    }

    public boolean pawnIsValid(ChessBoard board, ChessPosition newPosition) {
        return (newPosition.getRow() < 9 && newPosition.getRow() > 0 && newPosition.getColumn() < 9 && newPosition.getColumn() > 0 && board.getPiece(newPosition) == null);
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
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowsAndCols.get(i).get(0), myPosition.getColumn() + rowsAndCols.get(i).get(1));
            if (isValid(board, newPosition)) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + rowsAndCols.get(i).get(0), myPosition.getColumn() + rowsAndCols.get(i).get(1)), null));
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
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowsAndCols.get(i).get(0), myPosition.getColumn() + rowsAndCols.get(i).get(1));
            if (isValid(board, newPosition)) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + rowsAndCols.get(i).get(0), myPosition.getColumn() + rowsAndCols.get(i).get(1)), null));
            }
        }
    }

    public boolean isValid(ChessBoard board, ChessPosition newPosition) {
        return (newPosition.getRow() < 9 && newPosition.getRow() > 0 && newPosition.getColumn() < 9 && newPosition.getColumn() > 0 && (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != pieceColor));
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