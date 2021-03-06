/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.glsp.server.features.core.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.server.jsonrpc.GraphGsonConfiguratorFactory;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.protocol.GLSPServerException;
import org.eclipse.glsp.server.utils.ClientOptions;

import com.google.gson.Gson;
import com.google.inject.Inject;

/**
 * A source model loader that reads the graph model directly from a JSON file.
 */
public class JsonFileGModelLoader implements ModelSourceLoader {

   @Inject
   private GraphGsonConfiguratorFactory gsonConfigurationFactory;

   @Override
   public void loadSourceModel(final RequestModelAction action, final GModelState modelState) {
      final Optional<File> file = ClientOptions.getSourceUriAsFile(action.getOptions());
      try (Reader reader = new InputStreamReader(new FileInputStream(file.get()), StandardCharsets.UTF_8)) {
         Gson gson = gsonConfigurationFactory.configureGson().create();
         modelState.setRoot(gson.fromJson(reader, GGraph.class));
         modelState.getRoot().setRevision(-1);
      } catch (IOException e) {
         throw new GLSPServerException("Could not load model from file: " + file.get().toURI().toString(), e);
      }
   }

}
