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
package org.sonar.cxx.checks;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.cxx.visitors.CxxCharsetAwareVisitor;
import org.sonar.squidbridge.checks.SquidCheck;
import com.google.common.io.Files;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import java.nio.charset.CharsetEncoder;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.NoSqale;

@Rule(
  key = "FileEncoding",
  name = "Verify that all characters of the file can be encoded with the predefined charset.",
  priority = Priority.MINOR)
@ActivatedByDefault
@NoSqale
public class FileEncodingCheck extends SquidCheck<Grammar> implements CxxCharsetAwareVisitor {

  private Charset charset;

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  @Override
  public void visitFile(AstNode astNode) {
    List<String> lines;
    try {
      lines = Files.readLines(getContext().getFile(), charset);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    CharsetEncoder encoder = Charset.forName(charset.name()).newEncoder();
    for (String line : lines) {
      if (!encoder.canEncode(line)) {
        getContext().createFileViolation(this, "Not all characters of the file can be encoded with the predefined charset " + charset.name() + ".");
        break;
      }
    }
  }

}
