package ca.team2706.frc.robot;

public class Pair<T> {
    private final T first;
    private final T second;

    private Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public static <T> Pair<T> of(T first, T second) {
        return new Pair<>(first, second);
    }
}
