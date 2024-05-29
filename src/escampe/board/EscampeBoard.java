package escampe.board;

import escampe.board.types.PieceType;
import escampe.board.types.PlayerTurn;
import escampe.board.types.SquareType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EscampeBoard implements IBoard{

    private final List<Square> squares = new ArrayList<>();
    private final List<Piece> pieces = new ArrayList<>();
    private String lastMove = "";

    public EscampeBoard(){
        initSquares();
    }

    @Override
    public void setFromFile(String fileName) {
        // TODO
    }

    @Override
    public void saveToFile(String fileName) {
        // TODO
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("%  ABCDEF \n");
        for (int line = 0; line < 6; line++){
            stringBuilder.append("0");
            stringBuilder.append(line+1);
            stringBuilder.append(" ");

            for (int column = 0; column < 6; column++){
                int l = column;
                int c = line;
                var piece = pieces.stream().filter(p -> p.getPos()[0] == l && p.getPos()[1] == c).findFirst().orElse(null);

                if (piece == null) {
                    stringBuilder.append('-');
                    continue;
                }

                switch(piece.getPlayer()){
                    case BLACK:
                        if (piece.getType() == PieceType.PALADIN) stringBuilder.append('b');
                        else stringBuilder.append('B');
                        break;
                    case WHITE:
                        if (piece.getType() == PieceType.PALADIN) stringBuilder.append('w');
                        else stringBuilder.append('W');
                        break;
                    default:
                        throw new RuntimeException("Player not expected");
                }
            }

            stringBuilder.append(" ");
            stringBuilder.append("0");
            stringBuilder.append(line+1);
            stringBuilder.append(" ");
            stringBuilder.append("\n");
        }
        stringBuilder.append("%  ABCDEF \n");

        return stringBuilder.toString();
    }

    @Override
    public boolean isValidMove(String move, String player) {
        return Arrays.asList(possiblesMoves(player)).contains(move);
    }

    @Override
    public String[] possiblesMoves(String player) {
        PlayerTurn playerT = (player.equals("noir")) ? PlayerTurn.BLACK : PlayerTurn.WHITE;
        var playerPieces = pieces.stream().filter(p -> p.getPlayer() == playerT).collect(Collectors.toList());
        ArrayList<String> moves = new ArrayList<>();

        if (pieces.isEmpty() || pieces.stream().noneMatch(s -> s.getPlayer() == PlayerTurn.WHITE))
        {
            return getPossibleFirstMoves(player);
        }

        var lastLisere = getLastLisere();
        for (var piece : playerPieces){
            var pos = piece.getPos();
            boolean isValidLisere = squares.stream()
                    .filter(s -> s.getColumn() == pos[0] && s.getLine() == pos[1] )
                    .anyMatch(s -> s.getType().toNumber() == lastLisere || lastLisere == -1);
            if (!isValidLisere) continue;

            var m = possibleMoves(piece, playerT);
            var myPos = toLetterNumber(piece.getPos());
            m.forEach(mo -> moves.add(myPos + "-" + mo));
        }

        if (moves.isEmpty()){
            System.out.println("Possible moves : PASSE");
            return new String[] {"E"};
        }
        else{
            System.out.println("Possible moves : " + moves);
            return moves.toArray(new String[] {});
        }
    }

    private String[] getPossibleFirstMoves(String player) {
        String[] possibleMoveOneTwo = new String[]{ "A1/B1/C1/D1/E1/F1" };
        String[] possibleMoveFiveSix = new String[]{ "A6/B6/C6/D6/E6/F6" };

        if (Objects.equals(player, "noir")) return possibleMoveOneTwo;

        var randomPiece = pieces.stream().filter(p -> p.getPlayer() == PlayerTurn.BLACK).findFirst().orElseThrow();
        var randomPos = randomPiece.getPos();

        if (randomPos[1] == 0 || randomPos[1] == 1) return possibleMoveFiveSix;
        return possibleMoveOneTwo;
    }

    private final int[] UP = new int[] {0, -1};
    private final int[] DOWN = new int[] {0, 1};
    private final int[] LEFT = new int[] {-1, 0};
    private final int[] RIGHT = new int[] {1, 0};

    private List<String> possibleMoves(Piece piece, PlayerTurn playerT) {
        var pos = piece.getPos();
        var stepLength = squares.stream()
                .filter(s ->  s.getColumn() == pos[0] && s.getLine() == pos[1])
                .findFirst()
                .orElseThrow()
                .getType()
                .toNumber();
        stepLength -= 1;

        ArrayList<String> moves = move(stepLength, pos, UP, playerT);
        moves.addAll(move(stepLength, pos, DOWN, playerT));
        moves.addAll(move(stepLength, pos, RIGHT, playerT));
        moves.addAll(move(stepLength, pos, LEFT, playerT));

        return moves;
    }

    public ArrayList<String> move(int nbStep, int[] pos, int[] movement, PlayerTurn playerT){
        var newPos = new int[] {pos[0] + movement[0], pos[1] + movement[1]};
        var res = new ArrayList<String>();

        boolean isOut = newPos[1] >= 6 || newPos[0] >= 6 || newPos[0] < 0 || newPos[1] < 0;
        if (isOut)
            return res;

        boolean isOccupied = pieces.stream().anyMatch(p -> p.getPos()[0] == newPos[0] && p.getPos()[1] == newPos[1]);
        boolean isLastMove = nbStep == 0;
        if (isOccupied){
            // La piece est sur une case occupée
            if (!isLastMove)
                return res;

            // Ici on est au dernier mouvement
            boolean isEnemyUnicorn = pieces.stream()
                    .filter(p -> p.getPos() == newPos && p.getPlayer() != playerT)
                    .anyMatch(p -> p.getType() == PieceType.UNICORN);
            if (!isEnemyUnicorn)
                return res;

            // Ici on PEUT bouger ! On a le droit de manger la licorne :D
        }


        if (isLastMove){
            // Ici la case n'est PAS occupée ET on est au dernier mouvement, on retourne donc la coordonnée
            res.add(toLetterNumber(newPos));
            return res;
        }

        if (movement != LEFT) res.addAll(move(nbStep - 1, newPos, RIGHT, playerT));
        if (movement != UP) res.addAll(move(nbStep - 1, newPos, DOWN, playerT));
        if (movement != RIGHT) res.addAll(move(nbStep - 1, newPos, LEFT, playerT));
        if (movement != DOWN) res.addAll(move(nbStep - 1, newPos, UP, playerT));

        return res;
    }

    private int getLastLisere() {
        if (lastMove.isEmpty() || lastMove.isBlank())
        {
            return -1;
        }
        var lastSquareCoord = lastMove.split("-")[1];
        var coords = toCoord(lastSquareCoord);

        var lastSquare = squares.stream().filter(s -> s.getColumn() == coords[0] && s.getLine() == coords[1]).findFirst().orElseThrow();
        return lastSquare.getType().toNumber();
    }

    private String toLetterNumber(int[] newPos) {
        var letter = 'A' + newPos[0];
        var line = newPos[1] + '1';

        return String.valueOf((char)letter) + (char)line;
    }

    private int[] toCoord(String letterNumber){
        int letterToX = letterNumber.charAt(0) - 'A';
        int y = letterNumber.charAt(1) - '1';

        return new int[] {letterToX, y};
    }

    @Override
    public void play(String move, String player) {
        if (move.contains("/")) {
            playFirstMove(move, player);
            return;
        }

        if (!isValidMove(move, player))
            throw new RuntimeException("Move is not possible D:");

        if (move.equals("E"))
        {
            lastMove = "";
            return;
        }

        var positions = move.split("-");
        var firstPosition = positions[0];
        var fromX = firstPosition.charAt(0) - 'A';
        var fromY = firstPosition.charAt(1) - '1';

        var secondPosition = positions[1];
        var toX = secondPosition.charAt(0) - 'A';
        var toY = secondPosition.charAt(1) - '1';

        var myPiece = pieces.stream().filter(p -> p.getPos()[0] == fromX && p.getPos()[1] == fromY).findFirst().orElseThrow();
        myPiece.setPos(new int[] {toX, toY});

        lastMove = move;

        System.out.println("Move (" + player + "): " + move);
        System.out.println(this);
    }

    private void playFirstMove(String move, String player) {
        var playerT = Objects.equals(player, "noir") ? PlayerTurn.BLACK : PlayerTurn.WHITE;
        var positions = move.split("/");
        var isFirst = true;

        for (var position : positions){
            var x = position.charAt(0) - 'A';
            var y = position.charAt(1) - '1';
            pieces.add(new Piece(isFirst ? PieceType.UNICORN : PieceType.PALADIN, playerT, new int[] {x, y}));

            isFirst = false;
        }
    }

    @Override
    public boolean gameOver() {
        return pieces.stream().filter(p -> p.getType() == PieceType.UNICORN).count() == 1;
    }

    public void initSquares(){
        squares.clear();

        // Line One
        squares.add(new Square(0, 0, SquareType.ONE));
        squares.add(new Square(0, 1, SquareType.TWO));
        squares.add(new Square(0, 2, SquareType.TWO));
        squares.add(new Square(0, 3, SquareType.THREE));
        squares.add(new Square(0, 4, SquareType.ONE));
        squares.add(new Square(0, 5, SquareType.TWO));

        // Line Two
        squares.add(new Square(1, 0, SquareType.THREE));
        squares.add(new Square(1, 1, SquareType.ONE));
        squares.add(new Square(1, 2, SquareType.THREE));
        squares.add(new Square(1, 3, SquareType.ONE));
        squares.add(new Square(1, 4, SquareType.THREE));
        squares.add(new Square(1, 5, SquareType.TWO));

        // Line Three
        squares.add(new Square(2, 0, SquareType.TWO));
        squares.add(new Square(2, 1, SquareType.THREE));
        squares.add(new Square(2, 2, SquareType.ONE));
        squares.add(new Square(2, 3, SquareType.TWO));
        squares.add(new Square(2, 4, SquareType.ONE));
        squares.add(new Square(2, 5, SquareType.THREE));

        // Line Four
        squares.add(new Square(3, 0, SquareType.TWO));
        squares.add(new Square(3, 1, SquareType.ONE));
        squares.add(new Square(3, 2, SquareType.THREE));
        squares.add(new Square(3, 3, SquareType.TWO));
        squares.add(new Square(3, 4, SquareType.THREE));
        squares.add(new Square(3, 5, SquareType.ONE));

        // Line Five
        squares.add(new Square(4, 0, SquareType.ONE));
        squares.add(new Square(4, 1, SquareType.THREE));
        squares.add(new Square(4, 2, SquareType.ONE));
        squares.add(new Square(4, 3, SquareType.THREE));
        squares.add(new Square(4, 4, SquareType.ONE));
        squares.add(new Square(4, 5, SquareType.TWO));

        // Line Six
        squares.add(new Square(5, 0, SquareType.THREE));
        squares.add(new Square(5, 1, SquareType.TWO));
        squares.add(new Square(5, 2, SquareType.TWO));
        squares.add(new Square(5, 3, SquareType.ONE));
        squares.add(new Square(5, 4, SquareType.THREE));
        squares.add(new Square(5, 5, SquareType.TWO));

    }

}

