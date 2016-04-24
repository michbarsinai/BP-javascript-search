package bp.eventsets;

import java.util.Arrays;
import java.util.Collection;


/**
 * A wrapper class for {@link EventSetInterfaces} that allows natural style logical
 * compositions of event set interfaces. Unary operators are static methods to be statically 
 * imported, and binary operators are methods of the class. <br />
 * So, assuming that A,B,C and D are {@code EventSetInterface}s, one could write:
 * <pre><code>
 * 
 * import static bp.contrib.eventsetbooleanops.EventSetBooleanOp.*;
 * ...
 * is(A).and( not( is(B).or(C) ).xor( is(A).nand(c) ) ).contains( evt );
 * anyOf( A, B, is(C).and(not(d)) );
 * etc etc.	
 * 
 * </code></pre>
 * 
 * @author michaelbar-sinai
 */
public abstract class ComposableEventSet implements EventSet {
	
	public static ComposableEventSet theEventSet( final EventSet ifce ) {
		if ( ifce instanceof ComposableEventSet ) {
			return (ComposableEventSet) ifce;
		} else {
			return new ComposableEventSet() {
				@Override
				public boolean contains(Object o) {
					return ifce.contains(o);
				}

				@Override
				public String toString() {
					return "theEventSet(" + ifce.toString() +")";
				}};
		}
	}
	
	public static ComposableEventSet not( final EventSet ifce ) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
				return ! ifce.contains(o);
			}
			@Override
			public String toString() {
				return "not(" + ifce.toString() +")";
			}};
	}
	
    public static ComposableEventSet anyOf( final Collection<EventSet> ifces) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
                return ifces.stream().anyMatch( ifce->ifce.contains(o) );
			}
			@Override
			public String toString() {
				return "anyOf(" + Arrays.asList(ifces).toString() +")";
			}};
	}
    
	public static ComposableEventSet anyOf( final EventSet... ifces) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
				for ( EventSet ifce : ifces ) {
					if ( ifce.contains(o) ) return true;
				}
				return false;
			}
			@Override
			public String toString() {
				return "anyOf(" + Arrays.asList(ifces).toString() +")";
			}};
	}
	
	public static ComposableEventSet allOf( final EventSet... ifces) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
				for ( EventSet ifce : ifces ) {
					if ( ! ifce.contains(o) ) return false;
				}
				return true;
			}
			@Override
			public String toString() {
				return "allOf(" + Arrays.asList(ifces).toString() +")";
			}};
	}
	
	public ComposableEventSet and( final EventSet ifce ) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
				return ifce.contains(o) && ComposableEventSet.this.contains(o);
			}
			
			@Override
			public String toString() {
				return "(" + ifce.toString() +") and (" + ComposableEventSet.this.toString() +")";
			}
		
		};
	}
	
	public ComposableEventSet or( final EventSet ifce ) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
				return ifce.contains(o) || ComposableEventSet.this.contains(o);
			}
			
			@Override
			public String toString() {
				return "(" + ifce.toString() +") or (" + ComposableEventSet.this.toString() +")";
			}};
	}
	
	public ComposableEventSet xor( final EventSet ifce ) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
				return ifce.contains(o) ^ ComposableEventSet.this.contains(o);
			}
			
			@Override
			public String toString() {
				return "(" + ifce.toString() +") xor (" + ComposableEventSet.this.toString() +")";
			}};
	}
	
	public ComposableEventSet nor( final EventSet ifce ) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
				return !(ifce.contains(o) || ComposableEventSet.this.contains(o));
			}
			
			@Override
			public String toString() {
				return "(" + ifce.toString() +") nor (" + ComposableEventSet.this.toString() +")";
			}};
	}
	
	public ComposableEventSet nand( final EventSet ifce ) {
		return new ComposableEventSet() {
			@Override
			public boolean contains(Object o) {
				return !(ifce.contains(o) && ComposableEventSet.this.contains(o));
			}
			
			@Override
			public String toString() {
				return "(" + ifce.toString() +") nand (" + ComposableEventSet.this.toString() +")";
			}};
	}

}
