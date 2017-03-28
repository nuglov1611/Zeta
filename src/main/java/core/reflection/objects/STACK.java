package core.reflection.objects;

import action.api.HaveMethod;
import action.api.RTException;
import action.calc.OP;
import action.calc.objects.class_constructor;
import action.calc.objects.class_type;

import java.util.Vector;

public class STACK implements class_constructor, HaveMethod, class_type {

    class StackElement {

        StackElement next;
        Object value;

        public StackElement(Object value, StackElement next) {
            this.next = next;
            this.value = value;
        }
    }

    StackElement stack = null;

    public STACK() {
    }

    public Object constructor(Object arg) throws Exception {
        return this;
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("PUSH")) {
            if (arg instanceof Vector) {
                throw new RTException("CastException",
                        "STACK@PUSH must called with one argument");
            }
            stack = new StackElement(arg, stack);
            return new Double(1);
        } else if (method.equals("POP")) {
            if (stack == null) {
                throw new RTException("EmptyStackException", "Stack is Empty");
            }
            Object o = stack.value;
            stack = stack.next;
            return o;
        } else if (method.equals("EMPTY")) {
            return (stack == null) ? new Double(1) : new Double(0);
        } else {
            throw new RTException("HasMethodException",
                    "object STACK has not method " + method);
        }
    }

    public String toString() {
        StackElement s = stack;
        StringBuilder sb = new StringBuilder("<");
        while (s != null) {
            if (s.value instanceof Object[]) {
                sb.append(OP.printArray((Object[]) s.value));
            } else {
                sb.append(s.value);
            }
            sb.append(", ");
            s = s.next;
        }
        sb.append("@>");
        return sb.toString();
    }

    public String type() throws Exception {
        return "STACK";
    }
}
