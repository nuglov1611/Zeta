package core.reflection.functions;

import action.api.RTException;
import action.calc.OP;
import action.calc.functions.BaseExternFunction;

public class ROUND extends BaseExternFunction {
    public Object eval() throws Exception {
        Object o = OP.doHardOP(expr);
        if (!(o instanceof Double)) {
            throw new RTException("CastException", "ROUND");
        }
        return new Double(Math.round(((Double) o).doubleValue()));
    }
}
