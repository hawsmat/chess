package model;

import chess.ChessGame;

public record JoinGameData(ChessGame.TeamColor playerColor, int gameID) {
}
