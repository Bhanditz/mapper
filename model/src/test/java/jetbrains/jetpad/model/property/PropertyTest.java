/*
 * Copyright 2012-2014 JetBrains s.r.o
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.jetpad.model.property;

import com.google.common.base.Supplier;
import jetbrains.jetpad.model.event.EventHandler;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PropertyTest {
  @Test
  public void incorrectEventFiring() {
    final Property<String> prop = new ValueProperty<String>(null);
    ReadableProperty<Integer> derived = new SimpleDerivedProperty<Integer>(new Supplier<Integer>() {
      @Override
      public Integer get() {
        String value = prop.get();
        return value == null ? 0 : value.length();
      }
    }, prop);

    prop.set("xyz");

    EventHandler<PropertyChangeEvent<Integer>> handler = mock(EventHandler.class);

    derived.addHandler(handler);

    prop.set("");

    verify(handler).onEvent(new PropertyChangeEvent<Integer>(3, 0));
  }
}