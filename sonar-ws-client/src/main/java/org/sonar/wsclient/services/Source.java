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
package org.sonar.wsclient.services;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class Source extends Model {

  private SortedMap<Integer, String> lines = new TreeMap<Integer, String>();

  public Collection<String> getLines() {
    return lines.values();
  }

  public SortedMap<Integer, String> getLinesById() {
    return lines;
  }

  @CheckForNull
  public String getLine(int index) {
    return lines.get(index);
  }

  public int size() {
    return lines.size();
  }

  public Source addLine(int index, @Nullable String line) {
    lines.put(index, line);
    return this;
  }

}
