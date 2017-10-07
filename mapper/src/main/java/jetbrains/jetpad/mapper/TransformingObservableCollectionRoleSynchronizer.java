/*
 * Copyright 2012-2017 JetBrains s.r.o
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
package jetbrains.jetpad.mapper;

import jetbrains.jetpad.base.Registration;
import jetbrains.jetpad.model.collections.CollectionAdapter;
import jetbrains.jetpad.model.collections.CollectionItemEvent;
import jetbrains.jetpad.model.collections.list.ObservableArrayList;
import jetbrains.jetpad.model.collections.list.ObservableList;
import jetbrains.jetpad.model.transform.Transformation;
import jetbrains.jetpad.model.transform.Transformer;

import java.util.List;

class TransformingObservableCollectionRoleSynchronizer<
    SourceT,
    MappedT,
    TargetT> extends BaseCollectionRoleSynchronizer<MappedT, TargetT> {

  private SourceT mySource;

  private Transformer<? super SourceT, ObservableList<MappedT>> mySourceTransformer;
  private List<? super TargetT> myTarget;

  private Registration myCollectionRegistration;
  private Transformation<? super SourceT, ObservableList<MappedT>> mySourceTransformation;

  TransformingObservableCollectionRoleSynchronizer(
      Mapper<?, ?> mapper,
      SourceT source,
      Transformer<? super SourceT, ObservableList<MappedT>> sourceTransformer,
      List<? super TargetT> target,
      MapperFactory<MappedT, TargetT> factory) {
    super(mapper);

    mySource = source;
    mySourceTransformer = sourceTransformer;
    myTarget = target;

    addMapperFactory(factory);
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    ObservableList<MappedT> sourceList = new ObservableArrayList<>();
    mySourceTransformation = mySourceTransformer.transform(mySource, sourceList);
    new MapperUpdater().update(sourceList);
    for (Mapper<? extends MappedT, ? extends TargetT> m : getModifiableMappers()) {
      myTarget.add(m.getTarget());
    }
    myCollectionRegistration = sourceList.addListener(new CollectionAdapter<MappedT>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends MappedT> event) {
        Mapper<? extends MappedT, ? extends TargetT> mapper = createMapper(event.getNewItem());
        getModifiableMappers().add(event.getIndex(), mapper);
        myTarget.add(event.getIndex(), mapper.getTarget());
        processMapper(mapper);
      }

      @Override
      public void onItemRemoved(CollectionItemEvent<? extends MappedT> event) {
        getModifiableMappers().remove(event.getIndex());
        myTarget.remove(event.getIndex());
      }
    });
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    myCollectionRegistration.remove();
    mySourceTransformation.dispose();
    myTarget.clear();
  }

}