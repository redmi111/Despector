/*
 * The MIT License (MIT)
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.despector.emitter;

import org.objectweb.asm.Type;
import org.spongepowered.despector.ast.Annotation;

import java.util.List;

public class AnnotationEmitter {

    private void emitValue(EmitterContext ctx, Object value) {
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            if (list.isEmpty()) {
                ctx.printString("{}");
            } else if (list.size() == 1) {
                emitValue(ctx, list.get(0));
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (i != 0) {
                        ctx.printString(", ");
                    }
                    emitValue(ctx, list.get(i));
                }
            }
        } else if (value instanceof Type) {
            ctx.emitTypeName(((Type) value).getInternalName());
        } else {
            throw new IllegalStateException("Unknown annotation value type in emitter: " + value.getClass().getName());
        }
    }

    public void emit(EmitterContext ctx, Annotation annotation) {
        ctx.printString("@");
        ctx.emitType(annotation.getType().getName());
        if (annotation.getKeys().isEmpty()) {
            return;
        } else if (annotation.getKeys().size() == 1 && "value".equals(annotation.getKeys().iterator().next())) {
            ctx.printString("(");
            emitValue(ctx, annotation.getValue("value"));
            ctx.printString(")");
        } else {
            ctx.printString("(");
            boolean first = true;
            for (String key : annotation.getKeys()) {
                if (first) {
                    first = false;
                    ctx.printString(", ");
                }
                ctx.printString(key);
                ctx.printString(" = ");
                Object value = annotation.getValue(key);
                emitValue(ctx, value);
            }
            ctx.printString(")");
        }
    }

}