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
package org.jboss.libra.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

import org.jboss.libra.LibraVisitor;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ReflectionLibraVisitor implements LibraVisitor {
    //public static final ReflectionLibraVisitor INSTANCE = new ReflectionLibraVisitor();

    @Override
    public Long visit(Visitor<Long, Object> visitor, Object obj) throws VisitationException {
        return visit(visitor, obj, new LinkedList<Object>());
    }
    
    
    private Long visit(Visitor<Long, Object> visitor, Object obj, LinkedList<Object> parentChine) throws VisitationException {
        final Class<?> cls = obj.getClass();
        long size = visitor.visit(obj);
        try {
            Field declaredFields[] = cls.getDeclaredFields();
            for (Field field : declaredFields) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;
                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                if (field.getType().isPrimitive()) {
                    size += visitor.visit(fieldValue);
                } else if (fieldValue instanceof String) {
                    size += ((String) fieldValue).length();
                } else if (fieldValue != null) {     
                    if (parentChine.contains(fieldValue)) { 
                        continue;
                    } else {
                        parentChine.add(fieldValue);
                        size += visit(visitor, fieldValue, parentChine);
                    }
                } 
            }
            return size;
        } catch (IllegalAccessException e) {
            throw new VisitationException(e);
        } catch (StackOverflowError e) {
            throw new VisitationException(e);
        }
    }
}
