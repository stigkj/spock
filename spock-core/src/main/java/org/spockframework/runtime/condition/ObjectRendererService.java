/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.runtime.condition;

import java.util.*;

import org.spockframework.util.InternalSpockError;

public class ObjectRendererService implements IObjectRendererService {
  private final HashMap<Class<?>, IObjectRenderer<?>> renderers = new HashMap<Class<?>, IObjectRenderer<?>>();

  public <T> void addRenderer(Class<T> type, IObjectRenderer<? super T> renderer) {
    renderers.put(type, renderer);
  }

  @SuppressWarnings("unchecked")
  public String render(Object object) {
    // cast required although IDEA thinks it's unnecessary
    Set<Class<?>> types = Collections.<Class<?>>singleton(object.getClass());

    while (!types.isEmpty()) {
      for (Class<?> type : types) {
        IObjectRenderer renderer = renderers.get(type);
        if (renderer != null) return renderer.render(object);
      }
      types = getParents(types);
    }

    IObjectRenderer renderer = renderers.get(Object.class);
    if (renderer != null) return renderer.render(object);

    throw new InternalSpockError("should always have renderer for class Object");
  }

  private Set<Class<?>> getParents(Set<Class<?>> types) {
    Set<Class<?>> parents = new HashSet<Class<?>>();

    for (Class<?> type : types) {
      Class<?> superclass = type.getSuperclass();
      if (superclass != null && superclass != Object.class) {
        parents.add(superclass);
      }
      // cast required to compile with JDK 5
      parents.addAll(Arrays.asList((Class<?>[])type.getInterfaces()));
    }

    return parents;
  }
}
