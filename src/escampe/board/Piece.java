package escampe.board;

import escampe.board.types.PieceType;
import escampe.board.types.PlayerTurn;

public class Piece {

    private final PieceType type;
    private final PlayerTurn player;
    private int[] pos;

    public Piece(PieceType type, PlayerTurn player, int[] initialPosition){
        this.type = type;
        this.player = player;
        this.pos = initialPosition;
    }


    public PieceType getType() {
        return type;
    }

    /**
     *
     * @return Letter Number
     */
    public int[] getPos() {
        return pos;
    }

    public void setPos(int[] pos) {
        this.pos = pos;
    }

    public PlayerTurn getPlayer() {
        return player;
    }
}
