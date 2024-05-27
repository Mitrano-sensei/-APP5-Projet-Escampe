package escampe.board.types;

public enum SquareType {
    ONE{
        @Override
        public int toNumber() {
            return 1;
        }
    },
    TWO {
        @Override
        public int toNumber() {
            return 2;
        }
    },
    THREE {
        @Override
        public int toNumber() {
            return 3;
        }
    };

    public abstract int toNumber();
}
