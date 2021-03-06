package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public interface ForAllParametersGenerator extends Iterator<List<Shrinkable<Object>>> {

	default ForAllParametersGenerator andThen(Supplier<ForAllParametersGenerator> generatorCreator) {
		ForAllParametersGenerator first = this;
		ForAllParametersGenerator afterSuccessGenerator = generatorCreator.get();
		return new ForAllParametersGenerator() {
			@Override
			public boolean hasNext() {
				if (first.hasNext()) {
					return true;
				}
				return afterSuccessGenerator.hasNext();
			}

			@Override
			public List<Shrinkable<Object>> next() {
				if (first.hasNext()) {
					return first.next();
				}
				return afterSuccessGenerator.next();
			}
		};
	}
}
