import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.util.List;

public class PrintBoard {
    ChessGame game;
    ChessPosition position;

    PrintBoard(ChessGame game, ChessGame.TeamColor color, ChessPosition position) {
        this.game = game;
        int initial;
        int direction;
        int end;
        this.position=position;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            initial = 8;
            direction = -1;
            end = 0;
        }

        else {
            initial = 1;
            direction = 1;
            end = 9;
        }

        List<String> cols = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        List<String> rows = List.of("8", "7", "6", "5", "4", "3", "2", "1");
        printCols(cols, initial, direction, end);
        System.out.println();
        for (int i = initial; i != end; i += direction) {
            System.out.print(rows.get(7-(i-1)) + " ");
            for (int j = initial; j != end; j += direction) {
                if (game.getBoard().getPiece(new ChessPosition(i, 9-j)) == null) {
                    getBlankSpace(i, 9-j);
                } else {
                    printPiece(i, j, game.getBoard().getPiece(new ChessPosition(i, 9-j)));
                }
            }
            System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_BG_COLOR + " " +rows.get(i-1));
        }
        printCols(cols, initial, direction, end);
        System.out.println();
    }

    public void printPiece(int i, int j, ChessPiece chessPiece) {
        String piece;
        String color;
        boolean isValid = false;
        if (position != null) {
            for (ChessMove move : game.validMoves(position)) {
                if (new ChessPosition(i, j).equals(move.getEndPosition())){
                    isValid = true;
                    break;
                }
            }
        }
        if (chessPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            color = (isValid) ?  EscapeSequences.SET_TEXT_COLOR_MAGENTA : EscapeSequences.SET_TEXT_COLOR_RED;
            piece = switch (chessPiece.getPieceType()) {
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case KING -> EscapeSequences.BLACK_KING;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case PAWN -> EscapeSequences.BLACK_PAWN;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
            };
        } else {
            color = (isValid) ? EscapeSequences.SET_TEXT_COLOR_MAGENTA : EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
            piece = switch (chessPiece.getPieceType()) {
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case KING -> EscapeSequences.WHITE_KING;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case PAWN -> EscapeSequences.WHITE_PAWN;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
            };
        }
        if ((i + j) % 2 == 0) {
            System.out.print(EscapeSequences.SET_BG_COLOR_WHITE + color + piece);
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + color + piece);
        }
    }

    public void printCols(List<String> cols, int initial, int direction, int end) {
        System.out.print("  ");
        for (int i = initial; i != end; i+=direction) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + cols.get(7-(i-1)) + " ");
        }
    }

    public void getBlankSpace(int i, int j) {
        String positionColor = "";
        if (position != null) {
            for (ChessMove move : game.validMoves(position)) {
                if (new ChessPosition(i, j).equals(move.getEndPosition())) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_MAGENTA + "   ");
                    return;
                }
            }
            if ((i + j+1) % 2 == 0) {
                positionColor = EscapeSequences.SET_BG_COLOR_WHITE;
            } else {
                positionColor = EscapeSequences.SET_BG_COLOR_BLACK;
            }
        }
        else {
            if ((i + j+1) % 2 == 0) {
                positionColor = EscapeSequences.SET_BG_COLOR_WHITE;
            } else {
                positionColor = EscapeSequences.SET_BG_COLOR_BLACK;
            }
        }
            System.out.print(positionColor + "   " + EscapeSequences.RESET_BG_COLOR);
    }
}
