package com.parsleyj.toolparser.program;

import com.parsleyj.toolparser.configuration.Configuration;

/**
 * Functional interface used to define how a {@link Program} makes
 * a computational step.
 */
@FunctionalInterface
public interface ProgramExecutionMethod {
    boolean step(Program program, Configuration configuration);
}
