package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class OneOfArbitrary<T> implements Arbitrary<T>, SelfConfiguringArbitrary<T> {
	private final List<Arbitrary<T>> all = new ArrayList<>();

	public OneOfArbitrary(List<Arbitrary<T>> all) {this.all.addAll(all);}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return RandomGenerators.choose(all).flatMap(Function.identity(), genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.choose(all, maxNumberOfSamples)
								   .flatMap(generator -> ExhaustiveGenerators
									   .flatMap(generator, Function.identity(), maxNumberOfSamples));
	}

	@Override
	public Arbitrary<T> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		all.replaceAll(a -> {
			if (a instanceof SelfConfiguringArbitrary) {
				// TODO: This condition exists 3 times
				//noinspection unchecked
				return ((SelfConfiguringArbitrary) a).configure(configurator, targetType);
			} else {
				return configurator.configure(a, targetType);
			}
		});
		return this;
	}

	protected List<Arbitrary<T>> arbitraries() {
		return all;
	}

	protected void addArbitrary(Arbitrary<T> arbitrary) {
		all.add(arbitrary);
	}
}
