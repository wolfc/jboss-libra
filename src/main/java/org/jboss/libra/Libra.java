/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.libra;

import java.lang.instrument.Instrumentation;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Libra {
    private static volatile Instrumentation globalInstr;

    public static void agentmain(String args, Instrumentation inst) {
        premain(args, inst);
    }

    public static long getObjectSize(Object obj) {
        if (globalInstr == null) {
            LibraAttacher.attach();
            if (globalInstr == null) {
                throw new IllegalStateException("Agent not set");
            }
        }
        return globalInstr.getObjectSize(obj);
    }

    public static void premain(String args, Instrumentation inst) {
        globalInstr = inst;
        System.setProperty(Libra.class.getName() + ".installed", "true");
    }
}
