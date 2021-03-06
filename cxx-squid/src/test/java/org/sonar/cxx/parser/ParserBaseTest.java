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
package org.sonar.cxx.parser;

import static org.mockito.Mockito.mock;

import org.sonar.cxx.CxxConfiguration;
import org.sonar.squidbridge.SquidAstVisitorContext;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;

public class ParserBaseTest {

  protected CxxConfiguration conf = null;
  protected Parser<Grammar> p = null;
  protected Grammar g = null;

  public ParserBaseTest() {
    conf = new CxxConfiguration();
    conf.setErrorRecoveryEnabled(false);
    p = CxxParser.create(mock(SquidAstVisitorContext.class), conf);
    g = p.getGrammar();
  }
}
