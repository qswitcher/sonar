/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.duplications.detector.suffixtree;

import java.util.*;

import org.sonar.duplications.block.Block;
import org.sonar.duplications.block.ByteArray;
import org.sonar.duplications.index.CloneGroup;
import org.sonar.duplications.index.CloneIndex;

import com.google.common.collect.*;

public final class SuffixTreeCloneDetectionAlgorithm {

  public static List<CloneGroup> detect(CloneIndex cloneIndex, Collection<Block> fileBlocks) {
    if (fileBlocks.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    TextSet text = createTextSet(cloneIndex, fileBlocks);
    if (text == null) {
      return Collections.EMPTY_LIST;
    }
    DuplicationsCollector reporter = new DuplicationsCollector(text);
    Search.perform(text, reporter);
    return reporter.getResult();
  }

  private SuffixTreeCloneDetectionAlgorithm() {
  }

  private static TextSet createTextSet(CloneIndex index, Collection<Block> fileBlocks) {
    Set<ByteArray> hashes = Sets.newHashSet();
    for (Block fileBlock : fileBlocks) {
      hashes.add(fileBlock.getBlockHash());
    }

    String originResourceId = fileBlocks.iterator().next().getResourceId();
    Map<String, List<Block>> fromIndex = retrieveFromIndex(index, originResourceId, hashes);

    if (fromIndex.isEmpty() && hashes.size() == fileBlocks.size()) {
      // optimization for the case when there is no duplications
      return null;
    }

    return createTextSet(fileBlocks, fromIndex);
  }

  private static TextSet createTextSet(Collection<Block> fileBlocks, Map<String, List<Block>> fromIndex) {
    TextSet.Builder textSetBuilder = TextSet.builder();
    // TODO Godin: maybe we can reduce size of tree and so memory consumption by removing non-repeatable blocks
    List<Block> sortedFileBlocks = Lists.newArrayList(fileBlocks);
    Collections.sort(sortedFileBlocks, BLOCK_COMPARATOR);
    textSetBuilder.add(sortedFileBlocks);

    for (List<Block> list : fromIndex.values()) {
      Collections.sort(list, BLOCK_COMPARATOR);

      int i = 0;
      while (i < list.size()) {
        int j = i + 1;
        while ((j < list.size()) && (list.get(j).getIndexInFile() == list.get(j - 1).getIndexInFile() + 1)) {
          j++;
        }
        textSetBuilder.add(list.subList(i, j));
        i = j;
      }
    }

    return textSetBuilder.build();
  }

  private static Map<String, List<Block>> retrieveFromIndex(CloneIndex index, String originResourceId, Set<ByteArray> hashes) {
    Map<String, List<Block>> collection = Maps.newHashMap();
    for (ByteArray hash : hashes) {
      Collection<Block> blocks = index.getBySequenceHash(hash);
      for (Block blockFromIndex : blocks) {
        // Godin: skip blocks for this file if they come from index
        String resourceId = blockFromIndex.getResourceId();
        if (!originResourceId.equals(resourceId)) {
          List<Block> list = collection.get(resourceId);
          if (list == null) {
            list = Lists.newArrayList();
            collection.put(resourceId, list);
          }
          list.add(blockFromIndex);
        }
      }
    }
    return collection;
  }

  private static final Comparator<Block> BLOCK_COMPARATOR = new Comparator<Block>() {
    public int compare(Block o1, Block o2) {
      return o1.getIndexInFile() - o2.getIndexInFile();
    }
  };

}
