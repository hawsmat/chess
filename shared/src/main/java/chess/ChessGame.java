package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor team = TeamColor.WHITE;
    ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> possibleMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        for (ChessMove move : possibleMoves) {
            chess.ChessBoard tempBoard = new ChessBoard();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; i < 9; i++) {
                    ChessPosition position = new ChessPosition(i, j);
                    tempBoard.addPiece(position, new ChessPiece(board.getPiece(position).getTeamColor(), board.getPiece(position).getPieceType()));
                }
            }
            tempBoard.addPiece(move.getEndPosition(), tempBoard.getPiece(move.getStartPosition()));
            tempBoard.addPiece(move.getStartPosition(), null);
            if (isInCheck(board.getPiece(startPosition).getTeamColor()) || isInCheckmate(board.getPiece(startPosition).getTeamColor())) {
                possibleMoves.remove(move);
            }
        }
        return possibleMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        for (ChessMove possibleMove : possibleMoves) {
            if (move == possibleMove) {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                board.addPiece(move.getStartPosition(), null);
                break;
            }
        }
        throw new InvalidMoveException("That's not good");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = new ChessPosition(0, 0);
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = position;
                }
            }
        }

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition enemyPosition = new ChessPosition(i, j);
                ChessPiece enemyPiece = board.getPiece(enemyPosition);
                if (enemyPiece != null && enemyPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> otherMoves = validMoves(enemyPosition);
                    for (ChessMove move: otherMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            Collection<ChessMove> kingMoves = new ArrayList<>();
            ChessPosition kingPosition = new ChessPosition(0, 0);
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition position = new ChessPosition(i, j);
                    ChessPiece piece = board.getPiece(position);
                    if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingMoves = validMoves(position);
                        kingPosition = position;
                    }
                }
            }
            if (canCapture(kingPosition, teamColor) || canMove(kingMoves, teamColor) || canBlock(kingMoves, teamColor)){
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }

    public boolean canCapture(ChessPosition kingPosition, TeamColor teamColor) {
        ChessPosition attacking = new ChessPosition(0, 0);
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece attackingPiece = board.getPiece(position);
                if (attackingPiece != null && attackingPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> otherMoves = validMoves(position);
                    for (ChessMove move: otherMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            attacking = move.getEndPosition();
                            break;
                        }
                    }
                }
            }
        }

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece helpingPiece = board.getPiece(position);
                if (helpingPiece != null && helpingPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> helpingMoves = validMoves(position);
                    for (ChessMove move: helpingMoves) {
                        if (move.getEndPosition().equals(attacking)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean canMove(Collection<ChessMove> kingMoves, TeamColor teamColor) {
        boolean canMove = true;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> otherMoves = validMoves(position);
                    for (ChessMove kingMove : kingMoves) {
                        for (ChessMove move: otherMoves) {
                            if (move.getEndPosition().equals(kingMove.getEndPosition())) {
                                canMove = false;
                            }
                        }
                    }
                }
            }
        }
        return canMove;
    }

    public boolean canBlock(Collection<ChessMove> kingMoves, TeamColor teamColor) {
        Collection<ChessMove> attackingMoves = new ArrayList<>();
        ChessPosition attackingLocation = new ChessPosition(0, 0);
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> otherMoves = validMoves(position);
                    for (ChessMove kingMove : kingMoves) {
                        for (ChessMove move: otherMoves) {
                            if (move.getEndPosition().equals(kingMove.getEndPosition())) {
                                attackingLocation = position;
                                attackingMoves = otherMoves;
                            }
                        }
                    }
                }
            }
        }
        if (board.getPiece(attackingLocation).getPieceType() == ChessPiece.PieceType.QUEEN || board.getPiece(attackingLocation).getPieceType() == ChessPiece.PieceType.ROOK || board.getPiece(attackingLocation).getPieceType() == ChessPiece.PieceType.BISHOP){
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition position = new ChessPosition(i, j);
                    ChessPiece helpingPiece = board.getPiece(position);
                    if (helpingPiece != null && helpingPiece.getTeamColor() == teamColor) {
                        Collection<ChessMove> helpingMoves = validMoves(position);
                        for (ChessMove move: helpingMoves) {
                            for (ChessMove lines : attackingMoves) {
                                if (lines.equals(move)) {
                                    return true;
                                }
                            }

                        }
                    }
                }
            }
            return false;
        }
        else {
            return false;
        }

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = new ChessPosition(0, 0);
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = position;
                }
            }
        }

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> otherMoves = validMoves(position);
                    for (ChessMove move: otherMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                this.board.addPiece(new ChessPosition(i, j), piece);
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, board);
    }
}
