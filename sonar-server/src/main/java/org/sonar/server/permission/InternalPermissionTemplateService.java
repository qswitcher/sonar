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

package org.sonar.server.permission;

import com.google.common.collect.Lists;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ServerComponent;
import org.sonar.core.permission.PermissionTemplateDao;
import org.sonar.core.permission.PermissionTemplateDto;
import org.sonar.core.user.UserDao;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ServerErrorException;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Used by ruby code <pre>Internal.permission_templates</pre>
 */
public class InternalPermissionTemplateService implements ServerComponent {

  private static final Logger LOG = LoggerFactory.getLogger(InternalPermissionTemplateService.class);

  private final PermissionTemplateDao permissionTemplateDao;
  private final UserDao userDao;

  public InternalPermissionTemplateService(PermissionTemplateDao permissionTemplateDao, UserDao userDao) {
    this.permissionTemplateDao = permissionTemplateDao;
    this.userDao = userDao;
  }

  @CheckForNull
  public PermissionTemplate selectPermissionTemplate(String templateName) {
    PermissionTemplateUpdater.checkSystemAdminUser();
    PermissionTemplateDto permissionTemplateDto = permissionTemplateDao.selectPermissionTemplate(templateName);
    return PermissionTemplate.create(permissionTemplateDto);
  }

  public List<PermissionTemplate> selectAllPermissionTemplates() {
    return selectAllPermissionTemplates(null);
  }

  public List<PermissionTemplate> selectAllPermissionTemplates(@Nullable String componentKey) {
    PermissionTemplateUpdater.checkProjectAdminUser(componentKey);
    List<PermissionTemplate> permissionTemplates = Lists.newArrayList();
    List<PermissionTemplateDto> permissionTemplateDtos = permissionTemplateDao.selectAllPermissionTemplates();
    if (permissionTemplateDtos != null) {
      for (PermissionTemplateDto permissionTemplateDto : permissionTemplateDtos) {
        permissionTemplates.add(PermissionTemplate.create(permissionTemplateDto));
      }
    }
    return permissionTemplates;
  }

  public PermissionTemplate createPermissionTemplate(String name, @Nullable String description, @Nullable String keyPattern) {
    PermissionTemplateUpdater.checkSystemAdminUser();
    validateTemplateName(null, name);
    validateKeyPattern(null, keyPattern);
    PermissionTemplateDto permissionTemplateDto = permissionTemplateDao.createPermissionTemplate(name, description, keyPattern);
    if (permissionTemplateDto.getId() == null) {
      String errorMsg = "Template creation failed";
      LOG.error(errorMsg);
      throw new ServerErrorException(errorMsg);
    }
    return PermissionTemplate.create(permissionTemplateDto);
  }

  public void updatePermissionTemplate(Long templateId, String newName, @Nullable String newDescription, @Nullable String newKeyPattern) {
    PermissionTemplateUpdater.checkSystemAdminUser();
    validateTemplateName(templateId, newName);
    validateKeyPattern(templateId, newKeyPattern);
    permissionTemplateDao.updatePermissionTemplate(templateId, newName, newDescription, newKeyPattern);
  }

  public void deletePermissionTemplate(Long templateId) {
    PermissionTemplateUpdater.checkSystemAdminUser();
    permissionTemplateDao.deletePermissionTemplate(templateId);
  }

  public void addUserPermission(String templateName, String permission, String userLogin) {
    PermissionTemplateUpdater updater = new PermissionTemplateUpdater(templateName, permission, userLogin, permissionTemplateDao, userDao) {
      @Override
      protected void doExecute(Long templateId, String permission) {
        Long userId = getUserId();
        permissionTemplateDao.addUserPermission(templateId, userId, permission);
      }
    };
    updater.executeUpdate();
  }

  public void removeUserPermission(String templateName, String permission, String userLogin) {
    PermissionTemplateUpdater updater = new PermissionTemplateUpdater(templateName, permission, userLogin, permissionTemplateDao, userDao) {
      @Override
      protected void doExecute(Long templateId, String permission) {
        Long userId = getUserId();
        permissionTemplateDao.removeUserPermission(templateId, userId, permission);
      }
    };
    updater.executeUpdate();
  }

  public void addGroupPermission(String templateName, String permission, String groupName) {
    PermissionTemplateUpdater updater = new PermissionTemplateUpdater(templateName, permission, groupName, permissionTemplateDao, userDao) {
      @Override
      protected void doExecute(Long templateId, String permission) {
        Long groupId = getGroupId();
        permissionTemplateDao.addGroupPermission(templateId, groupId, permission);
      }
    };
    updater.executeUpdate();
  }

  public void removeGroupPermission(String templateName, String permission, String groupName) {
    PermissionTemplateUpdater updater = new PermissionTemplateUpdater(templateName, permission, groupName, permissionTemplateDao, userDao) {
      @Override
      protected void doExecute(Long templateId, String permission) {
        Long groupId = getGroupId();
        permissionTemplateDao.removeGroupPermission(templateId, groupId, permission);
      }
    };
    updater.executeUpdate();
  }

  private void validateTemplateName(@Nullable Long templateId, String templateName) {
    if (StringUtils.isNullOrEmpty(templateName)) {
      String errorMsg = "Name can't be blank";
      throw new BadRequestException(errorMsg);
    }
    List<PermissionTemplateDto> existingTemplates = permissionTemplateDao.selectAllPermissionTemplates();
    if (existingTemplates != null) {
      for (PermissionTemplateDto existingTemplate : existingTemplates) {
        if ((templateId == null || !existingTemplate.getId().equals(templateId)) && (existingTemplate.getName().equals(templateName))) {
          String errorMsg = "A template with that name already exists";
          throw new BadRequestException(errorMsg);
        }
      }
    }
  }

  private void validateKeyPattern(@Nullable Long templateId, @Nullable String keyPattern) {
    if (StringUtils.isNullOrEmpty(keyPattern)) {
      return;
    }
    try {
      Pattern.compile(keyPattern);
    } catch (PatternSyntaxException e) {
      String errorMsg = "Invalid pattern: " + keyPattern + ". Should be a valid Java regular expression.";
      throw new BadRequestException(errorMsg);
    }
  }

}
