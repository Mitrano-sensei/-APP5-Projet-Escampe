package escampe;

import escampe.board.EscampeBoard;
import escampe.board.types.PlayerTurn;

import java.util.Random;

public class RandomPlayer implements IJoueur {
    private PlayerTurn myTurn;
    private EscampeBoard board;

    @Override
    public void initJoueur(int myColour) {
        myTurn = myColour == -1 ? PlayerTurn.WHITE : PlayerTurn.BLACK;
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
        var r = new Random().nextInt(possibleMoves.length);
        var move = possibleMoves[r];

        board.play(move, thisPlayer);
        return move;
    }

    @Override
    public void declareLeVainqueur(int colour) {
        board.gameOver();
        var me = myTurn == PlayerTurn.WHITE ? -1 : 1;

        System.out.println(me == colour ? "Joueur aléatoire : G gagné !!! :D" : "Joueur aléatoire : g perdu D:");
    }

    @Override
    public void mouvementEnnemi(String coup) {
        var otherPlayer = myTurn == PlayerTurn.BLACK ? "blanc" : "noir";
        board.play(coup, otherPlayer);
    }

    @Override
    public String binoName() {
        return "Joueur aléatoire " + (myTurn == PlayerTurn.WHITE ? "blanc" : "noir");
    }
}
