/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.intellij.startup;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.evosuite.intellij.EvoAction;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point for the IntelliJ plugin for when IntelliJ starts
 * <p>
 * Created by arcuri on 11/1/14.
 */
public class ApplicationRegistration implements StartupActivity {

    @Override
    public synchronized void runActivity(@NotNull Project project) {
        EvoAction evo = new EvoAction();

        // Gets an instance of the WindowMenu action group.
        //DefaultActionGroup windowM = (DefaultActionGroup) am.getAction("WindowMenu");
        //this in the file editor, not the left-pane file selection
        //DefaultActionGroup editorM = (DefaultActionGroup) am.getAction("EditorPopupMenu");

        ActionManager am = ActionManager.getInstance();

        if (am.getAction("evosuite") == null) {
            am.registerAction("evosuite", evo);
            DefaultActionGroup pvM = (DefaultActionGroup) am.getAction("ProjectViewPopupMenu");
            pvM.addSeparator();
            pvM.add(evo);
            DefaultActionGroup epM = (DefaultActionGroup) am.getAction("EditorPopupMenu");
            epM.addSeparator();
            epM.add(evo);
        }
    }

}
