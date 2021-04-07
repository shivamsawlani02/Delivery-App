package com.dsdairysystem.deliveryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.dsdairysystem.deliveryapp.route_tab.OrderModel;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthYearPickerDialog extends DialogFragment {

    private String[] month_name = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
    private static final int MAX_YEAR = 2099;
    private List<OrderModel> list;
    private int selected_month, selected_year;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    private int sumAmt = 0;
    Context context;
    Activity activity;
    private static final String TAG = "ClientDetails";
    private String custName, custMobile;
    Boolean share;
    private String exportType;

    public void setList(List<OrderModel> list, Context context,Activity activity, String custName, String custMobile, Boolean share, String exportType) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.custName = custName;
        this.custMobile = custMobile;
        this.share = share;
        this.exportType = exportType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_year_picker_dialog, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH));

        final int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(year);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selected_month = monthPicker.getValue();
                        selected_year = yearPicker.getValue();
                        if (share) {
                            if (exportType.equals("pdf")) {
                                try {
                                    createPdf();
                                } catch (FileNotFoundException e){
                                    e.printStackTrace();
                                } catch (DocumentException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    createExcel();
                                } catch (FileNotFoundException e){
                                    e.printStackTrace();
                                } catch (DocumentException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                createPdf();
                            } catch (FileNotFoundException e){
                                e.printStackTrace();
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/DSDairy");
        try {
            if(!docsFolder.isDirectory()) {
                docsFolder.mkdirs();
                Log.i(TAG, "Created a new directory for PDF");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        String pdfname = "orderDetails-"+custName+".pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{10, 10, 10, 10});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        //table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("Order");
        table.addCell("Quantity(Kg/L)");
        table.addCell("Date");
        table.addCell("Amount(Rs)");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        String orders = "", quantity = "";
        sumAmt = 0;
        for (int i = 0;i< list.size();i++) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(list.get(i).getTimestamp());
            int month =  calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            Log.d(TAG,"month number"+month+year);

            if ((month==selected_month) && (year==selected_year)) {
                orders = "";
                quantity="";
                ArrayList<String> stringArrayList = new ArrayList<>();
                stringArrayList.addAll(list.get(i).getMilk().keySet());
                ArrayList<Long> longArrayList = new ArrayList<>();
                longArrayList.addAll(list.get(i).getMilk().values());
                for (int c=0 ; c<stringArrayList.size(); c++){
                    orders += stringArrayList.get(c)+"\n";
                }
                for (int c=0 ; c<longArrayList.size(); c++){
                    quantity += longArrayList.get(c)+"\n";
                }
                table.addCell(orders);
                table.addCell(quantity);
                table.addCell(list.get(i).getDate());
                table.addCell(String.valueOf(list.get(i).getAmount()));
                sumAmt += list.get(i).getAmount();
            }
        }

        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("Total Amount - Rs "+sumAmt);

//        System.out.println("Done");
        PdfWriter.getInstance(document, output);
        document.open();
        Font f = new Font(Font.FontFamily.TIMES_ROMAN, 32.0f, Font.NORMAL, BaseColor.BLACK);
        Font g = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE);
        Font h = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLACK);

        Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        Paragraph paragraph = new Paragraph("Date: "+formattedDate);
        paragraph.setAlignment(Element.ALIGN_RIGHT);

        Paragraph paragraph1 = new Paragraph("Order Details - "+month_name[selected_month-1],h);
        paragraph1.setAlignment(Element.ALIGN_CENTER);

        document.add(new Paragraph("DS-Dairy-System", f));
        document.add(paragraph);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Client Name - "+custName,g));
        document.add(new Paragraph("Client Number - "+custMobile,g));
        document.add(Chunk.NEWLINE);
        document.add(paragraph1);
        document.add(Chunk.NEWLINE);
        document.add(table);


//        for (int i = 0; i < MyList1.size(); i++) {
//            document.add(new Paragraph(String.valueOf(MyList1.get(i))));
//        }
        document.close();
        if (share) sharePdf();
        else previewPdf();
    }
    private void previewPdf() {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List lists = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (lists.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Toast.makeText(activity, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePdf() {
        File outputFile = new File(Environment.getExternalStorageDirectory() + "/DSDairy/orderDetails-"+custName+".pdf");
        Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        share.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
        startActivity(Intent.createChooser(share, "Share File"));
    }

    private void createExcel() throws FileNotFoundException, DocumentException{
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("Order Details");
        HSSFRow row1 = firstSheet.createRow(0);
        HSSFCell cell1 = row1.createCell(4);
        cell1.setCellValue(new HSSFRichTextString("DS-DAIRY-SYSTEM"));

        Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        HSSFRow row2 = firstSheet.createRow(2);
        HSSFCell cell2 = row2.createCell(0);
        cell2.setCellValue(new HSSFRichTextString("Date: "+formattedDate));


        HSSFRow row3 = firstSheet.createRow(4);

        HSSFCell cell3 = row3.createCell(0);
        cell3.setCellValue(new HSSFRichTextString("Client Name: "+custName));

        HSSFRow row4 = firstSheet.createRow(5);

        HSSFCell cell4 = row4.createCell(0);
        cell4.setCellValue(new HSSFRichTextString("Client No.: "+custMobile));

        HSSFRow row8 = firstSheet.createRow(7);
        HSSFCell cell8 = row8.createCell(3);
        cell8.setCellValue(new HSSFRichTextString("Order Details - "+month_name[selected_month-1]));

        HSSFRow row5 = firstSheet.createRow(9);

        HSSFCell cell5 = row5.createCell(0);
        cell5.setCellValue(new HSSFRichTextString("Orders"));
        HSSFCell cell5i = row5.createCell(2);
        cell5i.setCellValue(new HSSFRichTextString("Quantity(kg/l)"));
        HSSFCell cell5o = row5.createCell(4);
        cell5o.setCellValue(new HSSFRichTextString("Date"));
        HSSFCell cell5p = row5.createCell(6);
        cell5p.setCellValue(new HSSFRichTextString("Amount(₹)"));

        String orders = "", quantity = "";
        sumAmt = 0;
        int count = 0;

        for (int i = 0;i< list.size();i++) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(list.get(i).getTimestamp());
            int month =  calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            Log.d(TAG,"month number"+month+year);

            if ((month==selected_month) && (year==selected_year)) {
                HSSFRow row6 = firstSheet.createRow(11+count);

                orders = "";
                quantity="";
                ArrayList<String> stringArrayList = new ArrayList<>();
                stringArrayList.addAll(list.get(count).getMilk().keySet());
                ArrayList<Long> longArrayList = new ArrayList<>();
                longArrayList.addAll(list.get(count).getMilk().values());
                for (int k=0 ; k<stringArrayList.size(); k++){
                    if (k==stringArrayList.size()-1) orders += stringArrayList.get(k);
                    else orders += stringArrayList.get(k)+",";
                }
                for (int k=0 ; k<longArrayList.size(); k++){
                    if (k==stringArrayList.size()-1) quantity += longArrayList.get(k);
                    else quantity += longArrayList.get(k)+",";
                }

                HSSFCell cell6 = row6.createCell(0);
                cell6.setCellValue(new HSSFRichTextString(orders));
                HSSFCell cell6i = row6.createCell(2);
                cell6i.setCellValue(new HSSFRichTextString(quantity));
                HSSFCell cell6o = row6.createCell(4);
                cell6o.setCellValue(new HSSFRichTextString(list.get(count).getDate()));
                HSSFCell cell6p = row6.createCell(6);
                cell6p.setCellValue(new HSSFRichTextString(String.valueOf(list.get(count).getAmount())));
                sumAmt += list.get(count).getAmount();
                count++;
            }

        }

        HSSFRow row7 = firstSheet.createRow(11+count);
        HSSFCell cell7 = row7.createCell(5);
        cell7.setCellValue(new HSSFRichTextString("Total Amount - Rs "+sumAmt));




        String fileName = "orderDetails-"+custName+".xls"; //Name of the file

        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        File folder = new File(extStorageDirectory+"/DSDairy", "Excel");// Name of the folder you want to keep your file in the local storage.
        folder.mkdir(); //creating the folder
        File file = new File(folder, fileName);
        try {
            file.createNewFile(); // creating the file inside the folder
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(file); //Opening the file
            workbook.write(fileOut); //Writing all your row column inside the file
            fileOut.close(); //closing the file and done

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        shareExcel();

    }

    private void shareExcel() {
        File outputFile = new File(Environment.getExternalStorageDirectory() + "/DSDairy/Excel/orderDetails-"+custName+".xls");
        Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/xls");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        share.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
        startActivity(Intent.createChooser(share, "Share File"));
    }


}
