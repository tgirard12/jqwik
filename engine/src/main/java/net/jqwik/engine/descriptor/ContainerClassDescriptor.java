package net.jqwik.engine.descriptor;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.engine.discovery.predicates.*;

public class ContainerClassDescriptor extends AbstractTestDescriptor {

	private final static Predicate<Class<?>> isTopLevelClass = new IsTopLevelClass();
	private final static Predicate<Class<?>> isContainerAGroup = new IsContainerAGroup();

	private final Class<?> containerClass;
	private final boolean isGroup;
	private final Set<TestTag> tags;

	public ContainerClassDescriptor(UniqueId uniqueId, Class<?> containerClass, boolean isGroup) {
		super(uniqueId, determineDisplayName(containerClass), ClassSource.from(containerClass));
		warnWhenJunitAnnotationsArePresent(containerClass);
		this.tags = determineTags(containerClass);
		this.containerClass = containerClass;
		this.isGroup = isGroup;
	}

	private void warnWhenJunitAnnotationsArePresent(Class<?> containerClass) {
		DiscoverySupport.warnWhenJunitAnnotationsArePresent(containerClass);
	}

	private Set<TestTag> determineTags(Class<?> containerClass) {
		return DiscoverySupport.findTestTags(containerClass);
	}

	private static String determineDisplayName(Class<?> containerClass) {
		return DiscoverySupport.determineLabel(containerClass, () -> getDefaultDisplayName(containerClass));
	}

	private static String getDefaultDisplayName(Class<?> containerClass) {
		if (isTopLevelClass.test(containerClass) || isContainerAGroup.test(containerClass))
			return containerClass.getSimpleName();
		return getCanonicalNameWithoutPackage(containerClass);
	}

	private static String getCanonicalNameWithoutPackage(Class<?> containerClass) {
		String packageName = containerClass.getPackage().getName();
		String canonicalName = containerClass.getCanonicalName();
		return canonicalName.substring(packageName.length() + 1);
	}

	@Override
	public Set<TestTag> getTags() {
		Set<TestTag> allTags = new LinkedHashSet<>(tags);
		getParent().ifPresent(parentDescriptor -> allTags.addAll(parentDescriptor.getTags()));
		return allTags;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	public Class<?> getContainerClass() {
		return containerClass;
	}

	public boolean isGroup() {
		return isGroup;
	}
}
