package net.jqwik.properties.arbitraries;

import net.jqwik.properties.*;

import java.util.*;

public class Arbitraries {

	public static <T> Arbitrary<T> fromGenerator(RandomGenerator<T> generator) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(long seed, int tries) {
				return generator;
			}
		};
	}

	@SafeVarargs
	public static <U> Arbitrary<U> of(U... values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	public static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(RandomGenerators.choose(enumClass));
	}

	public static <T> Arbitrary<List<T>> list(Arbitrary<T> elementArbitrary) {
		return new Arbitrary<List<T>>() {
			@Override
			public RandomGenerator<List<T>> generator(long seed, int tries) {
				int maxSize = defaultMaxFromTries(tries);
				return createListGenerator(elementArbitrary, seed, tries, maxSize);
			}
		};
	}

	private static int defaultMaxFromTries(int tries) {
		return Math.max(tries / 2 - 3, 1);
	}

	public static <T> Arbitrary<List<T>> list(Arbitrary<T> elementArbitrary, int maxSize) {
		return new Arbitrary<List<T>>() {
			@Override
			public RandomGenerator<List<T>> generator(long seed, int tries) {
				return createListGenerator(elementArbitrary, seed, tries, maxSize);
			}
		};
	}


	private static<T> RandomGenerator<List<T>> createListGenerator(Arbitrary<T> elementArbitrary, long seed, int tries, int maxSize) {
		int elementTries = Math.max(maxSize / 2, 1) * tries;
		RandomGenerator<T> elementGenerator = elementArbitrary.generator(seed, elementTries);
		RandomGenerator<List<T>> generator = RandomGenerators.list(elementGenerator, maxSize);
		return generator;
	}

	public static Arbitrary<String> string(char[] validChars, int maxLength) {
		return new Arbitrary<String>() {
			@Override
			public RandomGenerator<String> generator(long seed, int tries) {
				return RandomGenerators.string(validChars, maxLength);
			}
		};
	}

	public static Arbitrary<String> string(char[] validChars) {
		return new Arbitrary<String>() {
			@Override
			public RandomGenerator<String> generator(long seed, int tries) {
				int maxLength = defaultMaxFromTries(tries);
				return RandomGenerators.string(validChars, maxLength);
			}
		};
	}

	public static Arbitrary<String> string(char from, char to, int maxLength) {
		return new Arbitrary<String>() {
			@Override
			public RandomGenerator<String> generator(long seed, int tries) {
				return RandomGenerators.string(from, to, maxLength);
			}
		};
	}

	public static Arbitrary<String> string(char from, char to) {
		return new Arbitrary<String>() {
			@Override
			public RandomGenerator<String> generator(long seed, int tries) {
				int maxLength = defaultMaxFromTries(tries);
				return RandomGenerators.string(from, to, maxLength);
			}
		};
	}
}