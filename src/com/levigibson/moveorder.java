package com.levigibson;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.List;
import java.util.Vector;

public class moveorder {

    private static class smove{
        Move move;
        int score;
        public smove(Move m, int s){
            move = m;
            score = s;
        }
    }

    public static Vector<Move> sortMoves(Board board){

        int[] values = {100,300,325,500,900,0,-100,-300,-325,-500,-900,0,0};

        Vector<smove> sorted_s_moves = new Vector<>();

        List<Move> unsortedMoves = board.legalMoves();
        for (Move move : unsortedMoves){
            board.doMove(move);
            smove a_move = new smove(move, evaluate.eval(board) * (board.getSideToMove().ordinal()*2-1));

            boolean foundPlace = false;
            for (int i = 0; i < sorted_s_moves.size(); i++){

                if (sorted_s_moves.get(i).score < a_move.score){
                    sorted_s_moves.insertElementAt(a_move, i);

                    foundPlace = true;
                    break;
                }
            }
            if (!foundPlace){
                sorted_s_moves.add(a_move);
            }

            board.undoMove();
        }

        Vector<Move> sortedMoves = new Vector<>();
        for (smove sm : sorted_s_moves){
            //System.out.println(sm.score);
            sortedMoves.add(sm.move);
        }
        return sortedMoves;

        /*int[] values = {100,300,325,500,900,0,-100,-300,-325,-500,-900,0,0};

        List<Move> unsortedMoves = board.legalMoves();
        Vector<smove> sorted_s_moves = new Vector<>();

        for (Move move : unsortedMoves){

            System.out.println(move);

            smove a_move = new smove(move, 0);

            int pieceType = board.getPiece(move.getFrom()).getPieceType().ordinal();
            PieceType capturedPiece = board.getPiece(move.getTo()).getPieceType();

            if (values[pieceType] < values[capturedPiece.ordinal()]) {
                a_move.score += 50;
            }


            for (int i = 0; i < sorted_s_moves.size(); i++){
                if (sorted_s_moves.get(i).score < a_move.score){
                    sorted_s_moves.insertElementAt(a_move, i);
                    break;
                }
            }
        }

        Vector<Move> sortedMoves = new Vector<>();
        for (smove sm : sorted_s_moves){
            sortedMoves.add(sm.move);
        }
        return sortedMoves;*/

    }
}
