package escampe;

import escampe.board.EscampeBoard;
import escampe.board.types.PlayerTurn;

public class BestPlayer implements IJoueur {

    private PlayerTurn myTurn;
    private EscampeBoard board;

    @Override
    public void initJoueur(int mycolour) {
        myTurn = mycolour == -1 ? PlayerTurn.WHITE : PlayerTurn.BLACK;
        this.board = new EscampeBoard();
    }

    @Override
    public int getNumJoueur() {
        return myTurn == PlayerTurn.WHITE ? -1 : 1;
    }

    @Override
    public String choixMouvement() {
        var thisPlayer = myTurn == PlayerTurn.WHITE ? "blanc" : "noir";
        var possibleMoves = board.possiblesMoves(thisPlayer);

        int maxScore = Integer.MIN_VALUE;
        String bestMove = null;
        for (var move : possibleMoves) {
            var score = computeScore(move);
            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int computeScore(String move) {
        var newBoard = board.clone();
        newBoard.play(move, myTurn == PlayerTurn.WHITE ? "blanc" : "noir");


        return 0;
    }

    @Override
    public void declareLeVainqueur(int colour) {
        if (!board.gameOver())
            return;

        var me = myTurn == PlayerTurn.WHITE ? -1 : 1;
        System.out.println(me == colour ? "Meilleur joueur : G gagné !!! :D" : "Meilleur joueur : g perdu D:");
    }

    @Override
    public void mouvementEnnemi(String coup) {
        var otherPlayer = myTurn == PlayerTurn.BLACK ? "blanc" : "noir";
        board.play(coup, otherPlayer);
    }

    @Override
    public String binoName() {
        return "Meilleur joueur " + (myTurn == PlayerTurn.WHITE ? "blanc" : "noir");
    }
}
