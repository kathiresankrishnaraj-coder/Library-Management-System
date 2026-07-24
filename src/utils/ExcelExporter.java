package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ExcelExporter {

    public static boolean exportToCSV(JTable table, File file) {
        try {
            TableModel model = table.getModel();
            FileWriter csv = new FileWriter(file);

            // Write Headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(escapeCSV(model.getColumnName(i)));
                if (i < model.getColumnCount() - 1) {
                    csv.write(",");
                }
            }
            csv.write("\n");

            // Write Data Row by Row
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object val = model.getValueAt(i, j);
                    csv.write(escapeCSV(val == null ? "" : val.toString()));
                    if (j < model.getColumnCount() - 1) {
                        csv.write(",");
                    }
                }
                csv.write("\n");
            }

            csv.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String escapeCSV(String value) {
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
