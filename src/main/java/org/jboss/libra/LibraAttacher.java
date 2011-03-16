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

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
class LibraAttacher {
    private static final Logger log = Logger.getLogger(LibraAttacher.class.getName());

    static void attach() {
        for (VirtualMachineDescriptor virtualMachineDescriptor : VirtualMachine.list()) {
            try {
                VirtualMachine virtualMachine = virtualMachineDescriptor.provider().attachVirtualMachine(virtualMachineDescriptor);
                try {
                    String propertyName = Libra.class.getName() + ".installed";
                    Properties systemProperties = virtualMachine.getSystemProperties();
                    Properties agentProperties = virtualMachine.getAgentProperties();
                    String installed = agentProperties.getProperty(propertyName, systemProperties.getProperty(propertyName));
                    if(installed == null || Boolean.valueOf(installed) == false) {
                        String javaClassPath = systemProperties.getProperty("java.class.path");
                        String location = Libra.class.getProtectionDomain().getCodeSource().getLocation().getFile();
                        if(javaClassPath.contains(location)) {
                            virtualMachine.loadAgent(location, propertyName + "=true");
                            log.info("Libra installed in " + virtualMachineDescriptor);
                        }
                    } else {
                        log.info("Libra already installed in " + virtualMachineDescriptor);
                    }
                }
                finally {
                    virtualMachine.detach();
                }
            }
            catch(Exception e) {
                log.warning("Failed to install Libra into " + virtualMachineDescriptor + ": " + e.getMessage());
            }
        }
    }
}
