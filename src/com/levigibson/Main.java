package com.levigibson;

import com.github.bhlangonijr.chesslib.Board;

public class Main {
    public static void main(String[] args) {
        //Board board = new Board();
        //moveorder.sortMoves(board);

        uci mainuci = new uci();
        mainuci.run();


    }
}
