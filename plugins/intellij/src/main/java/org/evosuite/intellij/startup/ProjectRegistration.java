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

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.evosuite.intellij.EvoParameters;
import org.evosuite.intellij.IntelliJNotifier;
import org.evosuite.intellij.StopEvoAction;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point for the IntelliJ plugin for when projects are opened/closed
 * <p>
 * <p/>
 * Created by arcuri on 9/9/14.
 */
public class ProjectRegistration implements StartupActivity {

    @Override
    public void runActivity(@NotNull final Project project) {

        Disposer.register(
                project,
                () -> {
                    EvoParameters.getInstance().save(project);
                    ComponentContainer console = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
                    if (console != null) {
                        console.dispose();
                    }
                }
        );

        EvoParameters.getInstance().load(project);

        ActionManager am = ActionManager.getInstance();

        //create the tool window, which will appear in the bottom when an EvoSuite run is started
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.registerToolWindow("EvoSuite", false, ToolWindowAnchor.BOTTOM);
        toolWindow.setTitle("EvoSuite Console Output");
        toolWindow.setType(ToolWindowType.DOCKED, null);


        //create a console panel
        ConsoleViewImpl console = (ConsoleViewImpl) TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JComponent consolePanel = console.getComponent();


        IntelliJNotifier notifier = IntelliJNotifier.registerNotifier(project, "EvoSuite Plugin", console);

        //create left-toolbar with stop button
        DefaultActionGroup buttonGroup = new DefaultActionGroup();
        buttonGroup.add(new StopEvoAction(notifier));
        ActionToolbar viewToolbar = am.createActionToolbar("EvoSuite.ConsoleToolbar", buttonGroup, false);
        JComponent toolBarPanel = viewToolbar.getComponent();


        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(toolBarPanel, BorderLayout.WEST);
        panel.add(consolePanel, BorderLayout.CENTER);


        //Content content = contentFactory.createContent(consolePanel, "", false);
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

}