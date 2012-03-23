package collabode;

import java.rmi.RemoteException;

import scala.*;

/**
 * Utility for constructing Scala {@code Function}<i>n</i> objects.
 */
public abstract class F {
    public abstract static class n0<R> implements Function0<R> {
        @Override public int $tag() throws RemoteException { return 0; }
    }
    
    public static <R> Function0<R> n0pass() {
        return new F.n0<R>() {
            public R apply() { return null; }
        };
    }
    
    public abstract static class n1<T1, R> implements Function1<T1, R> {
        @Override public int $tag() { return 0; }
        @Override public <A> Function1<T1, A> andThen(Function1<R, A> arg0) { return null; }
        @Override public <A> Function1<A, R> compose(Function1<A, T1> arg0) { return null; }
    }
    
    public static <T1, R> Function1<T1, R> n1pass() {
        return new F.n1<T1, R>() {
            public R apply(T1 arg0) { return null; }
        };
    }
    
    public abstract static class n2<T1, T2, R> implements Function2<T1, T2, R> {
        @Override public int $tag() { return 0; }
        @Override public Function1<T1, Function1<T2, R>> curry() { return null; }
    }
    
    public static <T1, T2, R> Function2<T1, T2, R> n2pass() {
        return new F.n2<T1, T2, R>() {
            public R apply(T1 arg0, T2 arg1) { return null; }
        };
    }
}
