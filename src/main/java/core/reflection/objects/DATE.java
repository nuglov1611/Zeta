package core.reflection.objects;

import action.api.GlobalValuesObject;
import action.api.HaveMethod;
import action.api.RTException;
import action.calc.objects.class_constructor;
import action.calc.objects.class_type;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

public class DATE implements class_constructor, HaveMethod, class_type,
        GlobalValuesObject {

    Calendar dat;

    public DATE() {
    }

    public Object constructor(Object arg) throws Exception {
        if (arg instanceof Vector) {
            Vector v = (Vector) arg;
            Integer zn0 = (Integer) v.elementAt(0);
            Integer zn1 = (Integer) v.elementAt(1);
            Integer zn2 = (Integer) v.elementAt(2);
            dat = new GregorianCalendar((zn0).intValue(), (zn1).intValue(),
                    (zn2).intValue());
            return this;
        } else if (arg instanceof String) {
            if (arg.equals("")) {
                dat = new GregorianCalendar();
                return this;
            }
            SimpleDateFormat form = new SimpleDateFormat("dd-MM-yyyy");
            dat = new GregorianCalendar();
            dat.setTime(form.parse(arg.toString()));
            return this;
        } else if (arg instanceof Long) {
            dat = new GregorianCalendar();
            SimpleDateFormat form = new SimpleDateFormat();
            dat.setTime(form.parse(arg.toString()));
            return this;
        } else if (arg instanceof Date) {
            dat = new GregorianCalendar();
            // this.dat = (Date) arg;
            dat.setTime((Date) arg);
            return this;
        }
        throw new RTException("CastException",
                "format for DATA must to be Objects or String or Long");
    }

    public Object getValue() {
        return dat;
    }

    public Object getValueByName(String name) {
        return null;
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("TONUMBER")) {
            return new Double(dat.getTime().getTime());
        } else if (method.equals("TOSTRING")) {
            return dat.toString();
        } else if (method.equals("VALUE")) {
            return dat;
        } else if (method.equals("TOPLSTRING")) {
            String s;

            s = new Integer(dat.get(Calendar.DAY_OF_MONTH)).toString();
            s = s + '.' + new Integer(dat.get(Calendar.MONTH) + 1).toString();
            s = s + '.' + new Integer(dat.get(Calendar.YEAR)).toString();
            return s;
        } else if (method.equals("MM")) {
            return new Double(dat.get(Calendar.MONTH) + 1);
        } else if (method.equals("YYYY")) {
            return new Double(dat.get(Calendar.YEAR));
        } else if (method.equals("DD")) {
            return new Double(dat.get(Calendar.DAY_OF_MONTH));
        } else if (method.equals("EQUALS")) {
            Date d = null;
            if (arg instanceof Date) {
                d = (Date) arg;
            }
            if (arg instanceof DATE) {
                d = ((DATE) arg).dat.getTime();
            }
            boolean b = (dat.getTime()).equals(d);
            if (b) {
                return new Double(1);
            } else {
                return new Double(0);
            }
        } else if (method.equals("BEFORE")) {
            Date d = null;
            if (arg instanceof Date) {
                d = (Date) arg;
            }
            if (arg instanceof DATE) {
                d = ((DATE) arg).dat.getTime();
            }
            Calendar dt = Calendar.getInstance();
            dt.setTime(d);
            boolean b = dat.before(dt);
            if (b) {
                return new Double(1);
            } else {
                return new Double(0);
            }
        } else if (method.equals("AFTER")) {
            Date d = null;
            if (arg instanceof Date) {
                d = (Date) arg;
            }
            if (arg instanceof DATE) {
                d = ((DATE) arg).dat.getTime();
            }
            Calendar dt = Calendar.getInstance();
            dt.setTime(d);
            boolean b = dat.after(dt);
            if (b) {
                return new Double(1);
            } else {
                return new Double(0);
            }
        } else if (method.equals("MOVEBYSECOND")) {
            Double d = null;
            if (arg instanceof Double) {
                d = (Double) arg;
            }
            if (arg instanceof String) {
                try {
                    d = Double.valueOf((String) arg);
                } catch (NumberFormatException e) {
                    throw new RTException("BadFormatException",
                            "wrong string for type Double");
                }
            }
            dat.add(Calendar.SECOND, d.intValue());
            return this;
        } else if (method.equals("MOVEBYMINUTE")) {
            Double d = null;
            if (arg instanceof Double) {
                d = (Double) arg;
            }
            if (arg instanceof String) {
                try {
                    d = Double.valueOf((String) arg);
                } catch (NumberFormatException e) {
                    throw new RTException("BadFormatException",
                            "wrong string for type Double");
                }
            }
            dat.add(Calendar.MINUTE, d.intValue());
            return this;
        } else if (method.equals("MOVEBYHOUR")) {
            Double d = null;
            if (arg instanceof Double) {
                d = (Double) arg;
            }
            if (arg instanceof String) {
                try {
                    d = Double.valueOf((String) arg);
                } catch (NumberFormatException e) {
                    throw new RTException("BadFormatException",
                            "wrong string for type Double");
                }
            }
            dat.add(Calendar.HOUR, d.intValue());
            return this;
        } else if (method.equals("MOVEBYDAY")) {
            Double d = null;
            if (arg instanceof Double) {
                d = (Double) arg;
            }
            if (arg instanceof String) {
                try {
                    d = Double.valueOf((String) arg);
                } catch (NumberFormatException e) {
                    throw new RTException("BadFormatException",
                            "wrong string for type Double");
                }
            }
            dat.add(Calendar.HOUR, d.intValue());
            return this;
        } else if (method.equals("MOVEBYMONTH")) {
            Double d = null;
            if (arg instanceof Double) {
                d = (Double) arg;
            }
            if (arg instanceof String) {
                try {
                    d = Double.valueOf((String) arg);
                } catch (NumberFormatException e) {
                    throw new RTException("BadFormatException",
                            "wrong string for type Double");
                }
            }
            dat.add(Calendar.MONTH, d.intValue());
            return this;
        } else if (method.equals("MOVEBYYEAR")) {
            Double d = null;
            if (arg instanceof Double) {
                d = (Double) arg;
            }
            if (arg instanceof String) {
                try {
                    d = Double.valueOf((String) arg);
                } catch (NumberFormatException e) {
                    throw new RTException("BadFormatException",
                            "wrong string for type Double");
                }
            }
            dat.add(Calendar.YEAR, d.intValue());
            return this;
        } else {
            throw new RTException("HasMethodException",
                    "object DATA has not method " + method);
        }
    }

    public void setValue(Object o) {
        if (o instanceof Date) {
            dat.setTime((Date) o);
        }
    }

    public void setValueByName(String name, Object o) {
    }

    public String type() throws Exception {
        return "DATE";
    }

}
