package core.rml.dbi;

import loader.ZetaProperties;
import org.apache.log4j.Logger;

import java.sql.Types;
import java.util.List;

public class Sorter {
    protected final static Logger log = Logger.getLogger(Sorter.class);

    private int x, y, j, k, n;

    public static final int ASC = 1;

    public static final int DESC = -1;

    private int[] directions;

    private List<Integer> keys;

    private VMatrix vm;

    private int cols;

    public Sorter(VMatrix vm) {
        if (vm != null) {
            this.vm = vm;
        } else {
            log.debug("core.rml.dbi.Sorter.Sorter: VMatrix is null!");
            return;
        }

        this.keys = vm.getKeys();
        if (keys == null) {
            log.debug("core.rml.dbi.Sorter.Sorter: keys is null!");
            return;
        }
        this.directions = vm.getDirections();
        if (directions == null) {
            log.debug("core.rml.dbi.Sorter.Sorter: directions is null!");
            return;
        }

        n = keys.size();
        cols = directions.length;

    }

    public List<Integer> getSortedArray() {
        if (ZetaProperties.dstore_debug > 0) {
            log.debug("core.rml.dbi.Sorter.getSortedArray  called");
        }
        if (vm == null) {
            return null;
        } else if (n == 0) {
            return keys;
        }
        k = n;
        for (j = n / 2; j >= 1; j--) {
            x = j;
            pros();
        }
        for (k = n - 1; k >= 1; k--) {
            swap(keys, 1 - 1, k + 1 - 1);
            x = 1;
            pros();
        }
        return keys;
    }

    private void pros() {
        while (true) {
            y = x + x;
            switch (sign(y - k) + 2) {
                case 1: {
                    if (compareRows(y - 1, y + 1 - 1, directions) < 0) {
                        y++;
                    }
                }
                case 2: {
                    if (compareRows(x - 1, y - 1, directions) >= 0) {
                        return;
                    }
                    swap(keys, x - 1, y - 1);
                    x = y;
                    break;
                }
                case 3: {
                    return;
                }
            }
        }
    }

    private int sign(int x) {
        if (x == 0) {
            return 0;
        }
        return x < 0 ? -1 : 1;
    }

    private void swap(List<Integer> s, int x, int y) {
        int t = s.get(x);
        s.set(x, s.get(y));
        s.set(y, t);
    }

    public int compareRows(int r1, int r2, int[] directions) {
        try {
            for (int i = 0; i < cols; i++) {
                int ret = compareTo(vm.get(r1, i), vm.get(r2, i), vm.getType(i));
                if ((ret != 0) || (i == cols - 1)) {
                    return ret * directions[i];
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return 0;

    }

    public int compareTo(Object o1, Object o2, int type) {
        try {
            if (o1 == null || o2 == null) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return -1;
                } else {
                    return 1;
                }
            }
            switch (type) {
                case Types.NUMERIC:
                case Types.INTEGER:
                case Types.FLOAT:
                case Types.REAL: {
                    if (((Double) o1).doubleValue() == ((Double) o2).doubleValue()) {
                        return 0;
                    }
                    if (((Double) o1).doubleValue() < ((Double) o2).doubleValue()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                case Types.CHAR:
                case Types.VARCHAR:
                case -8: {
                    return ((String) o1).compareTo((String) o2);
                }
                case Types.DATE:
                case Types.TIMESTAMP: {
                    java.util.Date d1 = (java.util.Date) o1;
                    java.util.Date d2 = (java.util.Date) o2;
                    if (d1.equals(d2)) {
                        return 0;
                    }
                    if (d1.before(d2)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                default: {
                    log.debug("UNKNOWN TYPE!!!");
                    return 0;
                }
            }
        } catch (NullPointerException e) {
            //e.printStackTrace();
            if (o1 == null) {
                return -1;
            } else {
                return 1;
            }
        } catch (Exception e1) {
            log.error("core.rml.dbi.Sorter.CompareTo:", e1);
            return 0;
        }

    }

}
