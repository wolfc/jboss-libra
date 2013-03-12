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

import org.jboss.libra.util.ReflectionLibraVisitor;
import org.jboss.libra.util.VisitationException;

import java.lang.instrument.Instrumentation;

/**
 * The Scales of Astraea with which objects can be weighed.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Libra {
    private static volatile Instrumentation globalInstr;

    public static void agentmain(String args, Instrumentation inst) {
        premain(args, inst);
    }

    /**
     * Returns an implementation-specific approximation of the amount of storage consumed by
     * the specified object graph.
     *
     * The method traverses each object in the <i>object graph</i> once and aggregates the
     * amount of storage consumed by these objects.
     *
     * @see Instrumentation#getObjectSize(Object)
     * @param obj the object graph starting point to size
     * @return an implementation-specific approximation of the amount of storage consumed by the specified object
     */
    public static long getDeepObjectSize(Object obj) throws LibraException {
        return getDeepObjectSize(obj, ReflectionLibraVisitor.INSTANCE);
    }

    public static long getDeepObjectSize(Object obj, LibraVisitor visitor) throws LibraException {
        try {
            return visitor.visit(LibraObjectVisitor.INSTANCE, obj);
        } catch (VisitationException e) {
            throw new LibraException(e);
        }
    }

    /**
     * @see Instrumentation#getObjectSize(Object) 
     * @param obj the object to size
     * @return an implementation-specific approximation of the amount of storage consumed by the specified object
     */
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
