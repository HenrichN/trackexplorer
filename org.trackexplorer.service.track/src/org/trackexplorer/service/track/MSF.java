package org.trackexplorer.service.track;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Missing Stream Functions.
 *
 */
public class MSF {
	
	/**
	 * Takes each element sequentially from {@code left} and {@code right} and applies the
	 * function {@code composer} to these two elements.
	 * 
	 * The function returns as soon as {@code left} or {@code right} run out of elements.
	 */
	public static <T,U,R> List<R> Join(List<T> left, List<U> right, BiFunction<T, U, R> composer) {
		
		LinkedList<R> result = new LinkedList<>();
		Iterator<T> iterLeft = left.iterator();
		Iterator<U> iterRight = right.iterator();
				
		while(iterLeft.hasNext() && iterRight.hasNext()) {
			result.add(composer.apply(iterLeft.next(), iterRight.next()));
		}
		
		return result;
	}

}
