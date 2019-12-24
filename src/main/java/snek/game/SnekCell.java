package snek.game;

public enum SnekCell {
    EMPTY,
    APPLE,
    BODY,
    HEAD;


    public char toChar() {
        switch (this) {
            case EMPTY:
                return ' ';
            case APPLE:
                return '*';
            case BODY:
                return 'B';
            case HEAD:
                return 'H';
        }
        return '?';
    }

    @Override
    public String toString() {
        return Character.toString(toChar());
    }
}
