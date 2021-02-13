package thedrake;

import java.io.PrintWriter;

public enum PlayingSide implements JSONSerializable {
    ORANGE, BLUE;


    public String toJSONString() {
        return "\"" + this.toString() + "\"";
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf(toJSONString());
    }
}
