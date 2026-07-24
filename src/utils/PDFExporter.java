package utils;

import java.text.MessageFormat;
import javax.swing.JTable;

public class PDFExporter {

    public static boolean printTable(JTable table, String headerTitle, String footerFormat) {
        try {
            MessageFormat header = new MessageFormat(headerTitle);
            MessageFormat footer = new MessageFormat(footerFormat + " | Page {0}");
            return table.print(JTable.PrintMode.FIT_WIDTH, header, footer, true, null, true, null);
        } catch (Exception e) {
            System.err.println("Failed to print table to PDF");
            e.printStackTrace();
            return false;
        }
    }
}
