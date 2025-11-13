package model;

import chess.ChessGame;

public record JoinGameData(String authToken, ChessGame.TeamColor playerColor, int gameID) {
}
