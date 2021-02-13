package thedrake;

import java.io.PrintWriter;

public enum TroopFace implements JSONSerializable{
    AVERS,REVERS;

    public String toJSONString() {
        return "\"" + this.toString() + "\"";
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf(toJSONString());
    }
}
