/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.cli.command.builtin;

import com.codenvy.cli.command.builtin.model.DefaultUserBuilderStatus;
import com.codenvy.cli.command.builtin.model.DefaultUserRunnerStatus;
import com.codenvy.cli.command.builtin.model.UserBuilderStatus;
import com.codenvy.cli.command.builtin.model.UserProject;
import com.codenvy.cli.command.builtin.model.UserRunnerStatus;
import com.codenvy.client.Codenvy;
import com.codenvy.client.model.BuilderState;
import com.codenvy.client.model.BuilderStatus;
import com.codenvy.client.model.Link;
import com.codenvy.client.model.RunnerState;
import com.codenvy.client.model.RunnerStatus;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to print the logs of a given runner/builder
 * @author Florent Benoit
 */
@Command(scope = "codenvy", name = "logs", description = "Show the logs of the given runner/builder ID")
public class LogsCommand extends AbsCommand {

    /**
     * Runner or builder ID
     */
    @Argument(name = "id", description = "Specify the runner/builder ID", required = true, multiValued = false)
    private String processID;

    /**
     * Execute the current command
     */
    protected Object doExecute() throws Exception {
        final Codenvy current = checkLoggedIn();

        // not logged in
        if (current == null) {
            return null;
        }

        // processId is beginning with a r --> runner ID
        if (processID.startsWith("r")) {
            displayRunnerLog(current);
        } else if (processID.startsWith("b")) {
            displayBuilderLog(current);
        } else {
            // invalid id
            Ansi buffer = Ansi.ansi();
            buffer.fg(Ansi.Color.RED);
            buffer.a("Invalid identifier");
            buffer.fg(Ansi.Color.DEFAULT);
            System.out.println(buffer.toString());
        }

        return null;
    }

    /**
     * Display the runner log
     * @param current
     */
    protected void displayRunnerLog(Codenvy current) {


        // first collect all processes
        List<UserProject> projects = getProjects(current);

        List<UserRunnerStatus> matchingStatuses = new ArrayList<>();

        // then for each project, gets the process IDs
        for (UserProject userProject : projects) {
            final List<? extends RunnerStatus> runnerStatuses = current.runner().processes(userProject.getInnerProject()).execute();
            for (RunnerStatus runnerStatus : runnerStatuses) {

                UserRunnerStatus tmpStatus = new DefaultUserRunnerStatus(runnerStatus, userProject);
                if (tmpStatus.shortId().startsWith(processID)) {
                    matchingStatuses.add(tmpStatus);
                }
            }
        }

        if (matchingStatuses.size() == 0) {
            errorNoIdentifier("runner");
            return;
        } else if (matchingStatuses.size() > 1) {
            errorTooManyIdentifiers("runners");
            return;
        }

        // only one matching status
        UserRunnerStatus foundStatus = matchingStatuses.get(0);

        RunnerState state = foundStatus.getInnerStatus().status();

        // not in a valid state
        if (state != RunnerState.RUNNING && state != RunnerState.STOPPED) {
            Ansi buffer = Ansi.ansi();
            buffer.fg(Ansi.Color.RED);
            buffer.a("Logs are only available in RUNNING or STOPPED state. Current state is ").a(state);
            buffer.fg(Ansi.Color.DEFAULT);
            System.out.println(buffer.toString());
            return;
        }


        // Now, print the log
        String log = current.runner().logs(foundStatus.getProject().getInnerProject(), foundStatus.getInnerStatus().processId()).execute();
        System.out.println(log);
    }

    /**
     * Display error if there are too many identifiers that have been found
     * @param text a description of the identifier
     */
    protected void errorTooManyIdentifiers(String text) {
        Ansi buffer = Ansi.ansi();
        buffer.fg(Ansi.Color.RED);
        buffer.a("Too many ").a(text).a(" have been found with identifier '").a(processID).a("'. Please add extra data to the identifier");
        buffer.fg(Ansi.Color.DEFAULT);
        System.out.println(buffer.toString());
    }

    /**
     * Display error if no identifier has been found
     * @param text a description of the identifier
     */
    protected void errorNoIdentifier(String text) {
        Ansi buffer = Ansi.ansi();
        buffer.fg(Ansi.Color.RED);
        buffer.a("No ").a(text).a(" found with identifier '").a(processID).a("'.");
        buffer.fg(Ansi.Color.DEFAULT);
        System.out.println(buffer.toString());
    }


    /**
     * Display the builder log.
     * @param current
     */
    protected void displayBuilderLog(Codenvy current) {


        // first collect all processes
        List<UserProject> projects = getProjects(current);

        List<UserBuilderStatus> matchingStatuses = new ArrayList<>();

        // then for each project, gets the builds IDs
        for (UserProject userProject : projects) {
            final List<? extends BuilderStatus> builderStatuses = current.builder().builds(userProject.getInnerProject()).execute();
            for (BuilderStatus builderStatus : builderStatuses) {

                UserBuilderStatus tmpStatus = new DefaultUserBuilderStatus(builderStatus, userProject);
                if (tmpStatus.shortId().startsWith(processID)) {
                    matchingStatuses.add(tmpStatus);
                }
            }
        }

        if (matchingStatuses.size() == 0) {
            errorNoIdentifier("builder");
            return;
        } else if (matchingStatuses.size() > 1) {
            errorTooManyIdentifiers("builders");
            return;
        }

        // only one matching status
        UserBuilderStatus foundStatus = matchingStatuses.get(0);

        BuilderState state = foundStatus.getInnerStatus().status();

        // not in a valid state
        if (state == BuilderState.IN_QUEUE) {
            Ansi buffer = Ansi.ansi();
            buffer.fg(Ansi.Color.RED);
            buffer.a("Logs are not available in IN_QUEUE state");
            buffer.fg(Ansi.Color.DEFAULT);
            System.out.println(buffer.toString());
            return;
        }


        // Now, print the log
        String log = current.builder().logs(foundStatus.getProject().getInnerProject(), foundStatus.getInnerStatus().taskId()).execute();
        System.out.println(log);

    }

}

