package model;

import chess.ChessGame;

public record JoinRequest(String authToken, ChessGame.TeamColor playerColor, Integer gameID) {
}
