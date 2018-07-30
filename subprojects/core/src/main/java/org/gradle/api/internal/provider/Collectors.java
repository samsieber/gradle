/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.provider;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import org.gradle.api.provider.Provider;
import org.gradle.util.CollectionUtils;

import javax.annotation.Nullable;
import java.util.Collection;

public class Collectors {
    public interface ProvidedCollector<T> extends Collector<T> {
        boolean isProvidedBy(Provider<?> provider);
    }

    public static class EmptyCollection implements Collector<Object> {
        @Override
        public boolean present() {
            return true;
        }

        @Override
        public boolean maybeCollectInto(Collection<Object> collection) {
            return true;
        }

        @Override
        public void collectInto(Collection<Object> collection) {
        }

        @Override
        public int size() {
            return 0;
        }
    }

    public static class SingleElement<T> implements Collector<T> {
        private final T element;

        public SingleElement(T element) {
            this.element = element;
        }

        @Override
        public boolean present() {
            return true;
        }

        @Override
        public void collectInto(Collection<T> collection) {
            collection.add(element);
        }

        @Override
        public boolean maybeCollectInto(Collection<T> collection) {
            collection.add(element);
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SingleElement<?> that = (SingleElement<?>) o;
            return Objects.equal(element, that.element);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(element);
        }

        @Override
        public int size() {
            return 1;
        }
    }

    public static class ElementFromProvider<T> implements ProvidedCollector<T> {
        private final Provider<? extends T> providerOfElement;

        public ElementFromProvider(Provider<? extends T> providerOfElement) {
            this.providerOfElement = providerOfElement;
        }

        @Override
        public boolean present() {
            return providerOfElement.isPresent();
        }

        @Override
        public void collectInto(Collection<T> collection) {
            T value = providerOfElement.get();
            collection.add(value);
        }

        @Override
        public boolean maybeCollectInto(Collection<T> collection) {
            T value = providerOfElement.getOrNull();
            if (value == null) {
                return false;
            }
            collection.add(value);
            return true;
        }

        @Override
        public boolean isProvidedBy(Provider<?> provider) {
            return Objects.equal(provider, providerOfElement);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ElementFromProvider<?> that = (ElementFromProvider<?>) o;
            return Objects.equal(providerOfElement, that.providerOfElement);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(providerOfElement);
        }

        @Override
        public int size() {
            return 1;
        }
    }

    public static class ElementsFromCollectionProvider<T> implements ProvidedCollector<T> {
        private final Provider<? extends Iterable<T>> providerOfElements;

        public ElementsFromCollectionProvider(Provider<? extends Iterable<T>> providerOfElements) {
            this.providerOfElements = providerOfElements;
        }

        @Override
        public boolean present() {
            return providerOfElements.isPresent();
        }

        @Override
        public void collectInto(Collection<T> collection) {
            Iterable<T> value = providerOfElements.get();
            CollectionUtils.addAll(collection, value);
        }

        @Override
        public boolean maybeCollectInto(Collection<T> collection) {
            Iterable<T> value = providerOfElements.getOrNull();
            if (value == null) {
                return false;
            }
            CollectionUtils.addAll(collection, value);
            return true;
        }

        @Override
        public boolean isProvidedBy(Provider<?> provider) {
            return Objects.equal(provider, providerOfElements);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ElementsFromCollectionProvider<?> that = (ElementsFromCollectionProvider<?>) o;
            return Objects.equal(providerOfElements, that.providerOfElements);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(providerOfElements);
        }

        @Override
        public int size() {
            if (providerOfElements instanceof CollectionProviderInternal) {
                return ((CollectionProviderInternal)providerOfElements).size();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public static class ElementsFromCollection<T> implements Collector<T> {
        private final Iterable<? extends T> value;

        public ElementsFromCollection(Iterable<? extends T> value) {
            this.value = value;
        }

        @Override
        public boolean present() {
            return true;
        }

        @Override
        public void collectInto(Collection<T> collection) {
            CollectionUtils.addAll(collection, value);
        }

        @Override
        public boolean maybeCollectInto(Collection<T> collection) {
            CollectionUtils.addAll(collection, value);
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ElementsFromCollection<?> that = (ElementsFromCollection<?>) o;
            return Objects.equal(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public int size() {
            return Iterables.size(value);
        }
    }

    public static class ElementsFromProvider<T> implements ProvidedCollector<T> {
        private final Provider<? extends Iterable<? extends T>> provider;

        public ElementsFromProvider(Provider<? extends Iterable<? extends T>> provider) {
            this.provider = provider;
        }

        @Override
        public boolean present() {
            return provider.isPresent();
        }

        @Override
        public void collectInto(Collection<T> collection) {
            Iterable<? extends T> value = provider.get();
            CollectionUtils.addAll(collection, value);
        }

        @Override
        public boolean maybeCollectInto(Collection<T> collection) {
            Iterable<? extends T> value = provider.getOrNull();
            if (value == null) {
                return false;
            }
            CollectionUtils.addAll(collection, value);
            return true;
        }

        @Override
        public boolean isProvidedBy(Provider<?> provider) {
            return Objects.equal(provider, provider);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ElementsFromProvider<?> that = (ElementsFromProvider<?>) o;
            return Objects.equal(provider, that.provider);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(provider);
        }

        @Override
        public int size() {
            if (provider instanceof CollectionProviderInternal) {
                return ((CollectionProviderInternal)provider).size();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public static class NoValueCollector implements Collector<Object> {
        @Override
        public boolean present() {
            return false;
        }

        @Override
        public void collectInto(Collection<Object> collection) {
            throw new IllegalStateException(Providers.NULL_VALUE);
        }

        @Override
        public boolean maybeCollectInto(Collection<Object> collection) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }
    }

    public static class TypedCollector<T> implements ProvidedCollector<T> {
        private final Class<? extends T> type;
        protected final Collector<T> delegate;

        public TypedCollector(@Nullable Class<? extends T> type, Collector<T> delegate) {
            this.type = type;
            this.delegate = delegate;
        }

        @Nullable
        public Class<? extends T> getType() {
            return type;
        }

        @Override
        public boolean present() {
            return delegate.present();
        }

        @Override
        public void collectInto(Collection<T> collection) {
            delegate.collectInto(collection);
        }

        @Override
        public boolean maybeCollectInto(Collection<T> collection) {
            return delegate.maybeCollectInto(collection);
        }

        @Override
        public boolean isProvidedBy(Provider<?> provider) {
            return delegate instanceof ProvidedCollector && ((ProvidedCollector<T>)delegate).isProvidedBy(provider);
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TypedCollector<?> that = (TypedCollector<?>) o;
            return Objects.equal(type, that.type) &&
                Objects.equal(delegate, that.delegate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type, delegate);
        }
    }
}
