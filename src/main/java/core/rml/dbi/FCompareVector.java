package core.rml.dbi;

class FCompareVector implements FCompare {

    public boolean compareB(Object o1, Object o2) {

        if (o1.toString().compareTo(o2.toString()) > 0) {
            return true;
        }
        return false;
    }

    public boolean compareL(Object o1, Object o2) {
        if (o1.toString().compareTo(o2.toString()) < 0) {
            return true;
        }
        return false;
    }
}
