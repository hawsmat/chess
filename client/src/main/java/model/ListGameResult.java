package model;

import chess.ChessGame;

public record ListGameResult(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
