/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.core.widgets;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.WidgetCategory;
import org.sonar.api.web.WidgetProperties;
import org.sonar.api.web.WidgetProperty;
import org.sonar.api.web.WidgetPropertyType;
import org.sonar.api.web.WidgetScope;

import static org.sonar.api.web.WidgetScope.GLOBAL;

@WidgetCategory({"Filters", "Global"})
@WidgetScope(GLOBAL)
@WidgetProperties({
  @WidgetProperty(key = MeasureFilterListWidget.FILTER_PROPERTY, type = WidgetPropertyType.FILTER, optional = false),
  @WidgetProperty(key = MeasureFilterListWidget.PAGE_SIZE_PROPERTY, type = WidgetPropertyType.INTEGER, optional = true)
}
)
public class MeasureFilterListWidget extends AbstractRubyTemplate implements RubyRailsWidget {
  public static final String FILTER_PROPERTY = "filter";
  public static final String PAGE_SIZE_PROPERTY = "pageSize";
  public static final String ID = "measure_filter_list";

  public String getId() {
    return ID;
  }

  public String getTitle() {
    return "Measure Filter as List";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/core/widgets/measure_filter_list.html.erb";
  }
}