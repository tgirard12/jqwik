package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import net.jqwik.properties.newShrinking.ShrinkableTypesForTest.*;

import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("MappedShrinkable")
class NMappedShrinkableTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	private Consumer<String> reporter = mock(Consumer.class);

	@Example
	void creation() {
		NShrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		NShrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + String.valueOf(i));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3));
		assertThat(shrinkable.value()).isEqualTo("33");
	}

	@Example
	void shrinking() {
		NShrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		NShrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + String.valueOf(i));

		ShrinkingSequence<String> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("22");
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("11");
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("00");
		assertThat(sequence.next(count, reporter)).isFalse();
		assertThat(sequence.current().value()).isEqualTo("00");

		assertThat(counter.get()).isEqualTo(3);
	}


	@Example
	void reportFalsifier() {

		NShrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		NShrinkable<String> shrinkable = integerShrinkable.map(i -> String.valueOf(i) + String.valueOf(i));

		ShrinkingSequence<String> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("22");
		verify(reporter).accept("22");


		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("11");
		verify(reporter).accept("11");

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("00");
		verify(reporter).accept("00");

		assertThat(sequence.next(count, reporter)).isFalse();
		verifyNoMoreInteractions(reporter);

	}

}
