package com.levigibson;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import javax.xml.stream.FactoryConfigurationError;
import java.util.Scanner;
import java.util.Vector;

public class uci {
    public uci(){

    }

    public
    void run(){

        Board board = new Board();
        boolean turn = true;

        Vector<Move> move_stack = new Vector<>();

        Scanner input = new Scanner(System.in);

        while (true) {

            String inputString = input.nextLine();

            if ("uci".equals(inputString))
            {
                System.out.println("id name eggog-chess");
                System.out.println("id author Levi Gibson");
                System.out.println("uciok");
            }
            else if (inputString.startsWith("setoption"))
            {

            }
            else if ("isready".equals(inputString))
            {
                System.out.println("readyok");
            }
            else if ("ucinewgame".equals(inputString))
            {
                board = new Board();
                turn = true;
                move_stack = new Vector<>();
                search.inOpening = false;
            }
            else if (inputString.startsWith("position"))
            {
                String[] moveStrings = (inputString.split(" "));

                board = new Board();
                move_stack = new Vector<>();
                turn = true;

                boolean movesStarted = false;

                for (String mstr : moveStrings){

                    if (mstr.length() == 4 || movesStarted){
                        movesStarted = true;
                        board.doMove(mstr);
                        //System.out.println((new Move(Square.fromValue(mstr.substring(0,2).toUpperCase()), Square.fromValue(mstr.substring(2,4).toUpperCase()))));
                        move_stack.add((new Move(Square.fromValue(mstr.substring(0,2).toUpperCase()), Square.fromValue(mstr.substring(2,4).toUpperCase()))));

                        turn = !turn;
                    }
                    //board.doMove(mstr);
                }
            }

            else if (inputString.startsWith("go"))
            {

                mmReturn aiMove = search.ai_choose_move(board, turn, move_stack);

                board.doMove(aiMove.move);
                move_stack.add(aiMove.move);

                turn = !turn;

                //System.out.print("line ");

                for (Move move : aiMove.line){
                    //System.out.print(move.toString() + " ");
                }

                //System.out.println();

                System.out.println("info score cp " + aiMove.loss + " depth " + aiMove.depth + " nodes " + aiMove.nodes);

                System.out.println("bestmove " + aiMove.move.toString());
            }
            else if ("print".equals(inputString))
            {

            }
            else {

            }
        }

    }
}
