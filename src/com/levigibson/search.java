package com.levigibson;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;

import java.io.File;
import java.io.*;
import java.sql.Array;
import java.time.Instant;
import java.util.*;

class mmReturn{
    Move move;
    int loss;
    int depth = 0;
    boolean timeOut = false;
    int nodes = 1;
    Vector<Move> line = new Vector<>();

    Tree tree = new Tree();

    public mmReturn(Move m, int e){
        move = m;
        loss = e;
    }
}

class Tree{

    Vector<Tree> branches = new Vector<>();
    Move move;
    int loss;



    public Tree(){

    }

    public Vector<Move> legalMovesFromTree(){
        Vector<Move> legalMoves = new Vector<>();

        for (Tree t : branches){
            legalMoves.add(t.move);
        }
        return legalMoves;
    }

    public boolean hasMoves(){
        return branches.size() != 0;
    }
}

public class search {

    static boolean inOpening = false;

    static mmReturn minimax(int depth, Board f_board, boolean maximisingPlayer, int a, int b, long startingTime, int timeLimit, int trueDepth, Tree tree){
        if (depth == 0){

            mmReturn ret = new mmReturn(null, evaluate.eval(f_board));

            ret.tree.loss = ret.loss;

            return ret;
        }


        Vector<Move> legalMoves = null;
        if (tree.hasMoves()){
            legalMoves = tree.legalMovesFromTree();
        }
        else {
            //legalMoves = (Vector<Move>) f_board.legalMoves();
            legalMoves = moveorder.sortMoves(f_board);
        }
        mmReturn eval = null;
        mmReturn maxEval = null;

        if (trueDepth < 5){
            if (Instant.now().getEpochSecond() - startingTime >= timeLimit){
                mmReturn ret = new mmReturn(null, 0);
                ret.timeOut = true;
                return ret;
            }
        }

        if (maximisingPlayer) {
            maxEval = new mmReturn(null, -100000);
            int childrenLooped = 0;
            for (Move move : legalMoves) {
                f_board.doMove(move);

                Tree passingTree = null;
                if (tree.hasMoves()){
                    passingTree = tree.branches.get(childrenLooped);
                }
                else {
                    passingTree = tree;
                }

                eval = minimax(depth - 1, f_board, false, a, b, startingTime, timeLimit, trueDepth+1, passingTree);
                maxEval.nodes += eval.nodes;

                eval.tree.move = move;

                //adding the node to the iterative deepening tree
                boolean foundPlace = false;
                int treeId = 0;
                for (Tree stree : maxEval.tree.branches){
                    if (stree.loss <= eval.loss){
                        maxEval.tree.branches.insertElementAt(eval.tree, treeId);
                        foundPlace = true;
                        break;
                    }
                    treeId++;
                }
                if (!foundPlace){
                    maxEval.tree.branches.add(eval.tree);
                }

                if (eval.loss > maxEval.loss){
                    maxEval.line = eval.line;
                    maxEval.timeOut = eval.timeOut;
                    maxEval.loss = eval.loss;
                    maxEval.move = move;
                    maxEval.line.add(move);
                    maxEval.tree.loss = maxEval.loss;
                }
                f_board.undoMove();

                //alpha beta pruning
                a = Math.max(a, eval.loss);
                if (a >= b){
                    for (Move m : legalMoves.subList(childrenLooped+1, legalMoves.size())){
                        Tree t = new Tree();
                        t.move = m;
                        t.loss = 0;
                        maxEval.tree.branches.add(t);
                    }

                    break;
                }
                childrenLooped++;

            }

            return maxEval;
        }
        else {
            maxEval = new mmReturn(null, 100000);
            int childrenLooped = 0;
            for (Move move : legalMoves) {
                f_board.doMove(move);

                Tree passingTree = null;
                if (tree.hasMoves()){
                    passingTree = tree.branches.get(childrenLooped);
                }
                else {
                    passingTree = tree;
                }

                eval = minimax(depth - 1, f_board, true, a, b, startingTime, timeLimit+1, trueDepth+1, passingTree);
                maxEval.nodes += eval.nodes;

                eval.tree.move = move;

                //adding the node to the iterative deepening tree
                boolean foundPlace = false;
                int treeId = 0;
                for (Tree stree : maxEval.tree.branches){
                    if (stree.loss >= eval.loss){
                        maxEval.tree.branches.insertElementAt(eval.tree, treeId);
                        foundPlace = true;
                        break;
                    }
                    treeId++;
                }
                if (!foundPlace){
                    maxEval.tree.branches.add(eval.tree);
                }

                if (eval.loss < maxEval.loss){
                    maxEval.line = eval.line;
                    maxEval.timeOut = eval.timeOut;
                    maxEval.loss = eval.loss;
                    maxEval.move = move;
                    maxEval.line.add(move);
                    maxEval.tree.loss = maxEval.loss;
                }

                f_board.undoMove();

                b = Math.min(b, eval.loss);

                if (b <= a){
                    for (Move m : legalMoves.subList(childrenLooped+1, legalMoves.size())){
                        Tree t = new Tree();
                        t.move = m;
                        t.loss = 0;
                        maxEval.tree.branches.add(t);
                    }
                    System.out.println(childrenLooped/legalMoves.size());
                    break;
                }
                childrenLooped++;

            }
            return maxEval;
        }
    }

    static mmReturn opening_choose_move(Board f_board, Vector<Move> move_stack){

        mmReturn aiRes = new mmReturn(null, 0);

        File file = new File("/home/levigibson/Pictures/python/java/chessTest2/src/com/levigibson/openings/book.csv");

        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Vector<String[]> book = new Vector<>();
        //Vector<String[]> matchingGames = new Vector<>();

        while (sc.hasNext()){
            book.add(sc.nextLine().split(","));
        }

        int id = 0;
        for (Move move : move_stack) {

            for (Iterator<String[]> iterator = book.iterator(); iterator.hasNext();) {
                String[] game = iterator.next();
                if (game.length <= move_stack.size()){
                    iterator.remove();
                }
                else {
                    String bookMove = (game[id]);
                    String gameMove = (move.toString());
                    if (!gameMove.equals(bookMove)) {
                        iterator.remove();
                    }
                }
            }

            id++;
        }

        int min = 0;
        int max = book.size()-1;
        int randint = (int)(Math.random()*(max-min+1)+min);

        if (book.size() > 30){
            String moveStr = (book.get(randint)[move_stack.size()]);
            aiRes.move = (new Move(Square.fromValue(moveStr.substring(0,2).toUpperCase()), Square.fromValue(moveStr.substring(2,4).toUpperCase())));

        }

        return aiRes;
    }

    static mmReturn ai_choose_move(Board f_board, boolean f_turn, Vector<Move> move_stack){
        mmReturn aiMove = null;

        long startingTime = Instant.now().getEpochSecond();


        if (!inOpening) {
            mmReturn aiRes = new mmReturn(null, 0);
            int depthSearching = 2;
            while (true) {
                aiRes = minimax(depthSearching, f_board, f_turn, -100000, 100000, startingTime, 3, 0, aiRes.tree);
                aiRes.depth = depthSearching;
                if (!aiRes.timeOut){
                    aiMove = aiRes;
                }
                else {
                    break;
                }
                depthSearching++;
            }
        }
        else {
            aiMove = opening_choose_move(f_board, move_stack);
            if (aiMove.move == null){
                inOpening = false;
                aiMove = ai_choose_move(f_board, f_turn, move_stack);
            }

        }



        return aiMove;
    }



}
