package com.bakdata.conquery.models.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;


class RangeTest {

	@Test
	public void exactly() {
		Range<Integer> exactly = Range.exactly(5);

		assertThat((IntPredicate) exactly::contains)
				.accepts(5)
				.rejects(6, 7, 4);

		assertThat((Predicate<Range<Integer>>) exactly::contains)
				.accepts(Range.exactly(5), exactly)
				.rejects(Range.exactly(6), Range.exactly(7), Range.exactly(4));
	}

	@Test
	public void atMost() {
		Range<Integer> range = Range.atMost(5);

		assertThat((IntPredicate) range::contains)
				.accepts(5, 4, Integer.MIN_VALUE)
				.rejects(6, Integer.MAX_VALUE);

		assertThat((Predicate<Range<Integer>>) range::contains)
				.accepts(Range.exactly(5), Range.atMost(4))
				.rejects(Range.exactly(6), Range.atLeast(5), Range.atLeast(4));
	}

	@Test
	public void atLeast() {
		Range<Integer> range = Range.atLeast(5);

		assertThat((IntPredicate) range::contains)
				.accepts(5, 6, Integer.MAX_VALUE)
				.rejects(4, Integer.MIN_VALUE)
		;

		assertThat((Predicate<Range<Integer>>) range::contains)
				.rejects(Range.atLeast(4), Range.atMost(4), Range.exactly(4))
				.accepts(Range.exactly(5), Range.exactly(6), Range.atLeast(5), Range.atLeast(6));
	}

	@Test
	public void all() {
		Range<Integer> range = Range.all();

		assertThat((IntPredicate) range::contains)
				.accepts(5, Integer.MAX_VALUE, Integer.MIN_VALUE);

		assertThat((Predicate<Range<Integer>>) range::contains)
				.accepts(Range.exactly(5), Range.of(5, 10))
				.rejects(Range.atMost(5), Range.atLeast(6));
	}

	@Test
	public void contains() {
		Range<Integer> range = Range.of(5, 10);

		assertThat((IntPredicate) range::contains)
				.accepts(5, 6, 7, 8, 9, 10)
				.rejects(Integer.MIN_VALUE, 4, 11, Integer.MAX_VALUE);

		assertThat((Predicate<Range<Integer>>) range::contains)
				.accepts(range, Range.of(5, 10), Range.exactly(7), Range.exactly(5), Range.exactly(10), Range.of(6, 9), Range.of(5, 9))
				.rejects(Range.atMost(5), Range.atMost(10), Range.all(), Range.exactly(4), Range.exactly(11), Range.atLeast(5), Range.atLeast(10), Range.of(7, 11), Range.of(11, 12), Range.of(3, 4));
	}

	@Test
	public void invalid() {
		assertThrows(IllegalArgumentException.class, () -> Range.of(5, 4));
	}

	@Test
	public void span() {
		assertThat(Range.exactly(5).span(Range.exactly(6)))
				.isEqualTo(Range.of(5, 6))
				.isEqualTo(Range.exactly(6).span(Range.exactly(5)));

		assertThat(Range.of(5, 7).span(Range.exactly(6)))
				.isEqualTo(Range.of(5, 7));
	}
}