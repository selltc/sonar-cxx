/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2011 Waleri Enns and CONTACT Software GmbH
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.cxx;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.squidbridge.api.SquidConfiguration;

public class CxxConfiguration extends SquidConfiguration {

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("CxxConfiguration");
  public static final String OverallIncludeKey = "CxxOverallInclude";
  public static final String OverallDefineKey = "CxxOverallDefine";

  private boolean ignoreHeaderComments = false;
  private final HashMap<String, List<String>> uniqueIncludes = new HashMap<>();
  private final HashMap<String, Set<String>> uniqueDefines = new HashMap<>();
  private List<String> forceIncludeFiles = new ArrayList<>();
  private List<String> headerFileSuffixes = new ArrayList<>();
  private String baseDir;
  private boolean errorRecoveryEnabled = true;
  private List<String> cFilesPatterns = new ArrayList<>();
  private boolean missingIncludeWarningsEnabled = true;
  private ResourcePerspectives perspectives;
  private FileSystem fs;

  private final CxxVCppBuildLogParser cxxVCppParser;

  public CxxConfiguration() {
    uniqueIncludes.put(OverallIncludeKey, new ArrayList<String>());
    uniqueDefines.put(OverallDefineKey, new HashSet<String>());
    cxxVCppParser = new CxxVCppBuildLogParser(uniqueIncludes, uniqueDefines);
  }

  public CxxConfiguration(Charset encoding) {
    super(encoding);
    uniqueIncludes.put(OverallIncludeKey, new ArrayList<String>());
    uniqueDefines.put(OverallDefineKey, new HashSet<String>());
    cxxVCppParser = new CxxVCppBuildLogParser(uniqueIncludes, uniqueDefines);
  }

  public CxxConfiguration(FileSystem fs) {
    super(fs.encoding());
    this.fs = fs;
    uniqueIncludes.put(OverallIncludeKey, new ArrayList<String>());
    uniqueDefines.put(OverallDefineKey, new HashSet<String>());
    cxxVCppParser = new CxxVCppBuildLogParser(uniqueIncludes, uniqueDefines);
  }

  public CxxConfiguration(FileSystem fs,
    ResourcePerspectives perspectivesIn) {
    super(fs.encoding());
    this.fs = fs;
    perspectives = perspectivesIn;
    uniqueIncludes.put(OverallIncludeKey, new ArrayList<String>());
    uniqueDefines.put(OverallDefineKey, new HashSet<String>());
    cxxVCppParser = new CxxVCppBuildLogParser(uniqueIncludes, uniqueDefines);
  }

  public void setIgnoreHeaderComments(boolean ignoreHeaderComments) {
    this.ignoreHeaderComments = ignoreHeaderComments;
  }

  public boolean getIgnoreHeaderComments() {
    return ignoreHeaderComments;
  }

  public void setDefines(List<String> defines) {
    Set<String> overallDefs = uniqueDefines.get(OverallDefineKey);
    for (String define : defines) {
      if (!overallDefs.contains(define)) {
        overallDefs.add(define);
      }
    }
  }

  public void setDefines(String[] defines) {
    if (defines != null) {
      setDefines(Arrays.asList(defines));
    }
  }

  public List<String> getDefines() {
    Set<String> allDefines = new HashSet<>();

    for (Set<String> elemSet : uniqueDefines.values()) {
      for (String value : elemSet) {
        if (!allDefines.contains(value)) {
          allDefines.add(value);
        }
      }
    }

    return new ArrayList<>(allDefines);
  }

  public void setIncludeDirectories(List<String> includeDirectories) {
    List<String> overallIncludes = uniqueIncludes.get(OverallIncludeKey);
    for (String include : includeDirectories) {
      if (!overallIncludes.contains(include)) {
        LOG.debug("setIncludeDirectories() adding dir '{}'", include);
        overallIncludes.add(include);
      }
    }
  }

  public void setIncludeDirectories(String[] includeDirectories) {
    if (includeDirectories != null) {
      setIncludeDirectories(Arrays.asList(includeDirectories));
    }
  }

  public List<String> getIncludeDirectories() {
    List<String> allIncludes = new ArrayList<>();

    for (List<String> elemList : uniqueIncludes.values()) {
      for (String value : elemList) {
        if (!allIncludes.contains(value)) {
          allIncludes.add(value);
        }
      }
    }

    return allIncludes;
  }

  public void setForceIncludeFiles(List<String> forceIncludeFiles) {
    this.forceIncludeFiles = forceIncludeFiles;
  }

  public void setForceIncludeFiles(String[] forceIncludeFiles) {
    if (forceIncludeFiles != null) {
      setForceIncludeFiles(Arrays.asList(forceIncludeFiles));
    }
  }

  public List<String> getForceIncludeFiles() {
    return forceIncludeFiles;
  }

  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }

  public String getBaseDir() {
    return baseDir;
  }

  public void setErrorRecoveryEnabled(boolean errorRecoveryEnabled) {
    this.errorRecoveryEnabled = errorRecoveryEnabled;
  }

  public boolean getErrorRecoveryEnabled() {
    return this.errorRecoveryEnabled;
  }

  public List<String> getCFilesPatterns() {
    return cFilesPatterns;
  }

  public void setCFilesPatterns(String[] cFilesPatterns) {
    if (this.cFilesPatterns != null) {
      this.cFilesPatterns = Arrays.asList(cFilesPatterns);
    }
  }

  public void setHeaderFileSuffixes(List<String> headerFileSuffixes) {
    this.headerFileSuffixes = headerFileSuffixes;
  }

  public void setHeaderFileSuffixes(String[] headerFileSuffixes) {
    if (headerFileSuffixes != null) {
      setHeaderFileSuffixes(Arrays.asList(headerFileSuffixes));
    }
  }

  public List<String> getHeaderFileSuffixes() {
    return this.headerFileSuffixes;
  }

  public void setMissingIncludeWarningsEnabled(boolean enabled) {
    this.missingIncludeWarningsEnabled = enabled;
  }

  public boolean getMissingIncludeWarningsEnabled() {
    return this.missingIncludeWarningsEnabled;
  }

  public void setCompilationPropertiesWithBuildLog(List<File> reports,
    String fileFormat,
    String charsetName) {

    if (reports == null) {
      return;
    }

    for (File buildLog : reports) {
      if (buildLog.exists()) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Parse build log  file '{}'", buildLog.getAbsolutePath());
        }
        if (fileFormat.equals("Visual C++")) {
          cxxVCppParser.parseVCppLog(buildLog, baseDir, charsetName);
        }

        LOG.debug("Parse build log OK: includes: '{}' defines: '{}'", uniqueIncludes.size(), uniqueDefines.size());
      } else {
        LOG.error("Compilation log not found: '{}'", buildLog.getAbsolutePath());
      }
    }
  }
}
