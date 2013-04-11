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
package org.sonar.core.issue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.sonar.api.issue.Issue;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class DefaultIssue implements Issue {

  private static final Set<String> SEVERITIES = ImmutableSet.of(SEVERITY_BLOCKER, SEVERITY_CRITICAL, SEVERITY_MAJOR, SEVERITY_MINOR, SEVERITY_INFO);

  private String key;
  private String componentKey;
  private String ruleKey;
  private String ruleRepositoryKey;
  private String severity;
  private String title;
  private String message;
  private Integer line;
  private Double cost;
  private String status;
  private String resolution;
  private String userLogin;
  private String assigneeLogin;
  private Date createdAt;
  private Date updatedAt;
  private Date closedAt;
  private boolean manual = false;
  private String checksum;
  private boolean isNew = true;
  private Map<String, String> attributes = null;

  public String key() {
    return key;
  }

  public DefaultIssue setKey(String key) {
    this.key = key;
    return this;
  }

  public String componentKey() {
    return componentKey;
  }

  public DefaultIssue setComponentKey(String componentKey) {
    this.componentKey = componentKey;
    return this;
  }

  public String ruleKey() {
    return ruleKey;
  }

  public DefaultIssue setRuleKey(String ruleKey) {
    this.ruleKey = ruleKey;
    return this;
  }

  public String ruleRepositoryKey() {
    return ruleRepositoryKey;
  }

  public DefaultIssue setRuleRepositoryKey(String ruleRepositoryKey) {
    this.ruleRepositoryKey = ruleRepositoryKey;
    return this;
  }

  public String severity() {
    return severity;
  }

  public DefaultIssue setSeverity(@Nullable String s) {
    Preconditions.checkArgument(s == null || SEVERITIES.contains(s), "Not a valid severity: " + s);
    this.severity = s;
    return this;
  }

  public String title() {
    return title;
  }

  public DefaultIssue setTitle(@Nullable String title) {
    this.title = title;
    return this;
  }

  public String message() {
    return message;
  }

  public DefaultIssue setMessage(@Nullable String message) {
    this.message = message;
    return this;
  }

  public Integer line() {
    return line;
  }

  public DefaultIssue setLine(@Nullable Integer l) {
    Preconditions.checkArgument(l == null || l > 0, "Line must be greater than zero (got " + l + ")");
    this.line = l;
    return this;
  }

  public Double cost() {
    return cost;
  }

  public DefaultIssue setCost(@Nullable Double c) {
    Preconditions.checkArgument(c == null || c >= 0, "Cost must be positive (got " + c + ")");
    this.cost = c;
    return this;
  }

  public String status() {
    return status;
  }

  public DefaultIssue setStatus(@Nullable String status) {
    this.status = status;
    return this;
  }

  public String resolution() {
    return resolution;
  }

  public DefaultIssue setResolution(@Nullable String resolution) {
    this.resolution = resolution;
    return this;
  }

  public String userLogin() {
    return userLogin;
  }

  public DefaultIssue setUserLogin(@Nullable String userLogin) {
    this.userLogin = userLogin;
    return this;
  }

  public String assigneeLogin() {
    return assigneeLogin;
  }

  public DefaultIssue setAssigneeLogin(@Nullable String assigneeLogin) {
    this.assigneeLogin = assigneeLogin;
    return this;
  }

  public Date createdAt() {
    return createdAt;
  }

  public DefaultIssue setCreatedAt(@Nullable Date d) {
    this.createdAt = d;
    return this;
  }

  public Date updatedAt() {
    return updatedAt;
  }

  public DefaultIssue setUpdatedAt(@Nullable Date d) {
    this.updatedAt = d;
    return this;
  }

  public Date closedAt() {
    return closedAt;
  }

  public DefaultIssue setClosedAt(@Nullable Date d) {
    this.closedAt = d;
    return this;
  }

  public boolean isManual() {
    return manual;
  }

  public DefaultIssue setManual(boolean b) {
    this.manual = b;
    return this;
  }

  public String getChecksum() {
    return checksum;
  }

  public DefaultIssue setChecksum(@Nullable String s) {
    this.checksum = s;
    return this;
  }

  public boolean isNew() {
    return isNew;
  }

  public DefaultIssue setNew(boolean b) {
    isNew = b;
    return this;
  }

  public String attribute(String key) {
    return attributes == null ? null : attributes.get(key);
  }

  public DefaultIssue setAttribute(String key, String value) {
    if (attributes == null) {
      attributes = Maps.newHashMap();
    }
    attributes.put(key, value);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultIssue that = (DefaultIssue) o;
    if (key != null ? !key.equals(that.key) : that.key != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return key != null ? key.hashCode() : 0;
  }
}