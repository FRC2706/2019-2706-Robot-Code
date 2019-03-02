package ca.team2706.frc.robot;

public class Pair<T> {
    private final T first;
    private final T second;

    /**
     * @param first  The first item
     * @param second The second item
     */
    private Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first item
     *
     * @return Returns the first item
     */
    public T getFirst() {
        return first;
    }

    /**
     * Gets the second item
     *
     * @return Returns the second item
     */
    public T getSecond() {
        return second;
    }

    /**
     * @param first  The first item
     * @param second The second item
     * @param <T>    The type of pair
     * @return The pair
     */
    public static <T> Pair<T> of(T first, T second) {
        return new Pair<>(first, second);
    }
}
