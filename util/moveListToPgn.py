import chess
import chess.pgn

moves = " a2a3, a7a5, b2b3, a5a4, b3a4, a8a4, c2c3, b7b5, d2d3, c7c6, e2e3, c6c5, f2f3, d7d5, g2g3, c5c4, d3c4, d5c4, h2h3, e7e5, e3e4, d8d1, e1d1, f8c5, g3g4, f7f6, h3h4, h7h5, g4h5, h8h5, a1a2, f6f5, b1d2, f5e4, d2e4, b8a6, f3f4, e5f4, e4c5, a6c5, c1b2, g7g5, f1e2, g5g4, e2f1, g4g3, f1e2, h5d5, d1c1, c5b3, c1c2, c8f5"
moves = moves.split(",")

for i in range(len(moves)):
    moves[i] = chess.Move.from_uci(moves[i][1:5])

game = chess.pgn.Game()

game.add_line(moves)
print(game)
